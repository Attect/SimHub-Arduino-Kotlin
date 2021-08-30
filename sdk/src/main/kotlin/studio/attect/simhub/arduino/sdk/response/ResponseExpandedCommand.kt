package studio.attect.simhub.arduino.sdk.response

class ResponseExpandedCommand:ArrayList<ResponseExpandedCommand.ExpandedCommand>() {

    enum class ExpandedCommand(str:String){
        UNKNOWN(""),

        /**
         * 车速表
         */
        SPEEDOMETER_GAUGES("speedo"),

        /**
         * 转速表
         */
        TACHOMETER("tachometer"),

        /**
         * 增压表？
         */
        BOOST_GAUGE("boostgauge"),

        /**
         * 温度表
         */
        TEMPERATURE_GAUGES("tempgauge"),

        /**
         * 油表
         */
        FUEL_GAUGE("fuelgauge"),

        /**
         * 燃油消耗表
         */
        CONSUMPTION_GAUGE("consumptiongauge"),

        DM163_RGB("dm163rgb"),

        /**
         * 编码器？
         */
        ENCODERS("encoders"),

        MCUTYPE("mcutype"),

        KEEP_ALIVE("keepalive")

        ;
        val value = str

        companion object{
            fun getValueOf(str:String):ExpandedCommand{
                return values().find { it.value == str }?:UNKNOWN
            }
        }
    }

}