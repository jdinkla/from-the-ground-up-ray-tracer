package net.dinkla.raytracer.utilities

import java.util.HashMap
import java.util.TreeMap
import java.util.concurrent.ConcurrentHashMap

class Counter private constructor() {

    // For each thread-id there is a map
    protected var instances: ConcurrentHashMap<Long, TreeMap<String, Int>>

    init {
        instances = ConcurrentHashMap()
    }

    companion object {

        internal val EMPTY = "                                                            "

        var PAUSE = false
        protected val INSTANCE = Counter()

        fun count(key: String) {
            if (!PAUSE) {
                val id = Thread.currentThread().id
                var map: TreeMap<String, Int>? = INSTANCE.instances[id]
                if (null == map) {
                    map = TreeMap()
                    INSTANCE.instances[id] = map
                }
                var c: Int? = map[key]
                if (null == c) {
                    c = 0
                }
                map[key] = c + 1
            }
        }

        fun stats(columns: Int) {
            val results = calculateStats()
            printStats(results, columns)
        }

        private fun printStats(results: TreeMap<String, Int>, columns: Int) {
            println("Counter")
            for (key in results.keys) {
                val spaces = columns - key.length - 1
                val count = results[key]
                println(key + ":" + EMPTY.substring(0, spaces) + count)
            }
        }

        private fun calculateStats(): TreeMap<String, Int> {
            val results = TreeMap<String, Int>()
            for (id in INSTANCE.instances.keys) {
                val map: TreeMap<String, Int>? = INSTANCE.instances[id!!]
                if (null != map) {
                    for (key in map.keys) {
                        val c: Int = results[key] ?: 0
                        results[key] = c + (map.get(key) ?: 0)
                    }
                }
            }
            return results
        }
    }

}
