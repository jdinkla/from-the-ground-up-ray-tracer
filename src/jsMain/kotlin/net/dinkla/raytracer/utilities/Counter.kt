package net.dinkla.raytracer.utilities

actual object Counter {
    actual fun count(key: String) {
        // Logger.info("Counter.count $key")
    }

    actual fun reset() {
        Logger.info("Counter.reset")
    }

    actual fun stats(columns: Int) {
        Logger.info("Counter.stats")
    }
}