package studio.attect.simhub.arduino.sdk.request

class RequestButtonCount:AbstractRequestReadCount() {
    override val command: Byte = 0x4A.toByte()
}