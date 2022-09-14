package net.dinkla.raytracer.utilities

fun <T> T.hash(vararg objects: Any): Int {
    val prime = 31
    var result = 1
    objects.forEach {
        result = prime * result + it.hashCode()
    }
    return result
}
