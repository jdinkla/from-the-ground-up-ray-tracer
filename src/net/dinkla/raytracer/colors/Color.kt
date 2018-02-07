package net.dinkla.raytracer.colors

import net.dinkla.raytracer.hits.Shade

import java.lang.Math.max

class Color {

    val red: Double
    val green: Double
    val blue: Double

    constructor(red: Double, green: Double, blue: Double) {
        this.red = red
        this.green = green
        this.blue = blue
    }

    constructor(v: Double) {
        this.red = v
        this.green = v
        this.blue = v
    }

    operator fun plus(v: Color): Color {
        return Color(red + v.red, green + v.green, blue + v.blue)
    }

    fun mult(v: Color): Color {
        return Color(red * v.red, green * v.green, blue * v.blue)
    }

    fun mult(s: Double): Color {
        return Color(s * red, s * green, s * blue)
    }

    fun pow(s: Double): Color {
        return Color(Math.pow(red, s), Math.pow(green, s), Math.pow(blue, s))
    }

    // TODO sick?
    fun getColor(sr: Shade): Color {
        return this
    }

    /**
     *
     * @return
     */
    fun asInt(): Int {
        val r = (red * 255).toInt()
        val g = (green * 255).toInt()
        val b = (blue * 255).toInt()
        return r shl 16 or (g shl 8) or b
    }

    fun createFromInt(rgb: Int): Color {
        val r: Double = (rgb and 0x00ff0000 shr 16) / 255.0
        val g: Double = (rgb and 0x0000ff00 shr 8) / 255.0
        val b: Double = (rgb and 0x000000ff) / 255.0
        return Color(r, g, b)
    }

    fun clampToColor(): Color {
        return if (red > 1 || green > 1 || blue > 1) {
            CLAMP_COLOR
        } else {
            this
        }
    }

    fun maxToOne(): Color {
        val maxValue = max(red, max(green, blue))
        return if (maxValue > 1) {
            this.mult(1 / maxValue)
        } else {
            this
        }
    }

    override fun equals(obj: Any?): Boolean {
        if (obj !is Color) {
            return false
        } else {
            val e = obj as Color?
            return red == e!!.red && green == e.green && blue == e.blue
        }
    }

    override fun toString(): String {
        return "($red,$green,$blue)"
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

        @JvmField
        val CLAMP_COLOR = Color(1.0, 0.0, 0.0)

    }
}
