package net.dinkla.raytracer.utilities

import korlibs.time.DateTime

class Timer {
    private var start: Double = 0.0
    private var end: Double = 0.0

    val duration: Long
        get() = (end - start).toLong()

    fun start() {
        start = timeStamp()
    }

    fun stop() {
        end = timeStamp()
    }

    private fun timeStamp() = DateTime.now().unixMillis
}
