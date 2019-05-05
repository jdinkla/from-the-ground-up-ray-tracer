package net.dinkla.raytracer.examples

import net.dinkla.raytracer.examples.reflective.World17
import net.dinkla.raytracer.world.WorldDef

fun worldDef(filename: String): WorldDef? =
        when (filename) {
            "World5.kt" -> World5
            "World6.kt" -> World6
            "World7.kt" -> World7
            "World10.kt" -> World10
            "World17.kt" -> World17
            "World20.kt" -> World20
            "World26.kt" -> World26
            "World34.kt" -> World34
            "World38.kt" -> World38
            "World42.kt" -> World42
            "World48.kt" -> World48
            "World66.kt" -> World66
            "NewWorld1.kt" -> NewWorld1
            "NewWorld2.kt" -> NewWorld2
            "NewWorld3.kt" -> NewWorld3
            else -> null
        }
