package net.dinkla.raytracer.utilities

inline fun <reified T> T.equals(other: Any?, f: (p: T, q: T) -> Boolean): Boolean =
        if (other != null && other is T) {
            f(this, other)
        } else false
