package studio.attect.simhub.arduino.sdk.exception

class NotSupportDeviceException(version:String):Exception() {
    override val message: String = "Not support device with version:$version"
}