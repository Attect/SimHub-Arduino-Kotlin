package studio.attect.simhub.arduino.sdk.response

class ResponseFeature : ArrayList<ResponseFeature.Feature>() {

    enum class Feature(str: String) {
        UNKNOWN(""),
        MATRIX("M"),
        I2C_LCD("L"),
        G_LCD("K"),// Nokia | OLEDS
        Gear("G"),
        Name("N"),
        UNIQUE_ID("I"),
        ADDITIONAL_BUTTONS("J"),
        CUSTOM_PROTOCOL_SUPPORT("P"),
        EXPANDED("X"),//XPANDED
        RGB_MATRIX("R"),
        MOTORS("V")
        ;

        val value: String = str

        companion object {
            fun getValueOf(str: String): Feature {
                return values().find { it.value == str }?: UNKNOWN
            }
        }
    }
}