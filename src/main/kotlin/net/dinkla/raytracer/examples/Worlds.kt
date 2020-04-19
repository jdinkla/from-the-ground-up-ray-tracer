package net.dinkla.raytracer.examples

import net.dinkla.raytracer.examples.reflective.World17
import net.dinkla.raytracer.world.WorldDefinition

fun worldDef(filename: String): WorldDefinition? =
        when (filename) {
            "World5.kt" -> World5
            "World6.kt" -> World6
            "World7.kt" -> World7
            "World10.kt" -> World10
            "World11.kt" -> World11
            "World14.kt" -> World14
            "World17.kt" -> World17
            "World20.kt" -> World20
            "World20area.kt" -> World20area
            "World23.kt" -> World23
            "World26.kt" -> World26
            "World34.kt" -> World34
            "World38.kt" -> World38
            "World42.kt" -> World42
            "World48.kt" -> World48
            "World66.kt" -> World66
            "World71.kt" -> World71
            "World74.kt" -> World74
            "World75.kt" -> World75
            "World75b.kt" -> World75b
            "NewWorld1.kt" -> NewWorld1
            "NewWorld2.kt" -> NewWorld2
            "NewWorld3.kt" -> NewWorld3
            "NewWorld4.kt" -> NewWorld4
            else -> null
        }
