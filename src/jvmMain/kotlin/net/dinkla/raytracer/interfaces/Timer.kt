package net.dinkla.raytracer.interfaces

class Timer {

    private var startTime: Long = 0L
    private var endTime: Long = 0L

    val duration: Long
        get() = endTime - startTime

    fun start() {
        startTime = System.currentTimeMillis()
        endTime = 0L
    }

    fun stop() {
        assert(endTime == 0L)
        endTime = System.currentTimeMillis()
    }

}
