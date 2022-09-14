package net.dinkla.raytracer.utilities

internal actual fun writeLogMessage(logLevel: Logger.LogLevel, message: String) {
    console.log(logLevel, message)
}