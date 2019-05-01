package net.dinkla.raytracer.examples

import net.dinkla.raytracer.world.WorldDef

fun worldDef(filename: String): WorldDef? =
        when (filename) {
            "World5.kt" -> World5
            "World6.kt" -> World6
            "World10.kt" -> World10
            "World48.kt" -> World48
            "World66.kt" -> World66
            else -> null
        }
