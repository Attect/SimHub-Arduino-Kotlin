package studio.attect.simhub.arduino.sdk

import com.fazecast.jSerialComm.SerialPort
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import studio.attect.simhub.arduino.sdk.exception.NotSupportDeviceException
import studio.attect.simhub.arduino.sdk.request.*
import studio.attect.simhub.arduino.sdk.response.ResponseExpandedCommand
import studio.attect.simhub.arduino.sdk.response.ResponseFeature
import kotlin.coroutines.CoroutineContext
import kotlin.experimental.xor

class SimHubArduinoDeviceSession(val systemPortName: String) : CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext =
        Dispatchers.IO + job + CoroutineName("SimHubArduinoDevice-$systemPortName")

    private lateinit var port: SerialPort
    private var baudRate = 19200
    private var lastSendTime = 0L
    private var packageId = 0xFF.toByte()

    private var handshaked = false

    private val requestChannel = Channel<IRequest<*>>()
    var feature = ResponseFeature()
    var expandedCommand = ResponseExpandedCommand()

    var rgbLedCount = 0
    var tm1638Count = 0
    var simpleModuleCount = 0
    var deviceName = "unknown"
    var uniqueId = ""
    var buttonCount = 0
    var mcuType = ByteArray(0)

    private val statusListenerList = arrayListOf<(Status) -> Unit>()
    private val statusListenerListLock = Mutex()
    var currentStatus = Status.DISCONNECT
        private set

    var keepAliveJob: Job? = null
    var sendJob: Job? = null

    suspend fun join() {
        job.join()
    }

    fun openAsync(): Deferred<Boolean> = async {
        if (this@SimHubArduinoDeviceSession::port.isInitialized) return@async false
        if (systemPortName.isBlank()) return@async false
        updateStatus(Status.CONNECTING)
        kotlin.runCatching {
            port = SerialPort.getCommPort(systemPortName.trim()).apply {
                baudRate = this@SimHubArduinoDeviceSession.baudRate
                if (!openPort()) return@async false
                setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0)
            }
            handShake()
            initAndStartSendJob()

            queryDeviceFeature()
            queryDeviceRgbLedCount()
            queryDeviceTM1638Count()
            queryDeviceSimpleModuleCount()
            queryDeviceListExpandedCommand()

            queryDeviceName()
            queryDeviceUniqueId()
            queryDeviceButtonCount()
            queryDeviceMcuType()

            doSetBaudRate(RequestSetBaudRate.BaudRate.R_115200)
            updateStatus(Status.READY)

            startKeepAlive()

        }.apply { exceptionOrNull()?.printStackTrace() }.isSuccess
    }

    /**
     * 发送hello指令，尝试与设备握手
     *
     * 若发生意外回应，或版本不匹配，抛出[NotSupportDeviceException]
     */
    private suspend fun handShake() {
        val hello = RequestHello()
        while (!handshaked) {
            delay(50)
            withContext(Dispatchers.IO) {
                port.outputStream.write(packageData(hello))
            }
            hello.readData(port)?.let { responseHello ->
                println("handshake version:${responseHello.version}")
                if (responseHello.version == VERSION) {
                    handshaked = true
                    packageId++
                } else {
                    port.closePort()
                    updateStatus(Status.DISCONNECT)
                    throw NotSupportDeviceException(responseHello.version)
                }
            }
        }
    }

    /**
     * 初始化发送队列
     */
    private fun initAndStartSendJob() {
        if (sendJob != null) return
        sendJob = launch(coroutineContext + Job(job)) {
            val outputStream = port.outputStream
            requestChannel.consumeEach {
                it.packageId = packageId
                withContext(Dispatchers.IO) {
                    outputStream.write(packageData(it))
                }
                lastSendTime = System.currentTimeMillis()
                if (packageId == PACKAGE_ID_LESS_THAN) {
                    packageId = ZERO_BYTE
                } else {
                    packageId++
                }
            }
        }
    }

    /**
     * 向设备查询所支持的功能特性
     */
    private suspend fun queryDeviceFeature() {
        val requestFeature = RequestFeature()
        requestChannel.send(requestFeature)
        requestFeature.readData(port)?.let {
            feature = it
//            feature.forEach {
//                println("feature ${it.name}")
//            }
        }
    }

    /**
     * 向设备查询所拥有的RGB灯数量
     */
    private suspend fun queryDeviceRgbLedCount() {
        val requestRgbLedCount = RequestRGBLedCount()
        requestChannel.send(requestRgbLedCount)
        requestRgbLedCount.readData(port)?.let {
            rgbLedCount = it
        }
    }

    /**
     * 向设备查询TM1638按键数码管数量
     */
    private suspend fun queryDeviceTM1638Count() {
        val requestTM1638Count = RequestTM1638Count()
        requestChannel.send(requestTM1638Count)
        requestTM1638Count.readData(port)?.let {
            tm1638Count = it
        }
    }

    /**
     * 向设备查询MAX7221启用的模块数量 + TM1637启用的模块数量 + TM1637_6D启用的模块数量 + 启用的ADA_HT16K33_7SEGMENTS数量
     */
    private suspend fun queryDeviceSimpleModuleCount() {
        val requestSimpleModuleCount = RequestSimpleModuleCount()
        requestChannel.send(requestSimpleModuleCount)
        requestSimpleModuleCount.readData(port)?.let {
            simpleModuleCount = it
        }
    }

    /**
     * 向设备查询扩展指令列表
     */
    private suspend fun queryDeviceListExpandedCommand() {
        val requestListExpandedCommand = RequestListExpandedCommand()
        requestChannel.send(requestListExpandedCommand)
        expandedCommand = requestListExpandedCommand.readData(port)
    }

    /**
     * 查询设备名称
     *
     * 若查询失败为unknown
     */
    private suspend fun queryDeviceName() {
        val requestDeviceName = RequestDeviceName()
        requestChannel.send(requestDeviceName)
        deviceName = requestDeviceName.readData(port) ?: "unknown"
    }

    /**
     * 查询设备唯一id
     *
     * 查询失败则为空字符串
     */
    private suspend fun queryDeviceUniqueId() {
        val requestUniqueId = RequestUniqueId()
        requestChannel.send(requestUniqueId)
        uniqueId = requestUniqueId.readData(port) ?: ""
    }

    /**
     * 查询设备按钮数量
     */
    private suspend fun queryDeviceButtonCount() {
        val requestButtonCount = RequestButtonCount()
        requestChannel.send(requestButtonCount)
        buttonCount = requestButtonCount.readData(port) ?: 0
    }

    /**
     * 查询设备Mcu类型
     */
    private suspend fun queryDeviceMcuType() {
        val requestMcuType = RequestMcuType()
        requestChannel.send(requestMcuType)
        mcuType = requestMcuType.readData(port) ?: ByteArray(0)
    }

    private fun startKeepAlive() {
        keepAliveJob = launch {
            val requestKeepAlive = RequestKeepAlive()
            while (currentStatus == Status.READY) {
                delay(1000)
                if (System.currentTimeMillis() - lastSendTime >= 1000) {
                    requestChannel.send(requestKeepAlive)
                    if (!requestKeepAlive.readData(port)) {
                        port.closePort()
                        updateStatus(Status.DISCONNECT)
                    }
                }
            }
        }
    }

    suspend fun addStatusListener(listener: (Status) -> Unit) {
        statusListenerListLock.withLock {
            statusListenerList.add(listener)
        }
    }

    suspend fun removeStatusListener(listener: (Status) -> Unit) {
        statusListenerListLock.withLock {
            statusListenerList.remove(listener)
        }
    }

    private fun updateStatus(status: Status) {
        launch {
            statusListenerListLock.withLock {
                statusListenerList.forEach { it(status) }
            }
        }
        currentStatus = status
    }

    suspend fun setBaudRate(rate: RequestSetBaudRate.BaudRate): Boolean {
        if (currentStatus == Status.READY) {
            return doSetBaudRate(rate)
        }
        return false
    }

    private suspend fun doSetBaudRate(rate: RequestSetBaudRate.BaudRate): Boolean = kotlin.runCatching {
        val request = RequestSetBaudRate().apply {
            this.rate = rate
        }
        requestChannel.send(request)
        val result = request.readData(port)
        if (result) {
            println("波特率变更为:${rate.intValue()}")
            port.baudRate = rate.intValue()
            port.openPort()
        }
        return result
    }.isSuccess

    private fun Byte.crc(verifyValue: Byte): Byte {
        return crcTable[this.xor(verifyValue).toInt() and 0xFF]
    }

    private fun packageData(request: IRequest<*>): ByteArray {
        val data = request.toByteArray()
        request.packageId = packageId
        val size = data.size + 1
//        if (size > 64) throw DataTooLongException(data)
        var currentCrc = 0.toByte()
        val length = size.toByte()
        val result = ByteArray(size + 5)
        result[0] = PROTOCOL_HEADER
        result[1] = PROTOCOL_HEADER
        result[2] = packageId
        result[3] = length
        result[4] = 0x03.toByte()

        currentCrc = currentCrc.crc(packageId)
        currentCrc = currentCrc.crc(length)
        currentCrc = currentCrc.crc(0x03.toByte())

        data.forEachIndexed { index, byte ->
            result[index + 5] = byte
            currentCrc = currentCrc.crc(byte)
        }

        result[result.size - 1] = currentCrc
        return result
//        val nextPackageId = packageId.plus(1).toByte()
//        packageId = if (nextPackageId > 127.toByte()) {
//            0
//        } else {
//            nextPackageId
//        }
//        lastSendTime = System.currentTimeMillis()

    }

    suspend fun request(request: IRequest<*>) {
        requestChannel.send(request)
    }

    fun test(): Job = launch {
        var i = 1
        while (isActive) {
            val frequency = RequestToneSetFrequency(i++)
            requestChannel.send(frequency)
            delay(33)
            if (i > 730) i = 0
        }
    }


    enum class Status {
        /**
         * 正在连接
         */
        CONNECTING,

        /**
         * 成功连接
         */
        READY,

        /**
         * 连接断开
         */
        DISCONNECT
    }

    companion object {
        const val VERSION = "j"

        /**
         * 查表CRC计算，来自ArqSerial.h
         */
        private val crcTable = arrayOf(
            0,
            213,
            127,
            170,
            254,
            43,
            129,
            84,
            41,
            252,
            86,
            131,
            215,
            2,
            168,
            125,
            82,
            135,
            45,
            248,
            172,
            121,
            211,
            6,
            123,
            174,
            4,
            209,
            133,
            80,
            250,
            47,
            164,
            113,
            219,
            14,
            90,
            143,
            37,
            240,
            141,
            88,
            242,
            39,
            115,
            166,
            12,
            217,
            246,
            35,
            137,
            92,
            8,
            221,
            119,
            162,
            223,
            10,
            160,
            117,
            33,
            244,
            94,
            139,
            157,
            72,
            226,
            55,
            99,
            182,
            28,
            201,
            180,
            97,
            203,
            30,
            74,
            159,
            53,
            224,
            207,
            26,
            176,
            101,
            49,
            228,
            78,
            155,
            230,
            51,
            153,
            76,
            24,
            205,
            103,
            178,
            57,
            236,
            70,
            147,
            199,
            18,
            184,
            109,
            16,
            197,
            111,
            186,
            238,
            59,
            145,
            68,
            107,
            190,
            20,
            193,
            149,
            64,
            234,
            63,
            66,
            151,
            61,
            232,
            188,
            105,
            195,
            22,
            239,
            58,
            144,
            69,
            17,
            196,
            110,
            187,
            198,
            19,
            185,
            108,
            56,
            237,
            71,
            146,
            189,
            104,
            194,
            23,
            67,
            150,
            60,
            233,
            148,
            65,
            235,
            62,
            106,
            191,
            21,
            192,
            75,
            158,
            52,
            225,
            181,
            96,
            202,
            31,
            98,
            183,
            29,
            200,
            156,
            73,
            227,
            54,
            25,
            204,
            102,
            179,
            231,
            50,
            152,
            77,
            48,
            229,
            79,
            154,
            206,
            27,
            177,
            100,
            114,
            167,
            13,
            216,
            140,
            89,
            243,
            38,
            91,
            142,
            36,
            241,
            165,
            112,
            218,
            15,
            32,
            245,
            95,
            138,
            222,
            11,
            161,
            116,
            9,
            220,
            118,
            163,
            247,
            34,
            136,
            93,
            214,
            3,
            169,
            124,
            40,
            253,
            87,
            130,
            255,
            42,
            128,
            85,
            1,
            212,
            126,
            171,
            132,
            81,
            251,
            46,
            122,
            175,
            5,
            208,
            173,
            120,
            210,
            7,
            83,
            134,
            44,
            249
        ).map { it.toByte() }.toByteArray()

        private const val PROTOCOL_HEADER = 0x01.toByte()
        private const val ZERO_BYTE = 0.toByte()
        private const val PACKAGE_ID_LESS_THAN = 0x80.toByte()
    }
}