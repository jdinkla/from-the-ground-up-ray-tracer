package net.dinkla.raytracer.utilities

import kotlin.math.max

expect object Counter {
    fun count(key: String)
    fun reset()
    fun stats(columns: Int)
}

private const val EMPTY = "                                                            "

fun printStats(map: Map<String, Int>, columns: Int) {
    Logger.info("Counter.stats")
    for (key in map.keys.sorted()) {
        val spaces = max(columns - key.length - 1, 0)
        val count = map[key]
        println(key + ":" + EMPTY.substring(0, spaces) + count)
    }
}
