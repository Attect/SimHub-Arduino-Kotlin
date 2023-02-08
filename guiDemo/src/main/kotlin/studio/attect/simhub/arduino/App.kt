package studio.attect.simhub.arduino

import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun byteArrayOfInts(vararg ints: Int) = ByteArray(ints.size) { pos -> ints[pos].toByte() }

var currentDevice: SimHubDevice? by mutableStateOf(null)

fun main(args: Array<String>?): Unit = runBlocking {
    refreshDevice(this)
    application() {
        Window(
            onCloseRequest = ::exitApplication,
            title = "SimHub Device",
            state = WindowState(width = 800.dp, height = 400.dp)
        ) {
            App(this@runBlocking)
        }
    }


}

@Composable
private fun App(appScope: CoroutineScope) {
    MaterialTheme {
        Column {
            DeviceTab(appScope)
            Text("test")
        }
    }
}


fun refreshDevice(appScope: CoroutineScope) {
    DeviceManager.refreshDevice {
        appScope.launch {
            if (currentDevice == null && DeviceManager.allComDevice.size > 0) {
                currentDevice = DeviceManager.allComDevice[0]
            }
        }
    }

}