package net.dinkla.raytracer

import net.dinkla.raytracer.examples.definitions
import net.dinkla.raytracer.utilities.Logger

fun synopsis(name: String) {
    Logger.error("$name expects world definition filename as input")
    Logger.info("Possible worlds are ${definitions.keys.joinToString(",")}")
}
