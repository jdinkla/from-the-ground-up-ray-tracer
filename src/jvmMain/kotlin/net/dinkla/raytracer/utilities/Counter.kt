package net.dinkla.raytracer.utilities

import java.util.TreeMap
import java.util.concurrent.ConcurrentHashMap

object Counter {

    // For each thread-id there is a map
    private var instances = ConcurrentHashMap<Long, TreeMap<String, Int>>()
    private var PAUSE = false

    fun count(key: String) {
        if (!PAUSE) {
            val id = Thread.currentThread().threadId()
            var map: TreeMap<String, Int>? = instances[id]
            if (null == map) {
                map = TreeMap()
                instances[id] = map
            }
            map[key] = (map[key] ?: 0) + 1
        }
    }

    fun stats(columns: Int) = printStats(calculateStats(), columns)

    private fun calculateStats(): Map<String, Int> {
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
