package net.dinkla.raytracer.interfaces.jvm

import net.dinkla.raytracer.interfaces.GetLogger
import net.dinkla.raytracer.interfaces.Logger
import org.slf4j.LoggerFactory

class LoggerImpl(private val logger: org.slf4j.Logger) : Logger {

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

    companion object : GetLogger {
        override fun getLogger(clazz: Any): Logger = LoggerImpl(LoggerFactory.getLogger(clazz::class.java))
    }
}

fun getLogger(clazz: Any): Logger = LoggerImpl.getLogger(clazz)