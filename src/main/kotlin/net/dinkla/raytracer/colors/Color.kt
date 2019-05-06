package net.dinkla.raytracer.colors

import net.dinkla.raytracer.hits.Shade

import java.lang.Math.max

data class Color(val red: Double, val green: Double, val blue: Double) {

    constructor(v: Double) : this(v, v, v)

    operator fun plus(v: Color) = Color(red + v.red, green + v.green, blue + v.blue)

    operator fun times(v: Color) = Color(red * v.red, green * v.green, blue * v.blue)

    operator fun times(s: Double) = Color(s * red, s * green, s * blue)

    fun pow(s: Double) = Color(Math.pow(red, s), Math.pow(green, s), Math.pow(blue, s))

    // TODO sick?
    fun getColor(sr: Shade): Color {
        return this
    }

    fun asInt(): Int {
        val r = (red * 255).toInt()
        val g = (green * 255).toInt()
        val b = (blue * 255).toInt()
        return r shl 16 or (g shl 8) or b
    }

    fun clamp(): Color = when {
        red > 1 || green > 1 || blue > 1 || red < 0 || green < 0 || blue < 0 -> CLAMP_COLOR
        else -> this
    }

    fun maxToOne(): Color {
        val maxValue = max(red, max(green, blue))
        return if (maxValue > 1) {
            this.times(1 / maxValue)
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
        val ERROR = Color(1.0, 0.0, 0.0)
        val CLAMP_COLOR = Color(1.0, 0.0, 0.0)

        fun create(rgb: Int): Color {
            val r: Double = (rgb and 0x00ff0000 shr 16) / 255.0
            val g: Double = (rgb and 0x0000ff00 shr 8) / 255.0
            val b: Double = (rgb and 0x000000ff) / 255.0
            return Color(r, g, b)
        }

        fun create(rgb: String) : Color {
            fun convert(s: Int): Double {
                val hex = rgb.substring(s, s + 2)
                val dec =Integer.valueOf(hex, 16)
                return dec / 255.0
            }
            val rf = convert(0)
            val gf = convert(2)
            val bf = convert(4)
            return Color(rf, gf, bf)
        }
    }
}
