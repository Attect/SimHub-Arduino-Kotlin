package studio.attect.simhub.arduino

import androidx.compose.runtime.mutableStateListOf
import com.fazecast.jSerialComm.SerialPort
import kotlinx.coroutines.*
import studio.attect.simhub.arduino.sdk.SimHubArduinoDeviceSession
import kotlin.coroutines.CoroutineContext

object DeviceManager : CoroutineScope {
    private val job = Job()

    override val coroutineContext: CoroutineContext = Dispatchers.Default + job + CoroutineName("DeviceManager")
    val allComDevice = mutableStateListOf<SimHubDevice>()

    fun refreshDevice(onRefreshDone: (() -> Unit)? = null): Job = launch {
        SerialPort.getCommPorts().forEach { serialPort ->
            println("scan posrt:${serialPort.portDescription} ${serialPort.portLocation} ${serialPort.systemPortPath}")
            if (serialPort == null || (!serialPort.systemPortName.startsWith("COM") && !serialPort.systemPortName.startsWith(
                    "/dev/tty"
                ))
            ) return@forEach
            var found = false
            for (device in allComDevice) {
                if (device.session.systemPortName == serialPort.portDescription) {
                    found = true
                    break
                }
            }
            if (!found) {
                allComDevice.add(SimHubDevice(serialPort, SimHubArduinoDeviceSession(serialPort.portDescription)))
            }
        }
        allComDevice.sortBy { it.session.systemPortName }
        onRefreshDone?.invoke()
    }
}