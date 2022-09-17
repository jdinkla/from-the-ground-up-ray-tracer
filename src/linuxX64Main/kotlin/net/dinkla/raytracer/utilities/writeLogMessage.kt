package net.dinkla.raytracer.utilities

import com.soywiz.klock.DateTime

internal actual fun writeLogMessage(logLevel: Logger.LogLevel, message: String) {
    println("${DateTime.now()} $logLevel $message")
}
