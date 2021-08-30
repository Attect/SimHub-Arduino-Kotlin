package studio.attect.simhub.arduino.sdk.request

import com.fazecast.jSerialComm.SerialPort
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import studio.attect.simhub.arduino.sdk.request.IRequest.Companion.readString

abstract class AbstractRequestReadString:IRequest<String>  {

    override var packageId: Byte = 0xFF.toByte()

    override suspend fun readData(port: SerialPort): String? {
        val dataSize = withTimeoutOrNull(2000){
            var availableBytes = port.bytesAvailable()
            var sameCount = 3
            while ( availableBytes < 4 || sameCount > 0){
                if(port.bytesAvailable() == availableBytes) sameCount--
                delay(50)
                availableBytes = port.bytesAvailable()
            }
            availableBytes
        } ?:return null
        val byteArray = ByteArray(dataSize)
        port.readBytes(byteArray,dataSize.toLong())

        if(dataSize> 4 && byteArray[0] == 0x03.toByte() && byteArray[1] == packageId) {
            return byteArray.sliceArray(2 until dataSize ).readString()
        }
        return null
    }

    override fun toByteArray(): ByteArray = ByteArray(1){command}
}