package net.dinkla.raytracer.world.dsl

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.lights.Light
import net.dinkla.raytracer.lights.PointLight
import net.dinkla.raytracer.math.Point3D

class LightsScope() {

    private val mutableLights : MutableList<Light> = mutableListOf()

    val lights: List<Light>
        get() = mutableLights.toList()

    fun pointLight(location: Point3D = Point3D.ORIGIN, ls: Double = 0.0, color: Color = Color.WHITE) {
        mutableLights += PointLight(location, ls, color)
    }
}