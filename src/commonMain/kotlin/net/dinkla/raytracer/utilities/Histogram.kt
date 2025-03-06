package net.dinkla.raytracer.utilities

class Histogram {
    private var counts = mutableMapOf<Int, Int>()

    fun add(key: Int) {
        counts[key] = (counts[key] ?: 0) + 1
    }

    operator fun get(key: Int): Int = counts[key] ?: 0

    fun keys(): Set<Int> = counts.keys

    fun println() {
        var min: Int = Int.MAX_VALUE
        var max: Int = Int.MIN_VALUE
        for (k in counts.keys) {
            val v = get(k)
            if (v > max) max = v
            if (v < min) min = v
            println("k=$k, v=$v")
        }
        if (min != Int.MAX_VALUE || max != Int.MIN_VALUE) {
            println("min=$min, max=$max")
        }
    }
}
