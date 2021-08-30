package studio.attect.simhub.arduino.extensions

import java.util.*
import kotlin.experimental.*

private val HEX_CHARS = "0123456789abcdef"

/**
 * 字节数组转为十六进制字符串
 */
fun ByteArray.toHexString(
    upper: Boolean = true,
    headSpace: Boolean = true
): String {
    val builder = StringBuilder()
    this.forEachIndexed { _, byte ->
        if (builder.isNotEmpty() && headSpace) {
            builder.append(" ")
        }
        val octet = byte.toInt()
        val firstIndex = (octet and 0xF0).ushr(4)
        val secondIndex = octet and 0x0F
        builder.append(HEX_CHARS[firstIndex])
        builder.append(HEX_CHARS[secondIndex])
    }
    return if (upper) {
        builder.toString().uppercase(Locale.getDefault())
    } else {
        builder.toString().lowercase(Locale.getDefault())
    }
}