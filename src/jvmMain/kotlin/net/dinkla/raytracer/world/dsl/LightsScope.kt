package net.dinkla.raytracer.world.dsl

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.lights.*
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.objects.GeometricObject

class LightsScope() {

    private val mutableLights : MutableList<Light> = mutableListOf()

    val lights: List<Light>
        get() = mutableLights.toList()

    fun pointLight(location: Point3D = Point3D.ORIGIN, ls: Double = 0.0, color: Color = Color.WHITE) {
        mutableLights += PointLight(location, ls, color)
    }

    fun directionalLight(direction: Vector3D = Vector3D.UP, ls: Double = 0.0, color: Color = Color.WHITE) {
        mutableLights += DirectionalLight().apply {
            this.ls = ls
            this.setDirection(direction)
            this.color = color
        }
    }

    fun areaLight(of: ILightSource, numSamples: Int) {
        val light = AreaLight().apply {
            this.source = of
            this.numSamples = numSamples
            this.material = of.getLightMaterial()
        }
        mutableLights += light
    }

}