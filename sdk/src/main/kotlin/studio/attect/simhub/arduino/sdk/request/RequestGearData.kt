package studio.attect.simhub.arduino.sdk.request

class RequestGearData:AbstractRequestOk() {
    override val command: Byte = 0x47.toByte()
    private var dataByte:Byte = COMMAND_BYTE_CLEAR_DIGIT

    fun clearDigit(){
        dataByte = COMMAND_BYTE_CLEAR_DIGIT
    }

    fun reverseDigit(){
        dataByte = COMMAND_BYTE_REVERSE_DIGIT
    }

    fun neutralDigit(){
        dataByte = COMMAND_BYTE_NEUTRAL_DIGIT
    }

    fun setData(value:Int){
        if((0..9).contains(value)){
            dataByte = (0x30+value).toByte()
        }
    }

    override fun toByteArray(): ByteArray = byteArrayOf(command,dataByte)

    companion object{
        private const val COMMAND_BYTE_CLEAR_DIGIT = 0x20.toByte()
        private const val COMMAND_BYTE_REVERSE_DIGIT = 0x52.toByte()
        private const val COMMAND_BYTE_NEUTRAL_DIGIT = 0x20.toByte()
    }
}