package studio.attect.simhub.arduino

import com.fazecast.jSerialComm.SerialPort
import studio.attect.simhub.arduino.sdk.SimHubArduinoDeviceSession

class SimHubDevice(serialPort: SerialPort, val session: SimHubArduinoDeviceSession) : ComDevice(serialPort) {
}