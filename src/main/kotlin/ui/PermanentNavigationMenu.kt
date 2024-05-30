package ui

import androidx.compose.animation.Crossfade
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import ui.MyMenus.*
import ui.menuIlems.*

enum class MyMenus(val label: Int, val icon: ImageVector, val contentDescription: String) {
    // 词法分析
    LEXICAL_ANALYSIS(1, Icons.Filled.Settings, "词法分析"),

    // 语法分析
    SYNTAX_ANALYSIS(2, Icons.Filled.AccountTree, "语法分析"),

    // 中间四元式代码生成
    MIDDLE_CODE_GENERATION(3, Icons.Filled.Code, "中间代码生成"),

    // 批量生成结果
    BATCH_RESULT_GENERATION(4, Icons.Filled.Api, "批量生成结果"),

    // 四元式解释器
    INTERPRETER(4, Icons.Filled.Translate, "四元式解释器"),
}

@Preview
@Composable
fun ModalNavigationDrawerDemo() {
    var selectedItem by remember { mutableStateOf(LEXICAL_ANALYSIS) }
    PermanentNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(200.dp)) {
                Box(Modifier.fillMaxSize()) {
                    Column {
                        // 顶部标题
                        Text("编译原理应用程序", modifier = Modifier.padding(16.dp))
                        Spacer(Modifier.height(16.dp)) // 添加空间

                        // 中间菜单
                        LazyColumn {
                            items(MyMenus.entries.toList()) { value ->
                                NavigationDrawerItem(
                                    icon = { Icon(value.icon, value.contentDescription) },
                                    label = { Text(value.contentDescription) },
                                    selected = selectedItem == value,
                                    onClick = { selectedItem = value },
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp)) // 添加空间
                    // 底部信息
                    Text("12107040137\n兰炜晨", modifier = Modifier.align(Alignment.BottomStart).padding(16.dp))
                }
            }
        },
    ) {
        // Screen content
        val paddingValues = PaddingValues(20.dp)
        childPages(paddingValues, selectedItem)
    }
}


@Composable
private fun childPages(paddingValues: PaddingValues, selectedItem: MyMenus) {
    Box(modifier = Modifier.padding(paddingValues)) {
        // 输出 demo 示例
        Crossfade(targetState = selectedItem) { selectedItem ->
            when (selectedItem) {
                LEXICAL_ANALYSIS -> LexicalAnalysis() // 词法分析
                SYNTAX_ANALYSIS -> SyntaxAnalysis() // 语法分析
                MIDDLE_CODE_GENERATION -> middleCodeGeneration() // 中间四元式代码生成
                BATCH_RESULT_GENERATION -> batchResultGeneration() // 批量生成结果
                INTERPRETER -> UIInterpreter() // 四元式解释器
            }
        }
    }
}

