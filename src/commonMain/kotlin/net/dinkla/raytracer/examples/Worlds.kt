package net.dinkla.raytracer.examples

import net.dinkla.raytracer.examples.ambient.World61
import net.dinkla.raytracer.examples.arealights.World20AreaDisk
import net.dinkla.raytracer.examples.arealights.World20AreaReactangle
import net.dinkla.raytracer.examples.arealights.World66area
import net.dinkla.raytracer.examples.reflective.World17
import net.dinkla.raytracer.world.WorldDefinition

val definitions = mapOf(
    "World5.kt" to World5,
    "World6.kt" to World6,
    "World7.kt" to World7,
    "World10.kt" to World10,
    "World11.kt" to World11,
    "World14.kt" to World14,
    "World17.kt" to World17,
    "World20.kt" to World20,
    "World20AreaDisk.kt" to World20AreaDisk,
    "World20AreaReactangle.kt" to World20AreaReactangle,
    "World23.kt" to World23,
    "World26.kt" to World26,
    "World34.kt" to World34,
    "World38.kt" to World38,
    "World42.kt" to World42,
    "World48.kt" to World48,
    "World61.kt" to World61,
    "World66.kt" to World66,
    World66area.id to World66area,
    "World66b.kt" to World66b,
    "World71.kt" to World71,
    "World74.kt" to World74,
    "World74kdt.kt" to World74kdt,
    "World75.kt" to World75,
    "World75b.kt" to World75b,
    "NewWorld1.kt" to NewWorld1,
    "NewWorld2.kt" to NewWorld2,
    "NewWorld3.kt" to NewWorld3,
    "NewWorld4.kt" to NewWorld4,
)

fun worldDef(filename: String): WorldDefinition? = definitions[filename]
