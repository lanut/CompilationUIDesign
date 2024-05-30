
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ui.ModalNavigationDrawerDemo



fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "编译原理课程设计",
        icon = painterResource("yunbox_pro.ico"),
        // 设置窗口大小
    ) {
        ModalNavigationDrawerDemo()
    }
}
