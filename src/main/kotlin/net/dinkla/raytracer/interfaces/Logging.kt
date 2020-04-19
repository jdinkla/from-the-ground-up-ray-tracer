package net.dinkla.raytracer.interfaces

interface Logging {
    fun debug(s: String)
    fun info(s: String)
    fun warn(s: String)
    fun error(s: String)
}
