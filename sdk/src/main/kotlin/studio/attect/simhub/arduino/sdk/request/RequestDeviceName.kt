package studio.attect.simhub.arduino.sdk.request

class RequestDeviceName:AbstractRequestReadString() {
    override val command: Byte = 0x4E.toByte()
}