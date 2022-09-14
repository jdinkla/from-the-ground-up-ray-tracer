package net.dinkla.raytracer.utilities

object Logger {
    enum class LogLevel {
        DEBUG, INFO, WARN, ERROR
    }

    fun debug(s: String) = writeLogMessage(LogLevel.DEBUG, s)
    fun info(s: String) = writeLogMessage(LogLevel.INFO, s)
    fun warn(s: String) = writeLogMessage(LogLevel.WARN, s)
    fun error(s: String) = writeLogMessage(LogLevel.ERROR, s)
}

internal expect fun writeLogMessage(logLevel: Logger.LogLevel, message: String)
