package net.dinkla.raytracer.utilities

import kotlin.math.max

actual object Counter {
    private val map = mutableMapOf<String, Int>()

    actual fun count(key: String) {
        map[key] = (map[key] ?: 0) + 1
    }

    actual fun reset() {
        Logger.info("Counter.reset")
        map.clear()
    }

    actual fun stats(columns: Int) {
        Logger.info("Counter.stats")
        for (key in map.keys) {
            val spaces = max(columns - key.length - 1, 0)
            val count = map[key]
            println(key + ":" + EMPTY.substring(0, spaces) + count)
        }
    }

    private const val EMPTY = "                                                            "
}