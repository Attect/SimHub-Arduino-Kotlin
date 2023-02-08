package studio.attect.simhub.arduino.sdk

import com.fazecast.jSerialComm.SerialPort
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import oshi.SystemInfo
import studio.attect.simhub.arduino.sdk.request.RequestToneSetFrequency
import studio.attect.simhub.arduino.sdk.response.ResponseExpandedCommand
import kotlin.test.Test

class SimHubArduinoDeviceSessionTest {
    @Test
    fun listPorts() {
        SerialPort.getCommPorts().forEach { println(it.descriptivePortName) }
    }

    @Test
    fun listFeature() {
        GlobalScope.launch {
            SerialPort.getCommPorts().forEach { serialPort ->
                if (serialPort.descriptivePortName.contains("USB-SERIAL")) {
                    println(serialPort.descriptivePortName)
                    val session = SimHubArduinoDeviceSession(serialPort.systemPortName)
                    if (!session.openAsync().await()) {
                        println("设备处理失败")
                        return@forEach
                    }
                    session.feature.forEach {
                        println("feature ${it.name}")
                    }
                    session.expandedCommand.forEach {
                        println("expand ${it.name}")
                    }
                    if (session.expandedCommand.contains(ResponseExpandedCommand.ExpandedCommand.TACHOMETER)) {
                        println("方波发生器开始工作")
                        var i = 1
                        while (true) {
                            val frequency = RequestToneSetFrequency(i++)
                            session.request(frequency)
//                            frequency.readData(serialPort)
                            delay(33)
                            if (i > 230) {
                                val resetFrequency = RequestToneSetFrequency(1)
                                session.request(resetFrequency)

                                break
                            }
                        }
                        println("方波发生器停止工作")
                    }
                    //todo 方波发生器后不应该发送心跳

                    session.join()

                }

            }
        }
        Thread.sleep(Long.MAX_VALUE)
    }

    @Test
    fun cpuToneSetFrequency() {
        val systemInfo = SystemInfo()
        GlobalScope.launch {
            SerialPort.getCommPorts().forEach { serialPort ->
                if (serialPort.descriptivePortName.contains("USB-SERIAL")) {
                    println(serialPort.descriptivePortName)
                    val session = SimHubArduinoDeviceSession(serialPort.systemPortName)
                    if (!session.openAsync().await()) {
                        println("设备处理失败")
                        return@forEach
                    }
                    session.feature.forEach {
                        println("feature ${it.name}")
                    }
                    session.expandedCommand.forEach {
                        println("expand ${it.name}")
                    }
                    if (session.expandedCommand.contains(ResponseExpandedCommand.ExpandedCommand.TACHOMETER)) {
                        println("方波发生器开始工作")
                        var i = 1
                        while (true) {
                            val processCoreLoads = systemInfo.hardware.processor.getProcessorCpuLoad(300)
                            var totalLoad = 0.0
                            processCoreLoads.forEach {
                                totalLoad += it
                            }
                            totalLoad /= processCoreLoads.size
//                            println("cpu:$totalLoad")
                            val frequency = RequestToneSetFrequency((totalLoad * 730).toInt())
                            session.request(frequency)
//                            frequency.readData(serialPort)
//                            delay(33)
                        }
                    }
                    //todo 方波发生器后不应该发送心跳

                    session.join()

                }

            }
        }
        Thread.sleep(Long.MAX_VALUE)
    }
}