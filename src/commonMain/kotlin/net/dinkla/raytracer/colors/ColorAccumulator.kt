package net.dinkla.raytracer.colors

class ColorAccumulator {
    private var aggregated = Color.BLACK
    private var count: Int = 0

    val average: Color
        get() =
            if (count > 0) {
                aggregated * (1.0 / count)
            } else {
                Color.BLACK
            }

    operator fun plus(color: Color) {
        aggregated += color
        count++
    }
}
