package studio.attect.simhub.arduino.sdk.request

import com.fazecast.jSerialComm.SerialPort
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import studio.attect.simhub.arduino.sdk.request.IRequest.Companion.readStringList
import studio.attect.simhub.arduino.sdk.response.ResponseExpandedCommand

class RequestListExpandedCommand:AbstractRequestExpandedCommand<ResponseExpandedCommand>() {
    override val expandedCommand: String = "list"

    override suspend fun readData(port: SerialPort): ResponseExpandedCommand {
        val result = ResponseExpandedCommand()
        val dataSize = withTimeoutOrNull(2000){
            var availableBytes = port.bytesAvailable()
            var sameCount = 3
            while ( availableBytes < 6 || sameCount > 0){
                if(port.bytesAvailable() == availableBytes) sameCount--
                delay(50)
                availableBytes = port.bytesAvailable()
            }
            availableBytes
        } ?:return result
        val byteArray = ByteArray(dataSize)
        port.readBytes(byteArray,dataSize.toLong())

        if(dataSize> 6 && byteArray[0] == 0x03.toByte() && byteArray[1] == packageId) {
            byteArray.sliceArray(2 until dataSize ).readStringList().forEach {
                val expandedCommand = ResponseExpandedCommand.ExpandedCommand.getValueOf(it)
                if(expandedCommand!=ResponseExpandedCommand.ExpandedCommand.UNKNOWN){
                    result.add(expandedCommand)
                }
            }
        }
        return result
    }
}