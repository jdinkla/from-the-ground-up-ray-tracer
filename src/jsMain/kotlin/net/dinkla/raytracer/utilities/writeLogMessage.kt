package net.dinkla.raytracer.utilities

import kotlin.js.Date

internal actual fun writeLogMessage(logLevel: Logger.LogLevel, message: String) {
    console.log(Date(), logLevel.name, message)
}