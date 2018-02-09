package net.dinkla.raytracer.utilities

class Timer {

    internal var startTime: Long = 0
    internal var endTime: Long = 0

    val duration: Long
        get() = endTime - startTime

    init {
        endTime = 0L
        startTime = endTime
    }

    fun start() {
        startTime = System.currentTimeMillis()
        endTime = 0L
    }

    fun stop() {
        assert(endTime == 0L)
        endTime = System.currentTimeMillis()
    }

}
