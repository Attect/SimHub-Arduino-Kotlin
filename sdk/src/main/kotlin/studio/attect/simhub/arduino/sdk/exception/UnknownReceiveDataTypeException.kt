package studio.attect.simhub.arduino.sdk.exception

class UnknownReceiveDataTypeException(val byte: Byte):Exception() {
    override val message: String
        get() = "Unknown receive data type(int):$byte"
}