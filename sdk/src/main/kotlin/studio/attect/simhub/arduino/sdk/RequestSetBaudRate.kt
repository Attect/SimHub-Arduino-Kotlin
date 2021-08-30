package studio.attect.simhub.arduino.sdk

import studio.attect.simhub.arduino.sdk.request.AbstractRequestOk

class RequestSetBaudRate:AbstractRequestOk() {
    override val command: Byte = 0x38.toByte()
    var rate = BaudRate.R_19200

    override fun toByteArray(): ByteArray = byteArrayOf(command,rate.value)

    enum class BaudRate(v:Byte){
        R_300(1.toByte()),
        R_1200(2.toByte()),
        R_2400(3.toByte()),
        R_4800(4.toByte()),
        R_9600(5.toByte()),
        R_14400(6.toByte()),
        R_19200(7.toByte()),
        R_28800(8.toByte()),
        R_38400(9.toByte()),
        R_57600(10.toByte()),
        R_115200(11.toByte()),
        R_230400(12.toByte()),
        R_250000(13.toByte()),
        R_1000000(14.toByte()),
        R_2000000(15.toByte()),
        R_200000(16.toByte()),
        R_500000(17.toByte()),
        ;
        val value = v

        fun intValue() = name.substringAfter("_").toInt()
    }
}