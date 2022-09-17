package net.dinkla.raytracer.utilities

import com.soywiz.klock.DateTime

actual class Timer {
    private var start: Double = 0.0
    private var end: Double = 0.0

    actual val duration: Long
        get() = (end - start).toLong()

    actual fun start() {
        start = timeStamp()
    }

    actual fun stop() {
        end = timeStamp()
    }

    // TODO fix
    fun timeStamp() = DateTime.nowUnix()
}