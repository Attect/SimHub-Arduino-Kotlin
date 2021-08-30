package studio.attect.simhub.arduino.sdk.request

import com.fazecast.jSerialComm.SerialPort
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import studio.attect.simhub.arduino.sdk.response.ResponseHello

class RequestHello:IRequest<ResponseHello> {
    override val command: Byte = 0x31.toByte()
    override var packageId:Byte = 0xFF.toByte()

    override fun toByteArray(): ByteArray = ByteArray(1){ command }

    override suspend fun readData(port: SerialPort)=coroutineScope {
        val response = ResponseHello()
        val dataSize = withTimeoutOrNull(2000){
            while (port.bytesAvailable() < 4){
                delay(50)
            }
            port.bytesAvailable()
        }?:return@coroutineScope null
        val byteArray = ByteArray(dataSize)
        port.readBytes(byteArray,dataSize.toLong())
        if(dataSize>= 4){
            if(byteArray[0] == 0x03.toByte() && byteArray[1] == packageId){
                response.version = String(byteArrayOf(byteArray[3]))
                return@coroutineScope response
            }else{
                return@coroutineScope null
            }
        }
        return@coroutineScope null


    }
}