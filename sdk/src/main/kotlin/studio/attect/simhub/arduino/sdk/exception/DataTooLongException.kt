package studio.attect.simhub.arduino.sdk.exception

class DataTooLongException(var data:ByteArray):Exception() {
    override val message: String
        get() = "data too long. ${data.size} > 32"
}