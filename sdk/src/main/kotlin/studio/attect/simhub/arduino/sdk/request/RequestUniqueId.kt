package studio.attect.simhub.arduino.sdk.request

class RequestUniqueId:AbstractRequestReadString() {
    override val command: Byte = 0x49.toByte()

}