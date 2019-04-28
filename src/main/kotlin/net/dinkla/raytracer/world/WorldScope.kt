package net.dinkla.raytracer.world

import net.dinkla.raytracer.cameras.Camera
import net.dinkla.raytracer.cameras.IColorCorrector
import net.dinkla.raytracer.cameras.lenses.Pinhole
import net.dinkla.raytracer.cameras.render.ForkJoinRenderer
import net.dinkla.raytracer.cameras.render.SimpleRenderer
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.world.dsl.LightsScope
import net.dinkla.raytracer.world.dsl.MaterialsScope
import net.dinkla.raytracer.world.dsl.ObjectsScope

class WorldScope(val id: String) {

    val world: World = World()

    init {
        world.id = id
    }

    fun p(x: Int, y: Int, z: Int) = Point3D(x, y, z)
    fun p(x: Double, y: Double, z: Double) = Point3D(x, y, z)

    fun c(v: Double) = Color(v)
    fun c(x: Double, y: Double, z: Double) = Color(x, y, z)

    fun camera(d: Double = 1.0, eye: Point3D = Point3D.ORIGIN, lookAt : Point3D = Point3D.ORIGIN, up : Vector3D = Vector3D.UP) {
        val lens = Pinhole(world.viewPlane)
        lens.d = d

        val renderer = SimpleRenderer(lens, world.tracer)
        val corrector: IColorCorrector = world.viewPlane
        val renderer2 = ForkJoinRenderer(renderer, corrector)
        val camera = Camera(lens, renderer2)
        camera.setup(eye, lookAt, up)

        world.camera = camera
    }

    fun ambientLight(color: Color = Color.WHITE, ls: Double = 1.0) {
        world.ambientLight.color = color
        world.ambientLight.ls = ls
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

