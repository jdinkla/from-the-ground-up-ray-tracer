package net.dinkla.raytracer.colors

import net.dinkla.raytracer.hits.Shade

import java.lang.Math.max

data class Color(val red: Double, val green: Double, val blue: Double) {

    constructor(v: Double) : this(v, v, v) {}

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

    fun clamp(): Color {
        return if (red > 1 || green > 1 || blue > 1) {
            CLAMP_COLOR
        } else if (red < 0 || green < 0 || blue < 0) {
            CLAMP_COLOR
        } else {
            this
        }
    }

    fun maxToOne(): Color {
        val maxValue = max(red, max(green, blue))
        return if (maxValue > 1) {
            this.times(1 / maxValue)
        } else {
            this
        }
    }

    companion object {

        @JvmField
        val BLACK = Color(0.0, 0.0, 0.0)

        @JvmField
        val RED = Color(1.0, 0.0, 0.0)

        @JvmField
        val GREEN = Color(0.0, 1.0, 0.0)

        @JvmField
        val BLUE = Color(0.0, 0.0, 1.0)

        @JvmField
        val WHITE = Color(1.0, 1.0, 1.0)

        @JvmField
        val errorColor = Color(1.0, 0.0, 0.0)

        // TODO why red?
        @JvmField
        val CLAMP_COLOR = Color(1.0, 0.0, 0.0)

        fun createFromInt(rgb: Int): Color {
            val r: Double = (rgb and 0x00ff0000 shr 16) / 255.0
            val g: Double = (rgb and 0x0000ff00 shr 8) / 255.0
            val b: Double = (rgb and 0x000000ff) / 255.0
            return Color(r, g, b)
        }
    }
}
