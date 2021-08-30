package studio.attect.simhub.arduino.sdk.request

import com.fazecast.jSerialComm.SerialPort
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull

class RequestKeepAlive:AbstractRequestExpandedCommand<Boolean>() {
    override val expandedCommand: String = "keepalive"

    override suspend fun readData(port: SerialPort): Boolean = coroutineScope {
        val dataSize = withTimeoutOrNull(2000){
            while (port.bytesAvailable() < 2){
                delay(50)
            }
            port.bytesAvailable()
        } ?:return@coroutineScope false
        val byteArray = ByteArray(dataSize)
        port.readBytes(byteArray,dataSize.toLong())
        if(dataSize>= 2){
            return@coroutineScope(byteArray[0] == 0x03.toByte() && byteArray[1] == packageId)
        }
        return@coroutineScope false
    }
}