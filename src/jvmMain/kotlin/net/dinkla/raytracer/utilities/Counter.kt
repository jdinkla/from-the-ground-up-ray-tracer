package net.dinkla.raytracer.utilities

import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.max

actual object Counter {

    // For each thread-id there is a map
    private var instances = ConcurrentHashMap<Long, TreeMap<String, Int>>()

    private const val EMPTY = "                                                            "
    var PAUSE = false

    actual fun count(key: String) {
        if (!PAUSE) {
            val id = Thread.currentThread().id
            var map: TreeMap<String, Int>? = instances[id]
            if (null == map) {
                map = TreeMap()
                instances[id] = map
            }
            map[key] = (map[key] ?: 0) + 1
        }
    }

    fun stats(columns: Int) = printStats(calculateStats(), columns)

    private fun printStats(results: TreeMap<String, Int>, columns: Int) {
        println("Counter")
        for (key in results.keys) {
            val spaces = max(columns - key.length - 1, 0)
            val count = results[key]
            println(key + ":" + EMPTY.substring(0, spaces) + count)
        }
    }

    private fun calculateStats(): TreeMap<String, Int> {
        val results = TreeMap<String, Int>()
        for (id in instances.keys) {
            val map: TreeMap<String, Int>? = instances[id]
            if (null != map) {
                for (key in map.keys) {
                    val c: Int = results[key] ?: 0
                    results[key] = c + (map[key] ?: 0)
                }
            }
        }
        return results
    }

    fun reset() {
        instances.clear()
    }
}
