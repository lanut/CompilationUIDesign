package ui.menuIlems

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.alibaba.fastjson2.toJSONString
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import homework.grammatical.语法分析器
import homework.interpreter.Interpreter
import homework.interpreter.Variable
import homework.lexical.codeStringToTokenList
import homework.quaternionTranslators.QuaternionGeneration
import homework.quaternionTranslators.entity.QExpression
import kotlinx.coroutines.launch
import java.io.File

@Preview
@Composable
fun UIInterpreter() {
    // 记录输入或者读取的文本
    var text by remember { mutableStateOf("") }
    // 记录输入的值
    var input by remember { mutableStateOf("") }
    // 记录输出的值
    var outputText by remember { mutableStateOf("") }
    var interpreter by remember { mutableStateOf<Interpreter?>(null) }
    var qExpressions by remember { mutableStateOf(listOf<QExpression>()) }
    var index by remember { mutableStateOf(0) }
    // 全局变量表
    var globalVariableStore by remember { mutableStateOf(listOf<Variable>()) }
    // 普通变量表
    var variableStore by remember { mutableStateOf(listOf<Variable>()) }
    // 临时变量表
    var tempVariableStore by remember { mutableStateOf(listOf<Variable>()) }
    // 列表状态
    val listState = rememberLazyListState()
    // 协程作用域
    val coroutineScope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    var isEnd by remember { mutableStateOf(false) }


    Row {
        Column(modifier = Modifier.width(300.dp).padding(20.dp)) {
            // 左侧
            Row {
                // 打开文件
                var showPicker by remember { mutableStateOf(false) }
                Button(onClick = { showPicker = true }) {
                    Text("打开文件")
                }
                FilePicker(show = showPicker, fileExtensions = listOf("sample")) { mpFile ->
                    if (mpFile != null) {
                        showPicker = false
                        text = (mpFile.platformFile as File).readText()
                    }
                    showPicker = false
                }
                Spacer(modifier = Modifier.width(8.dp))
                // 执行分析
                Button(onClick = {
                    try {
                        val tokenJsStr = codeStringToTokenList(text).toJSONString()
                        val expressionStore = 语法分析器(tokenJsStr)
                        val syntaxTree = expressionStore.toTree().apply { tidy() }
                        val tempGeneration = QuaternionGeneration()
                        tempGeneration.程序(syntaxTree.root)
                        interpreter = Interpreter(tempGeneration, input)
                        qExpressions = interpreter?.stateStore?.lastOrNull()?.qExpressionList?.Myclone() ?: listOf()
                        index = interpreter?.stateStore?.lastOrNull()?.executeIndex ?: 0
                        globalVariableStore = interpreter?.globalVariableStore ?: listOf()
                        variableStore = interpreter?.stateStore?.lastOrNull()?.variableStore ?: listOf()
                        tempVariableStore = interpreter?.stateStore?.lastOrNull()?.tempVariableStore ?: listOf()
                    } catch (e: Exception) {
                        showDialog = true
                        dialogMessage = e.stackTraceToString()
                    }
                    isEnd = false
                    outputText = ""
                }) {
                    Text("执行分析")
                }
            }
            // 输入的程序文本
            TextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("请输入程序文本") },
                modifier = Modifier.padding(8.dp),
                // maxLines = 7
            )
        }

        Column(modifier = Modifier.width(450.dp).padding(20.dp)) {
            // 右侧
            // 状态名
            if (interpreter != null) {
                Text("状态名: ${interpreter?.stateStore?.lastOrNull()?.stateName}")
                if (isEnd) {
                    Text("程序执行完毕，请重新执行分析")
                }
                // 变量表和临时变量表
                Row(Modifier.height(180.dp)) {
                    OutlinedCard(Modifier.width(110.dp).fillMaxHeight()) {
                        Column(Modifier.padding(8.dp)) {
                            Text("普通变量表")
                            Box(Modifier.height(100.dp)) {
                                showVariable(variableStore)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedCard(Modifier.fillMaxSize()) {
                        Column(Modifier.padding(8.dp)) {
                            Text("全局变量表")
                            Box(Modifier.height(50.dp)) {
                                showVariable(globalVariableStore)
                            }
                            Text("临时变量表")
                            showVariable(tempVariableStore)
                        }
                    }
                }
            }

            // 输入重定向
            OutlinedTextField(
                value = input,
                onValueChange = {
                    input = it
                    interpreter?.inputDirection = it.let {
                        // 如果输入为空字符串或只包含空白，则返回空列表
                        if (input.isBlank()) {
                            emptyList<String>().toMutableList()
                        } else {
                            input.split("\\s+".toRegex()).toMutableList()
                        }
                    }
                },
                label = { Text("输入重定向") },
                maxLines = 1,
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            )

            // 输出重定向
            TextField(
                value = outputText,
                onValueChange = {},
                label = { Text("输出重定向") },
                readOnly = true,
                maxLines = 3,
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            )
            // 四元式列表
            OutlinedCard {
                // Text("四元式列表")
                showQuaternion(
                    interpreter?.stateStore?.lastOrNull()?.stateName ?: "temp",
                    qExpressions,
                    index,
                    listState
                )
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Row(Modifier.align(Alignment.BottomEnd)) {
            // 执行一次
            FloatingActionButton(
                onClick = {
                    try {
                        interpreter?.executeStep()
                        index = interpreter?.stateStore?.lastOrNull()?.executeIndex ?: 0
                        globalVariableStore = interpreter?.globalVariableStore?.clone() ?: listOf()
                        variableStore = interpreter?.stateStore?.lastOrNull()?.variableStore?.clone() ?: listOf()
                        tempVariableStore =
                            interpreter?.stateStore?.lastOrNull()?.tempVariableStore?.clone() ?: listOf()
                        qExpressions = interpreter?.stateStore?.lastOrNull()?.qExpressionList?.Myclone() ?: listOf()
                        outputText = interpreter?.outputString ?: ""
                        isEnd = interpreter?.stateStore?.size == 0
                    } catch (e: Exception) {
                        showDialog = true
                        dialogMessage = e.stackTraceToString()
                    }
                    coroutineScope.launch {
                        listState.animateScrollToItem(index)
                    }
                },
                modifier = Modifier.padding(16.dp),
            ) {
                Row {
                    Icon(Icons.Filled.PlayArrow, contentDescription = "Localized description")
                }
            }
            // 执行全部
            FloatingActionButton(
                onClick = {
                    try {
                        interpreter?.executeAll()
                        index = interpreter?.stateStore?.lastOrNull()?.executeIndex ?: 0
                        globalVariableStore = interpreter?.globalVariableStore?.clone() ?: listOf()
                        variableStore = interpreter?.stateStore?.lastOrNull()?.variableStore?.clone() ?: listOf()
                        tempVariableStore =
                            interpreter?.stateStore?.lastOrNull()?.tempVariableStore?.clone() ?: listOf()
                        outputText = interpreter?.outputString ?: ""
                        isEnd = interpreter?.stateStore?.size == 0
                    } catch (e: Exception) {
                        showDialog = true
                        dialogMessage = e.stackTraceToString()
                    }
                    coroutineScope.launch {
                        listState.animateScrollToItem(index)
                    }
                },
                modifier = Modifier.padding(16.dp),
            ) {
                // 适合执行全部的图标
                Row {
                    Icon(Icons.Filled.PlayArrow, contentDescription = "Localized description")
                    Icon(Icons.Filled.PlayArrow, contentDescription = "Localized description")
                }
            }
        }
    }
    ErrorDialog(dialogMessage, show = showDialog) {
        showDialog = it
    }
}

// 输出四元式与位置索引，灰白相间，当列表的索引为index时，背景色为强调色
@Composable
fun showQuaternion(listName: String = "temp", qExpressions: List<QExpression>, index: Int, state: LazyListState) {
    Column(Modifier.fillMaxSize().padding(8.dp)) {
        ListItem(
            headlineContent = {
                Box(
                    Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        listName,
                    )
                }
            }
        )
        HorizontalDivider()
        LazyColumn(state = state) {

            itemsIndexed(qExpressions) { i, qExpression ->
                ListItem(
                    modifier = Modifier.background(
                        if (i == index) Color(
                            231,
                            224,
                            236
                        ) else if (i % 2 == 0) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.background
                    ),
                    headlineContent = {
                        Box(
                            Modifier.fillMaxWidth().background(
                                if (i == index) Color(
                                    231,
                                    224,
                                    236
                                ) else if (i % 2 == 0) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.background
                            )
                        ) {
                            Text(
                                qExpression.toString(),
                            )
                        }
                    },
                )
                HorizontalDivider()
            }
        }
    }
}

fun List<Variable>.clone(): List<Variable> {
    val result = mutableListOf<Variable>()
    this.forEach {
        result.add(it.copy())
    }
    return result.toList()
}

fun List<QExpression>.Myclone(): List<QExpression> {
    val result = mutableListOf<QExpression>()
    this.forEach {
        result.add(it.copy())
    }
    return result.toList()
}


// 变量输出
@Composable
fun showVariable(variableStore: List<Variable>) {
    LazyColumn {
        itemsIndexed(variableStore) { i, variable ->
            Box(Modifier.background(if (i % 2 == 0) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.background)) {
                Text("${variable.name} = ${variable.value}")
            }
        }
    }
}