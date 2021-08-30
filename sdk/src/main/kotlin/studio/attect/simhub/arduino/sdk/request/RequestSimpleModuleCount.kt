package studio.attect.simhub.arduino.sdk.request

class RequestSimpleModuleCount:AbstractRequestReadCount() {
    override val command: Byte = 0x42.toByte()
}