package studio.attect.simhub.arduino.sdk.request

import com.fazecast.jSerialComm.SerialPort
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull

abstract class AbstractRequestReadCount:IRequest<Int> {
    override var packageId: Byte = 0xFF.toByte()
    override suspend fun readData(port: SerialPort): Int? = coroutineScope{
        val dataSize = withTimeoutOrNull(2000){
            while (port.bytesAvailable() < 4){
                delay(50)
            }
            port.bytesAvailable()
        } ?:return@coroutineScope null
        val byteArray = ByteArray(dataSize)
        port.readBytes(byteArray,dataSize.toLong())
        if(dataSize>= 4){
            if(byteArray[0] == 0x03.toByte() && byteArray[1] == packageId){
                return@coroutineScope byteArray[3].toInt() and 0xFF
            }else{
                return@coroutineScope null
            }
        }
        return@coroutineScope null
    }

    override fun toByteArray(): ByteArray = ByteArray(1){command}
}