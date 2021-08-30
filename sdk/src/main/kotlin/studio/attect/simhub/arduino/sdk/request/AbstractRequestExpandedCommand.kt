package studio.attect.simhub.arduino.sdk.request

import kotlin.math.exp

abstract class AbstractRequestExpandedCommand<ResponseData>:IRequest<ResponseData> {
    override val command: Byte = 0x58.toByte()
    abstract val expandedCommand:String
    override var packageId: Byte = 0xFF.toByte()
    var value:String? = null

    override fun toByteArray(): ByteArray {
        val expandedCommandByteArray = expandedCommand.toByteArray()
        val valueByteArray = value?.toByteArray()
        val length = if(valueByteArray == null){
            1+expandedCommandByteArray.size+1
        }else{
            1+expandedCommandByteArray.size+1+valueByteArray.size+1
        }
        val result = ByteArray(length)
        result[0] = command
        expandedCommandByteArray.forEachIndexed { index, byte ->
            result[index+1] = byte
        }
        result[1+ expandedCommandByteArray.size] = breakLineByte
        valueByteArray?.let { bytes ->
            bytes.forEachIndexed { index, byte ->
                result[index+1+expandedCommandByteArray.size+1] = byte
            }
            result[result.size-1] = breakLineByte
        }
        return result
    }

    companion object{
        const val breakLineByte = 0x0A.toByte()
    }
}