package ui.others

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }
    val openDialog = remember { mutableStateOf(false) }

    if (openDialog.value) {
        myDialog1(openDialog)
    }
    Column {
        Button(onClick = {
            text = "Hello, Desktop!"
        }) {
            Text(text)
        }
        Button(onClick = {
            text = "Hello, World!"
            openDialog.value = true
        }) {
            Text("open Dialog")
        }
        myMessage()
        var checkedState by remember { mutableStateOf(false) }
        Switch(checked = checkedState, onCheckedChange = { checkedState = !checkedState })
    }
    Box(modifier = Modifier.fillMaxSize()) {
        FloatingActionButton(
            onClick = { /*do something*/ }, modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(Icons.Filled.PlayArrow, contentDescription = "Localized description")
        }
    }
}

@Composable
private fun myDialog1(openDialog: MutableState<Boolean>) {
    AlertDialog(onDismissRequest = {
        // 当用户点击对话框以外的地方或者按下系统返回键将会执行的代码
        openDialog.value = false
    }, title = {
        Text(
            text = "这是一个弹窗信息", fontWeight = FontWeight.W700, style = MaterialTheme.typography.titleLarge
        )
    }, text = {
        Text(
            text = "弹出的窗口将会实现一些任务", fontSize = 16.sp
        )
    }, confirmButton = {
        TextButton(
            onClick = {
                openDialog.value = false

            },
        ) {
            Text(
                "确认", fontWeight = FontWeight.W700, style = MaterialTheme.typography.bodyMedium
            )
        }
    }, dismissButton = {
        TextButton(onClick = {
            openDialog.value = false
        }) {
            Text(
                "取消", fontWeight = FontWeight.W700, style = MaterialTheme.typography.bodyMedium
            )
        }
    })
}

@Preview
@Composable
fun myMessage() {
    val cardName = remember { mutableStateOf("lanut") }
    Row {
        Image(
            painter = painterResource("stamp2_hans_008.png"),
            modifier = Modifier.size(150.dp).padding(10.dp).clip(CircleShape),
            contentDescription = "$cardName"
        )
        Column {
            Text("lanut", style = MaterialTheme.typography.titleLarge, fontSize = 50.sp)
            Text("先进于礼乐，野人也；后进于礼乐，君子也。如用之，则吾从先进。", modifier = Modifier.width(300.dp))
        }
    }
}

@Composable
fun Cell(content: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Text(
            text = content,
            modifier = Modifier.padding(8.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}