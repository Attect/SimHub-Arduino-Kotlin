package studio.attect.simhub.arduino.sdk.request

import com.fazecast.jSerialComm.SerialPort
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import studio.attect.simhub.arduino.sdk.response.ResponseHello

class RequestMcuType:AbstractRequestExpandedCommand<ByteArray>() {
    override val expandedCommand: String = "mcutype"

    override suspend fun readData(port: SerialPort): ByteArray? = coroutineScope {

        val dataSize = withTimeoutOrNull(2000){
            while (port.bytesAvailable() < 8){
                delay(50)
            }
            port.bytesAvailable()
        } ?:return@coroutineScope null
        val byteArray = ByteArray(dataSize)
        port.readBytes(byteArray,dataSize.toLong())
        if(dataSize>= 8){
            if(byteArray[0] == 0x03.toByte() && byteArray[1] == packageId){
                return@coroutineScope byteArrayOf(byteArray[3],byteArray[5],byteArray[7])
            }else{
                return@coroutineScope null
            }
        }
        return@coroutineScope null


    }
}