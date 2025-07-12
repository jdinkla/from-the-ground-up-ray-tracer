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
        val r = (red * 255).toInt()
        val g = (green * 255).toInt()
        val b = (blue * 255).toInt()
        return (r shl 16) or (g shl 8) or b
    }

    fun toRgba(): Int {
        val r = (red * 255).toInt()
        val g = (green * 255).toInt()
        val b = (blue * 255).toInt()
        return (255 shl 24) or (b shl 16) or (g shl 8) or r
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
        val BLACK = Color(0.0, 0.0, 0.0)
        val RED = Color(1.0, 0.0, 0.0)
        val GREEN = Color(0.0, 1.0, 0.0)
        val BLUE = Color(0.0, 0.0, 1.0)
        val WHITE = Color(1.0, 1.0, 1.0)
        val YELLOW = Color(1.0, 1.0, 0.0)

        fun fromInt(rgb: Int): Color {
            val r: Double = (rgb and 0x00ff0000 shr 16) / 255.0
            val g: Double = (rgb and 0x0000ff00 shr 8) / 255.0
            val b: Double = (rgb and 0x000000ff) / 255.0
            return Color(r, g, b)
        }

        fun fromString(rgb: String): Color {
            fun convert(s: Int): Double {
                val hex = rgb.substring(s, s + 2)
                val dec = hex.toLong(radix = 16)
                return dec / 255.0
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
        ) = Color(red / 255.0, green / 255.0, blue / 255.0)
    }
}

operator fun Double.times(c: Color) = c.times(this)
