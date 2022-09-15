package net.dinkla.raytracer.utilities

actual class Timer {

    private var startTime: Long = 0L
    private var endTime: Long = 0L

    actual val duration: Long
        get() = endTime - startTime

    actual fun start() {
        startTime = System.currentTimeMillis()
        endTime = 0L
    }

    actual fun stop() {
        assert(endTime == 0L)
        endTime = System.currentTimeMillis()
    }

}
