package net.dinkla.raytracer.world

import net.dinkla.raytracer.ViewPlane
import net.dinkla.raytracer.cameras.Camera
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.hits.ShadowHit
import net.dinkla.raytracer.lights.Ambient
import net.dinkla.raytracer.lights.Light
import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.objects.GeometricObject
import net.dinkla.raytracer.objects.compound.Compound
import net.dinkla.raytracer.tracers.Tracer
import net.dinkla.raytracer.utilities.Counter

class World(val id: String, val viewPlane: ViewPlane) {

    val compound: Compound = Compound()
    var backgroundColor: Color = Color.BLACK
    var errorColor: Color = Color.ERROR
    var lights : List<Light> = listOf()
    var ambientLight: Ambient = Ambient()
    var materials : Map<String, IMaterial> = mapOf()
    var objects : List<GeometricObject> = listOf()

    // tmp
    var renderer: Renderer? = null
    val tracer: Tracer
        get() = renderer?.tracer!!
    val camera: Camera
        get() = renderer?.camera!!

    fun hit(ray: Ray): Shade {
        Counter.count("World.hit1")
        return compound.hitObjects(this, ray)
    }

    fun hit(ray: Ray, sr: Hit): Boolean {
        Counter.count("World.hit2")
        return compound.hit(ray, sr)
    }

    fun shadowHit(ray: Ray, tmin: ShadowHit): Boolean {
        Counter.count("World.shadowHit")
        return compound.shadowHit(ray, tmin)
    }

    fun inShadow(ray: Ray, sr: Shade, d: Double): Boolean {
        Counter.count("World.inShadow")
        return compound.inShadow(ray, sr, d)
    }

    fun initialize() = compound.initialize()

    fun size(): Int = compound.size()

    fun add(obj: GeometricObject) = compound.add(obj)

    fun add(objects: List<GeometricObject>) = this.compound.add(objects)

    override fun toString(): String = "World $id"
}
