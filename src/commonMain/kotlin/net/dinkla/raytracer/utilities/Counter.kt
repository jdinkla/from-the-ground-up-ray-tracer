package net.dinkla.raytracer.utilities

expect object Counter {
    fun count(key: String)
    fun reset()
    fun stats(columns: Int)
}
