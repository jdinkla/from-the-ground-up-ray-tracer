package net.dinkla.raytracer.utilities

internal actual fun writeLogMessage(logLevel: Logger.LogLevel, message: String) {
    println("$logLevel $message")
}
