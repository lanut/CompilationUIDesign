package ui.menuIlems

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alibaba.fastjson2.toJSONString
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import homework.grammatical.utils.toUmlTree
import homework.grammatical.语法分析器
import homework.lexical.codeStrToTokenList
import homework.quaternionTranslators.QuaternionGeneration
import kotlinx.coroutines.runBlocking
import ui.utils.readFileInBackground
import java.io.File

@Preview
@Composable
fun middleCodeGeneration() {
    // 记录输入或者读取的文本
    var text by remember { mutableStateOf("") }
    Row {
        Column(modifier = Modifier.width(300.dp)) {
            // 左侧
            Column {
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
                // 输入程序文本
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("请输入程序文本") },
                    modifier = Modifier.fillMaxSize().padding(8.dp)
                )
            }
        }
        Column {
            // 右侧
            var quaternionGeneration by remember { mutableStateOf(QuaternionGeneration()) }
            // 执行分析按钮
            Button(onClick = {
                val lTokenList = text.codeStrToTokenList()
                val jsonString = lTokenList.toJSONString()
                val expressionStore = 语法分析器(jsonString)
                val syntaxTree = expressionStore.toTree().apply { tidy() }
                val umlTree = syntaxTree.toUmlTree()
                val tempGeneration = QuaternionGeneration()
                tempGeneration.程序(syntaxTree.root)
                quaternionGeneration = tempGeneration
            }) {
                Text("执行分析")
            }
            // 输出结果
            Box (modifier = Modifier.fillMaxSize().padding(8.dp)) {
                showQuaternion(quaternionGeneration)
            }
        }
    }
}


@Composable
fun showQuaternion(quaternionGeneration: QuaternionGeneration) {
    Column {
        Text("主函数四元式")
        OutlinedCard(modifier = Modifier.height(200.dp).fillMaxSize().padding(8.dp)) {
            LazyColumn {
                for (quaternion in quaternionGeneration.qExpressionList) {
                    item {
                        Text(quaternion.toString())
                    }
                }
            }
        }

        Text("其余函数四元式")
        OutlinedCard(modifier = Modifier.height(200.dp).fillMaxSize().padding(8.dp)) {
            LazyColumn {
                for (function in quaternionGeneration.functionList) {
                    item {
                        val sb = StringBuilder()
                        sb.append("函数表：\n").append(function).append("\n").append("函数四元式：\n")
                        function.qExpressionList.forEach { qExpression ->
                            sb.append(qExpression).append('\n')
                        }
                        Text(sb.toString())
                    }
                }
            }
        }
    }
}