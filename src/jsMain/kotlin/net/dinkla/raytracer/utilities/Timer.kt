package net.dinkla.raytracer.utilities

import kotlin.js.Date

actual class Timer {

    private var start: Double = 0.0
    private var end: Double = 0.0

    actual val duration: Long
        get() = (end - start).toLong()

    actual fun start() {
        start = Date.now()
    }

    actual fun stop() {
        end = Date.now()
    }
}