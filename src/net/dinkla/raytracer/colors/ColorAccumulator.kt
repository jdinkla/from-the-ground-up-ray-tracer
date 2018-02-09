package net.dinkla.raytracer.colors

class ColorAccumulator {

    protected var aggregated: Color
    protected var count: Int = 0

    val average: Color
        get() {
            val result: Color
            if (count > 0) {
                result = aggregated.mult(1.0 / count)
            } else {
                result = Color.BLACK
            }
            return result
        }

    init {
        aggregated = Color.BLACK
        count = 0
    }

    operator fun plus(color: Color) {
        aggregated = aggregated.plus(color)
        count++
    }

}
