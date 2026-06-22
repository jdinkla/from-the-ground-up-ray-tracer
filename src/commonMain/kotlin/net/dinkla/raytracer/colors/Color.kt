package net.dinkla.raytracer.colors

import net.dinkla.raytracer.math.MathUtils.max
import kotlin.math.pow

data class Color(
    val red: Double,
    val green: Double,
    val blue: Double,
) {
    constructor(v: Double) : this(v, v, v)

    operator fun plus(v: Color) = Color(red + v.red, green + v.green, blue + v.blue)

    operator fun times(v: Color) = Color(red * v.red, green * v.green, blue * v.blue)

    operator fun times(s: Double) = Color(s * red, s * green, s * blue)

    fun pow(s: Double) = Color(red.pow(s), green.pow(s), blue.pow(s))

    fun toInt(): Int {
        val r = (red * MAX_CHANNEL).toInt()
        val g = (green * MAX_CHANNEL).toInt()
        val b = (blue * MAX_CHANNEL).toInt()
        return (r shl SHIFT_BYTE_2) or (g shl SHIFT_BYTE_1) or b
    }

    fun toRgba(): Int {
        val r = (red * MAX_CHANNEL).toInt()
        val g = (green * MAX_CHANNEL).toInt()
        val b = (blue * MAX_CHANNEL).toInt()
        return (MAX_CHANNEL shl SHIFT_BYTE_3) or (b shl SHIFT_BYTE_2) or (g shl SHIFT_BYTE_1) or r
    }

    fun clamp(): Color =
        when {
            red > 1.0 || green > 1.0 || blue > 1.0 || red < 0.0 || green < 0.0 || blue < 0.0 -> RED
            else -> this
        }

    fun maxToOne(): Color {
        val maxValue = max(red, green, blue)
        return if (maxValue > 1.0) {
            this * (1.0 / maxValue)
        } else {
            this
        }
    }

    override fun toString(): String = "Color($red,$green,$blue)"

    companion object {
        /** Maximum value of an 8-bit colour channel (0..255). */
        private const val MAX_CHANNEL = 255

        /** [MAX_CHANNEL] as a Double, used to map a normalized channel (0.0..1.0) to/from 0..255. */
        private const val MAX_CHANNEL_DOUBLE = 255.0

        // Bit positions of the four packed colour bytes in a 32-bit integer.
        private const val SHIFT_BYTE_1 = 8
        private const val SHIFT_BYTE_2 = 16
        private const val SHIFT_BYTE_3 = 24

        val BLACK = Color(0.0, 0.0, 0.0)
        val RED = Color(1.0, 0.0, 0.0)
        val GREEN = Color(0.0, 1.0, 0.0)
        val BLUE = Color(0.0, 0.0, 1.0)
        val WHITE = Color(1.0, 1.0, 1.0)
        val YELLOW = Color(1.0, 1.0, 0.0)

        fun fromInt(rgb: Int): Color {
            val r: Double = (rgb and 0x00ff0000 shr SHIFT_BYTE_2) / MAX_CHANNEL_DOUBLE
            val g: Double = (rgb and 0x0000ff00 shr SHIFT_BYTE_1) / MAX_CHANNEL_DOUBLE
            val b: Double = (rgb and 0x000000ff) / MAX_CHANNEL_DOUBLE
            return Color(r, g, b)
        }

        fun fromString(rgb: String): Color {
            fun convert(s: Int): Double {
                val hex = rgb.substring(s, s + 2)
                val dec = hex.toLong(radix = 16)
                return dec / MAX_CHANNEL_DOUBLE
            }
            val rf = convert(0)
            val gf = convert(2)
            val bf = convert(4)
            return Color(rf, gf, bf)
        }

        fun fromRGB(
            red: Int,
            green: Int,
            blue: Int,
        ) = Color(red / MAX_CHANNEL_DOUBLE, green / MAX_CHANNEL_DOUBLE, blue / MAX_CHANNEL_DOUBLE)
    }
}

operator fun Double.times(c: Color) = c.times(this)
