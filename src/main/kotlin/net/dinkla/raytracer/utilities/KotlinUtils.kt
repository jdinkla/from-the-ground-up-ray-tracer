package net.dinkla.raytracer.utilities

import java.util.*

inline fun <reified T> T.equals(other: Any?, f: (p: T, q: T) -> Boolean): Boolean =
        if (other != null && other is T) {
            f(this, other)
        } else false

//inline fun <reified T> equals1(ths: Any?, other: Any?, f: (p: T, q: T) -> Boolean): Boolean {
//    return if (ths != null && ths is T && other != null && other is T) {
//        f(ths, other)
//    } else false
//}

fun <T> T.hash(vararg objects: Any) = Objects.hash(*objects)


