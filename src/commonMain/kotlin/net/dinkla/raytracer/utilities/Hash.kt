package net.dinkla.raytracer.utilities

import java.util.Objects

fun <T> T.hash(vararg objects: Any): Int = Objects.hash(*objects)
