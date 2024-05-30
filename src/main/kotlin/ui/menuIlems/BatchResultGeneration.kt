package ui.menuIlems

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import com.darkrockstudios.libraries.mpfilepicker.MultipleFilePicker
import kotlinx.coroutines.launch
import ui.utils.outPutFile
import ui.utils.outputFiles
import java.io.File

@Preview
@Composable
fun batchResultGeneration() {
    // 批量生成结果
    // 记录选择的文件
    var files by remember { mutableStateOf(listOf<File>()) }
    var file by remember { mutableStateOf<File?>(null) }
    var text by remember { mutableStateOf("") }
    // 状态列表
    val listState = rememberLazyListState()
    Row {
        Column(Modifier.width(300.dp).padding(8.dp)) {
            // 打开文件
            var showPicker by remember { mutableStateOf(false) }
            Row {
                Button(onClick = { showPicker = true }) {
                    Text("打开文件")
                }
                Column {
                    Text("已选择${files.size}个文件", Modifier.padding(8.dp))
                    Text("批量导出时间略久，请耐心等待")
                }
            }
            MultipleFilePicker(show = showPicker, fileExtensions = listOf("sample")) { mpFiles ->
                if (mpFiles != null) {
                    showPicker = false
                }
                showPicker = false
                if (mpFiles != null) {
                    // mpFiles 转换为 List<File>
                    files = mpFiles.map { it.platformFile as File }
                }
            }
            // 输出选择的文件
            OutlinedCard(Modifier.fillMaxSize()) {
                showSelectedFiles(files) {
                    file = it
                    text = it.readText()
                }
            }
        }
        Column(Modifier.padding(8.dp)) {
            // 右侧

            var outputPath by remember { mutableStateOf("./output") }
            var showDirPicker by remember { mutableStateOf(false) }
            // 协程作用域
            val coroutineScope = rememberCoroutineScope()

            Button(onClick = {
                coroutineScope.launch {
                    outPutFile(file!!, outputPath)
                }
            }, modifier = Modifier.padding(8.dp)) {
                Text("处理当前文件")
            }
            Button(onClick = {
                coroutineScope.launch {
                    outputFiles(files, outputPath)
                }
            }, modifier = Modifier.padding(8.dp)) {
                Text("处理所有文件")
            }


            // 选择输出目录
            Button(onClick = {
                showDirPicker = true
            }, modifier = Modifier.padding(8.dp)) {
                Text("选择输出目录")
            }
            // 输出目录
            Text(outputPath, Modifier.padding(8.dp))

            // 输出文件内容
            TextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("文件内容") },
                modifier = Modifier.fillMaxSize().padding(8.dp))
            // DirectoryPicker()
            DirectoryPicker(showDirPicker) {
                if (it != null) {
                    outputPath = it
                }
                showDirPicker = false
            }
        }
    }
}


// 按照列表输出选择的文件
@Composable
fun showSelectedFiles(files: List<File>, fileChange: (File) -> Unit) {
    Box(Modifier.padding(8.dp)) {
        LazyColumn {
            items(files) { file ->
                ListItem(
                    headlineContent = { Text(file.name) },
                    Modifier.clickable {
                        fileChange(file)
                    }
                )
                HorizontalDivider()
            }
        }
    }
}

