package net.dinkla.raytracer.world.dsl

import net.dinkla.raytracer.ViewPlane
import net.dinkla.raytracer.cameras.Camera
import net.dinkla.raytracer.cameras.IColorCorrector
import net.dinkla.raytracer.cameras.lenses.Pinhole
import net.dinkla.raytracer.cameras.render.ForkJoinRenderer
import net.dinkla.raytracer.cameras.render.Renderers
import net.dinkla.raytracer.cameras.render.SimpleSingleRayRenderer
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.tracers.Tracer
import net.dinkla.raytracer.tracers.Tracers
import net.dinkla.raytracer.tracers.Whitted
import net.dinkla.raytracer.utilities.Resolution
import net.dinkla.raytracer.world.Renderer
import net.dinkla.raytracer.world.World

@Suppress("TooManyFunctions")
class WorldScope(val id: String, val resolution: Resolution) {

    val viewPlane = ViewPlane(resolution)
    val world: World = World(id, viewPlane)
    val renderer = Renderer()

    fun p(x: Int, y: Int, z: Int) = Point3D(x.toDouble(), y.toDouble(), z.toDouble())
    fun p(x: Double, y: Double, z: Double) = Point3D(x, y, z)

    fun c(v: Double) = Color(v)
    fun c(x: Double, y: Double, z: Double) = Color(x, y, z)

    fun c(s: String) = Color.create(s)

    fun n(x: Int, y: Int, z: Int) = Normal(x.toDouble(), y.toDouble(), z.toDouble())
    fun n(x: Double, y: Double, z: Double) = Normal(x, y, z)

    fun v(x: Int, y: Int, z: Int) = Vector3D(x.toDouble(), y.toDouble(), z.toDouble())
    fun v(x: Double, y: Double, z: Double) = Vector3D(x, y, z)

    fun camera(d: Double = 1.0,
               eye: Point3D = Point3D.ORIGIN,
               lookAt : Point3D = Point3D.ORIGIN,
               up : Vector3D = Vector3D.UP,
               engine: Renderers = Renderers.FORK_JOIN,
               tracer: Tracers = Tracers.WHITTED) {
        val lens = Pinhole(world.viewPlane)
        lens.d = d

        val tracer = tracer.create(world)
        this.renderer.tracer = tracer

        val singleRayRenderer = SimpleSingleRayRenderer(lens, tracer)
        val corrector: IColorCorrector = world.viewPlane
        this.renderer.renderer = engine.create(singleRayRenderer, corrector)

        val camera = Camera(lens)
        camera.setup(eye, lookAt, up)
        this.renderer.camera = camera

        // tmp
        world.renderer = this.renderer
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

