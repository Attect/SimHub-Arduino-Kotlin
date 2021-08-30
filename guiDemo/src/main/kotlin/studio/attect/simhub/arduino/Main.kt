// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.Window
import androidx.compose.material.Text
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import studio.attect.simhub.arduino.sdk.SDK
import studio.attect.simhub.arduino.sdk.SimHubArduinoDeviceSession

fun byteArrayOfInts(vararg ints: Int) = ByteArray(ints.size) { pos -> ints[pos].toByte() }

fun main() = runBlocking {

    val session = SimHubArduinoDeviceSession()
    GlobalScope.launch {
        println("A")
        launch {
            if(!session.open("COM5")){
                println("open failed")
            }
        }

        session.statusChannel.consumeEach {
            println("status:$it")
        }
    }

    Window {


        var text by remember { mutableStateOf("Hello, World!") }

        MaterialTheme {
            Button(onClick = {
                text = SDK().test()
            }) {
                Text(text)
            }
        }
    }
}