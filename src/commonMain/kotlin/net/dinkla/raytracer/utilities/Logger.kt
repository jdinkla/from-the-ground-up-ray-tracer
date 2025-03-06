package net.dinkla.raytracer.utilities

import korlibs.time.DateFormat.Companion.FORMAT2
import korlibs.time.DateTime

object Logger {
    enum class LogLevel {
        DEBUG,
        INFO,
        WARN,
        ERROR,
    }

    fun debug(s: String) = writeLogMessage(LogLevel.DEBUG, s)

    fun info(s: String) = writeLogMessage(LogLevel.INFO, s)

    fun warn(s: String) = writeLogMessage(LogLevel.WARN, s)

    fun error(s: String) = writeLogMessage(LogLevel.ERROR, s)
}

private fun writeLogMessage(
    logLevel: Logger.LogLevel,
    message: String,
) {
    println("${DateTime.now().format(FORMAT2)} $logLevel $message")
}
