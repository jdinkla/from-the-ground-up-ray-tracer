package net.dinkla.raytracer.utilities

import org.slf4j.LoggerFactory

class App {}

val logger = LoggerFactory.getLogger(App::class.java)

internal actual fun writeLogMessage(logLevel: Logger.LogLevel, message: String) {
    when (logLevel) {
        Logger.LogLevel.DEBUG -> logger.debug(message)
        Logger.LogLevel.INFO -> logger.info(message)
        Logger.LogLevel.WARN -> logger.warn(message)
        Logger.LogLevel.ERROR -> logger.error(message)
    }
}