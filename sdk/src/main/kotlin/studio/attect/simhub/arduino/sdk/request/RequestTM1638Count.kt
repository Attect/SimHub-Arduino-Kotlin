package studio.attect.simhub.arduino.sdk.request

class RequestTM1638Count:AbstractRequestReadCount() {
    override val command: Byte = 0x32.toByte()
}