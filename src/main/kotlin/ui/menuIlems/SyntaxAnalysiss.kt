package ui.menuIlems

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alibaba.fastjson2.toJSONString
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import homework.grammatical.entity.ExpressionStore
import homework.grammatical.entity.Node
import homework.grammatical.语法分析器
import homework.lexical.codeStrToTokenList
import kotlinx.coroutines.runBlocking
import ui.utils.readFileInBackground
import java.io.File

// 语法分析
@Preview
@Composable
fun SyntaxAnalysis() {
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
            // 执行分析按钮
            var expressionStore by remember { mutableStateOf(ExpressionStore()) }
            Button(onClick = {
                val lTokenList = text.codeStrToTokenList()
                val jsonString = lTokenList.toJSONString()
                expressionStore = 语法分析器(jsonString)
            }) {
                Text("执行分析")
            }
            // 输出结果
            OutlinedCard(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                showExpressionStore(expressionStore)
            }
        }
    }
}

@Composable
fun showExpressionStore(expressionStore: ExpressionStore) {
    Column(Modifier.padding(8.dp)) {
        Text("语法分析结果", style = MaterialTheme.typography.titleMedium)
        // 分割线
        HorizontalDivider()
        Spacer(Modifier.height(8.dp))
        LazyColumn {
            for (expression in expressionStore.nodes) {
                item {
                    Text(
                        "${expression.originNode.name} -> ${
                            expression.expansionNode.joinToString(
                                " ", transform = Node::name
                            )
                        }",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
