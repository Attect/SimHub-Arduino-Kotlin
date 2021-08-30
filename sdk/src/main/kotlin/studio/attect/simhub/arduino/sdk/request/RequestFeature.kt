package studio.attect.simhub.arduino.sdk.request

import com.fazecast.jSerialComm.SerialPort
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import studio.attect.simhub.arduino.sdk.request.IRequest.Companion.readString
import studio.attect.simhub.arduino.sdk.response.ResponseFeature

class RequestFeature:IRequest<ResponseFeature> {
    override val command: Byte = 0x30.toByte()
    override var packageId: Byte = 0x01.toByte()

    override suspend fun readData(port: SerialPort): ResponseFeature? {
        val feature = ResponseFeature()

        val dataSize = withTimeoutOrNull(2000){
            var availableBytes = port.bytesAvailable()
            var sameCount = 3
            while ((availableBytes-2)%4 != 0 || availableBytes < 6 || sameCount > 0){
                if(port.bytesAvailable() == availableBytes) sameCount--
                delay(50)
                availableBytes = port.bytesAvailable()
            }
            availableBytes
        }?:return null
        val byteArray = ByteArray(dataSize)
        port.readBytes(byteArray,dataSize.toLong())

        if(dataSize >= 6){
            if(byteArray[0] == 0x03.toByte() && byteArray[1] == packageId){
                val contentByteArray = byteArray.sliceArray(2 until dataSize)
                if(contentByteArray.size%4!=0) return null
                for (i in 0 until dataSize-2 step 4){
                    val strByteArray = contentByteArray.sliceArray(i..(i+3))
                    strByteArray.readString()?.let {
                        if(it.isNotBlank()){
                            val f = ResponseFeature.Feature.getValueOf(it)
                            if(f!= ResponseFeature.Feature.UNKNOWN){
                                feature.add(f)
                            }
                        }
                    }
                }
                return feature
            }

        }
        return null
    }

    override fun toByteArray(): ByteArray = ByteArray(1){command}
}