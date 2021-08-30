package studio.attect.simhub.arduino.sdk

object SimHubArduinoCRC {

    private fun byteArrayOfInts(vararg ints: Int) = ByteArray(ints.size) { pos -> ints[pos].toByte() }

}