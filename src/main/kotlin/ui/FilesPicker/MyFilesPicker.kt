package ui.FilesPicker

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import com.darkrockstudios.libraries.mpfilepicker.MultipleFilePicker

@Composable
@Preview
fun MyFilesPicker() {
    var multiChosen by remember { mutableStateOf(false) }
    var showPicker by remember { mutableStateOf(false) }
    var pathChosen by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }
    if (!multiChosen) {
        text = "单选模式："
        FilePicker(showPicker) { file ->
            pathChosen = file?.path ?: "none selected"
            showPicker = false
        }
    } else {
        text = "多选模式："
        MultipleFilePicker(showPicker) { mpFiles ->
            val sb = StringBuilder()
            mpFiles?.forEach { mpFile ->
                sb.appendLine(mpFile.path)
            }
            pathChosen = if (mpFiles != null) {
                sb.toString()
            } else {
                "none selected"
            }
            showPicker = false
        }
    }
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = text, style = MaterialTheme.typography.bodyMedium)
            Switch(
                checked = multiChosen,
                onCheckedChange = { multiChosen = !multiChosen },
                thumbContent = if (multiChosen) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            modifier = Modifier.size(SwitchDefaults.IconSize),
                        )
                    }
                } else {
                    null
                }
            )
        }
        Button(onClick = {
            showPicker = true
        }) {
            Text("FilePicker")
        }
        OutlinedCard(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            border = BorderStroke(1.dp, color = Color.Black),
            modifier = Modifier
                .size(width = 480.dp, height = 300.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("已选文本")
                Text(pathChosen, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}