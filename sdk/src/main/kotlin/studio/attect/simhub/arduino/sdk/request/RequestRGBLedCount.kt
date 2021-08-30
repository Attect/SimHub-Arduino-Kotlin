package studio.attect.simhub.arduino.sdk.request

class RequestRGBLedCount:AbstractRequestReadCount() {
    override val command = 0x34.toByte()
}