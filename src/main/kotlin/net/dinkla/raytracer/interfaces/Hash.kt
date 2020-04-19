package net.dinkla.raytracer.interfaces

import java.util.Objects

fun <T> T.hash(vararg objects: Any) = Objects.hash(*objects)