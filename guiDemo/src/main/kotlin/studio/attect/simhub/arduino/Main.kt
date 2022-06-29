package studio.attect.simhub.arduino

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.runBlocking
import studio.attect.simhub.arduino.sdk.SimHubArduinoDeviceSession
import kotlin.system.exitProcess

fun byteArrayOfInts(vararg ints: Int) = ByteArray(ints.size) { pos -> ints[pos].toByte() }

var currentFrequency by mutableStateOf(0)
fun main(args: Array<String>?): Unit = runBlocking {
    val session = SimHubArduinoDeviceSession("COM6")
    if (!session.openAsync().await()) {
        println("端口${session.portDescriptor}打开失败")
        exitProcess(-1)
    }
    session.addStatusListener {
        println("status:$it")
    }
    application() {
        Window(
            onCloseRequest = ::exitApplication,
            title = "SimHub Device"
        ) {

            App(session)
        }
    }


}

@Composable
fun App(session: SimHubArduinoDeviceSession) {

    MaterialTheme {
        Column {
            Box {
                Text("当前频率：${currentFrequency}Hz", fontSize = 64.sp)
            }
            Button(onClick = {
                session.test()
            }) {
                Text("测试")
            }
        }

    }

}