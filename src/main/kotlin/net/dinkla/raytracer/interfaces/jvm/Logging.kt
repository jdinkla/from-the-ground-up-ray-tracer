package net.dinkla.raytracer.interfaces.jvm

import net.dinkla.raytracer.interfaces.Logging
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class LoggingImpl(private val logger: Logger) : Logging {

    override fun debug(s: String) {
        logger.debug(s)
    }

    override fun info(s: String) {
        logger.info(s)
    }

    override fun warn(s: String) {
        logger.warn(s)
    }

    override fun error(s: String) {
        logger.error(s)
    }
}

fun getLogger(clazz: Any): Logging = LoggingImpl(LoggerFactory.getLogger(clazz::class.java))