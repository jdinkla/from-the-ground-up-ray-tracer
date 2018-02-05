package net.dinkla.raytracer.math

import java.util.HashMap
import java.util.TreeMap

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 22.05.2010
 * Time: 18:34:41
 * To change this template use File | Settings | File Templates.
 */
class Histogram {

    internal var counts: MutableMap<Int, Int>

    init {
        counts = TreeMap()
    }

    fun add(key: Int) {
        (counts as java.util.Map<Int, Int>).merge(key, 1) { a, b -> a + b }
    }

    operator fun get(key: Int?): Int? {
        val elem = counts[key]
        return elem ?: 0
    }

    fun clear() {
        counts.clear()
    }

    fun keySet(): Set<Int> {
        return counts.keys
    }

    fun println() {
        var min: Int = Integer.MAX_VALUE
        var max: Int = Integer.MIN_VALUE
        for (k in keySet()) {
            val v = get(k)
            if (null != v && v > max) max = v
            if (null != v && v < min) min = v
            println("k=$k, v=$v")
        }
        if (min != Integer.MAX_VALUE || max != Integer.MIN_VALUE) {
            println("min=$min, max=$max")
        }
    }

}
