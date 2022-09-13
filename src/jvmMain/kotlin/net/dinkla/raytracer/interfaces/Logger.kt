package net.dinkla.raytracer.interfaces

interface Logger {
    fun debug(s: String)
    fun info(s: String)
    fun warn(s: String)
    fun error(s: String)
}

interface GetLogger {
    fun getLogger(clazz: Any): Logger
}
