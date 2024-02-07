package net.dinkla.raytracer.examples

import net.dinkla.raytracer.examples.ambient.World61
import net.dinkla.raytracer.examples.lights.area.World20AreaDisk
import net.dinkla.raytracer.examples.lights.area.World20AreaReactangle
import net.dinkla.raytracer.examples.lights.area.World66area
import net.dinkla.raytracer.examples.cameras.World58
import net.dinkla.raytracer.examples.lights.area.World23b
import net.dinkla.raytracer.examples.objects.World16
import net.dinkla.raytracer.examples.reflective.World17
import net.dinkla.raytracer.examples.reflective.World27
import net.dinkla.raytracer.examples.reflective.World33
import net.dinkla.raytracer.examples.reflective.World80
import net.dinkla.raytracer.examples.tracers.MultipleObjects
import net.dinkla.raytracer.world.WorldDefinition

private val worlds = listOf(
    World5,
    World6,
    World7,
    World10,
    World11,
    World14,
    World16,
    World17,
    World20, World20AreaDisk, World20AreaReactangle,
    World23, World23b,
    World26,
    World27,
    World33,
    World34,
    World38,
    World42,
    World48,
    World57,
    World58,
    World61,
    World66, World66area, World66b,
    World71,
    World72,
    World74,
    World74kdt,
    World75,
    World75b,
    World80,
    NewWorld1,
    NewWorld2,
    NewWorld3,
    NewWorld4,
    MultipleObjects
)

val definitions = worlds.map { it.id to it }.toMap()

fun worldDef(filename: String): WorldDefinition? = definitions[filename]
