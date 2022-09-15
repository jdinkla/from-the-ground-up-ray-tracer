package net.dinkla.raytracer.utilities

expect class Timer() {
    val duration: Long
    fun start()
    fun stop()
}