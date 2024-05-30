package ui.menuIlems

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import homework.lexical.codeStringToTokenList
import homework.lexical.entity.Token
import ui.others.Cell
import java.io.File


// 词法分析
@Preview
@Composable
fun LexicalAnalysis() {// 左侧为打开文件或者输入程序文本，右侧为词法分析输出结果
    // 记录输入或者读取的文本
    var text by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

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
//                        text = runBlocking {
//                            readFileInBackground(mpFile.path)
//                        }
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
            var tokens by remember { mutableStateOf(emptyList<Token>()) }
            Button(onClick = {
                // todo: 执行词法分析
                try {
                    tokens = codeStringToTokenList(text)
                } catch (e: Exception) {
                    showDialog = true
                    dialogMessage = e.stackTraceToString()
                }
            }) {
                Text("执行分析")
            }
            // 词法分析输出结果
            OutlinedCard(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                //
                showToken(tokens)
            }
        }
    }
    ErrorDialog(dialogMessage, show = showDialog) {
        showDialog = it
    }
}

@Composable
fun ErrorDialog(dialogMessage: String, show: Boolean, dialogState: (boolean: Boolean) -> Unit) {
    if (show) {
        AlertDialog(
            onDismissRequest = { dialogState(false) },
            title = { Text("Error") },
            text = { Text(dialogMessage) },
            confirmButton = {
                Button(onClick = { dialogState(false) }) {
                    Text("OK")
                }
            }
        )
    }
}


// 显示Token
@Composable
fun showToken(tokens: List<Token>) {
    LazyColumn {
        itemsIndexed(tokens) { index, token ->
            val backgroundColor = if (index % 2 == 0) Color.LightGray else Color.White
            Row(modifier = Modifier.background(backgroundColor).fillMaxSize()) {
                Cell(token.category.toString(), Modifier.width(100.dp))
                Cell(token.value, Modifier.width(100.dp))
                Cell(token.line.toString(), Modifier.width(75.dp))
            }
        }
    }
}