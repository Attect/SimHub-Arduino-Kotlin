package studio.attect.simhub.arduino.sdk.request

import com.fazecast.jSerialComm.SerialPort
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull

/**
 * 请求设置方波发生器频率
 *
 * 相关文件：SHTonePin.h/Tone.h
 */
class RequestToneSetFrequency(val frequency: Int) : AbstractRequestExpandedCommand<Any>() {
    override val expandedCommand: String = "tach"

    init {
        value = frequency.toString()
    }

    override suspend fun readData(port: SerialPort): Any? {
        val dataSize = withTimeoutOrNull(2000) {
            var availableBytes = port.bytesAvailable()
            var sameCount = 3
            while (availableBytes < 6 || sameCount > 0) {
                if (port.bytesAvailable() == availableBytes) sameCount--
                delay(50)
                availableBytes = port.bytesAvailable()
            }
            availableBytes
        } ?: return null
        val byteArray = ByteArray(dataSize)
        port.readBytes(byteArray, dataSize.toLong())
        println("read dataSize:$dataSize ${String(byteArray)}")
        return null
    }
}