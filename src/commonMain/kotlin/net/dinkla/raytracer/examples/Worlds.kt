package net.dinkla.raytracer.examples

import net.dinkla.raytracer.examples.ambient.World61
import net.dinkla.raytracer.examples.arealights.World20AreaDisk
import net.dinkla.raytracer.examples.arealights.World20AreaReactangle
import net.dinkla.raytracer.examples.arealights.World66area
import net.dinkla.raytracer.examples.reflective.World17
import net.dinkla.raytracer.world.WorldDefinition

private val worlds = listOf(
    World5,
    World6,
    World7,
    World10,
    World11,
    World14,
    World17,
    World20, World20AreaDisk, World20AreaReactangle,
    World23,
    World26,
    World34,
    World38,
    World42,
    World48,
    World57,
    World61,
    World66, World66area, World66b, World71,
    World74,
    World74kdt,
    World75,
    World75b,
    NewWorld1,
    NewWorld2,
    NewWorld3,
    NewWorld4,
)

val definitions = worlds.map { it -> it.id to it }.toMap()

fun worldDef(filename: String): WorldDefinition? = definitions[filename]
