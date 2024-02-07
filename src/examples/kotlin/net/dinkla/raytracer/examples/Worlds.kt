package net.dinkla.raytracer.examples

import io.github.classgraph.ClassGraph
import net.dinkla.raytracer.world.WorldDefinition

private const val PACKAGE_NAME = "net.dinkla.raytracer.examples"

fun worlds(): List<WorldDefinition> =
    ClassGraph().enableClassInfo().acceptPackages(PACKAGE_NAME).scan().use { scanResult ->
        scanResult.getClassesImplementing(WorldDefinition::class.java.name).map { classInfo ->
            val clazz = Class.forName(classInfo.name)
            if (clazz.kotlin.objectInstance is WorldDefinition) {
                clazz.kotlin.objectInstance as WorldDefinition
            } else {
                null
            }
        }
    }.filterNotNull()

val worldMap: Map<String, WorldDefinition> by lazy {
    worlds().map { it.id to it }.toMap()
}

fun worldDef(filename: String): WorldDefinition? = worldMap[filename]
