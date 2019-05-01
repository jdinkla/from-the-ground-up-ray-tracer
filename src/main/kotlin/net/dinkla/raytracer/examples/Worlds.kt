package net.dinkla.raytracer.examples

import net.dinkla.raytracer.world.WorldDef

fun worldDef(filename: String): WorldDef? =
        when (filename) {
            "World5.kt" -> World5
            "World6.kt" -> World6
            "World7.kt" -> World7
            "World10.kt" -> World10
            "World20.kt" -> World20
            "World48.kt" -> World48
            "World66.kt" -> World66
            "NewWorld1.kt" -> NewWorld1
            "NewWorld2.kt" -> NewWorld2
            else -> null
        }
