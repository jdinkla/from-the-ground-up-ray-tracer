package net.dinkla.raytracer.utilities

actual object Counter {
    private val map = mutableMapOf<String, Int>()

    actual fun count(key: String) {
        map[key] = (map[key] ?: 0) + 1
    }

    actual fun reset() {
        Logger.info("Counter.reset")
        map.clear()
    }

    actual fun stats(columns: Int) = printStats(map, columns)
}
