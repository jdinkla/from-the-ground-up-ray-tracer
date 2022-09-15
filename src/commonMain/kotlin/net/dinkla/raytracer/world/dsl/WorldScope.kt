package net.dinkla.raytracer.world.dsl

import net.dinkla.raytracer.cameras.Camera
import net.dinkla.raytracer.cameras.lenses.Pinhole
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.lights.AmbientOccluder
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.samplers.Sampler
import net.dinkla.raytracer.world.World

@Suppress("TooManyFunctions")
class WorldScope(val id: String) {

    val world: World = World(id)

    fun p(x: Int, y: Int, z: Int) = Point3D(x.toDouble(), y.toDouble(), z.toDouble())
    fun p(x: Double, y: Double, z: Double) = Point3D(x, y, z)

    fun c(v: Double) = Color(v)
    fun c(red: Double, green: Double, blue: Double) = Color(red, green, blue)
    fun c(red: Int, green: Int, blue: Int) = Color(red.toDouble() / 255.0, green.toDouble() / 255.0, blue.toDouble() / 255.0)
    fun c(hexCode: String) = Color.fromString(hexCode)

    fun n(x: Int, y: Int, z: Int) = Normal(x.toDouble(), y.toDouble(), z.toDouble())
    fun n(x: Double, y: Double, z: Double) = Normal(x, y, z)

    fun v(x: Int, y: Int, z: Int) = Vector3D(x.toDouble(), y.toDouble(), z.toDouble())
    fun v(x: Double, y: Double, z: Double) = Vector3D(x, y, z)

    fun camera(
        d: Double = 1.0,
        eye: Point3D = Point3D.ORIGIN,
        lookAt: Point3D = Point3D.ORIGIN,
        up: Vector3D = Vector3D.UP
    ) {
        world.camera = Camera({ eye, uvw ->
            val p = Pinhole(world.viewPlane, eye, uvw)
            p.d = d
            p
        }, eye, lookAt, up)
    }

    fun ambientLight(color: Color = Color.WHITE, ls: Double = 1.0) {
        world.ambientLight.color = color
        world.ambientLight.ls = ls
    }

    fun ambientOccluder(minAmount: Color, sampler: Sampler, numSamples: Int) {
        world.ambientLight = AmbientOccluder(minAmount, sampler, numSamples)
    }

    fun lights(builder: LightsScope.() -> Unit) {
        val scope = LightsScope()
        scope.builder()
        world.lights = scope.lights
    }

    fun materials(builder: MaterialsScope.() -> Unit) {
        val scope = MaterialsScope()
        scope.builder()
        world.materials = scope.materials
    }

    fun objects(builder: ObjectsScope.() -> Unit) {
        val scope = ObjectsScope(world.materials, world.compound)
        scope.builder()
        world.objects = scope.objects
    }

}

