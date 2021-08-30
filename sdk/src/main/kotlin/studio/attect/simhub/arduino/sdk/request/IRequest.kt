package studio.attect.simhub.arduino.sdk.request

import com.fazecast.jSerialComm.SerialPort

interface IRequest<ResponseData> {
    val command:Byte
    var packageId:Byte

    suspend fun readData(port:SerialPort):ResponseData?

    fun toByteArray():ByteArray

    companion object {
        /**
         * 查表CRC计算，来自ArqSerial.h
         */
        val crcTable = arrayOf(
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

        const val PROTOCOL_HEADER = 0x01.toByte()

        enum class Type(byte: Byte) {
            UNKNOWN(0xFF.toByte()),
            PACKAGE_ID(0x03.toByte()),
            LAST_KNOWN_VALID_PACKAGE(0x05.toByte()),
            CUSTOM_PACKAGE(0x09.toByte()),
            BYTE_DATA(0x08.toByte()),
            STRING_DATA(0x06.toByte()),
            DEBUG_STRING_DATA(0x07.toByte())
            ;
            var value:Byte = byte

            companion object{
                fun valueOf(byte:Byte):Type{
                    return when(byte){
                        PACKAGE_ID.value-> PACKAGE_ID
                        LAST_KNOWN_VALID_PACKAGE.value-> LAST_KNOWN_VALID_PACKAGE
                        CUSTOM_PACKAGE.value-> CUSTOM_PACKAGE
                        BYTE_DATA.value-> BYTE_DATA
                        STRING_DATA.value-> STRING_DATA
                        DEBUG_STRING_DATA.value-> DEBUG_STRING_DATA
                        else-> UNKNOWN
                    }
                }
            }
        }

        fun ByteArray.readString():String?{
            if (get(0) == Type.STRING_DATA.value){
                val length = get(1).toInt() and 0xFF
                if(size!=length+3) return null
                return decodeToString(2,size-1).trim()
            }
            return null
        }

        fun ByteArray.readStringList():ArrayList<String>{
            val result = ArrayList<String>()
            var offset = 0
            while (offset < size-1){
                if(get(offset) == Type.STRING_DATA.value){
                    val length = offset+ 1+( get(offset+1).toInt() and 0xFF)+1+1
                    sliceArray(offset until length).readString()?.let {
                        result+=it.trim()
                    }
                    offset = length
                }else{
                    offset++
                }
            }
            return result
        }
    }
}