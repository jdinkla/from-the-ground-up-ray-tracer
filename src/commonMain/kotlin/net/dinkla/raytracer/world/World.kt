package net.dinkla.raytracer.world

import net.dinkla.raytracer.ViewPlane
import net.dinkla.raytracer.cameras.Camera
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.hits.ShadowHit
import net.dinkla.raytracer.lights.Ambient
import net.dinkla.raytracer.lights.Light
import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.objects.GeometricObject
import net.dinkla.raytracer.objects.compound.Compound
import net.dinkla.raytracer.renderer.IRenderer
import net.dinkla.raytracer.tracers.Tracer
import net.dinkla.raytracer.utilities.Counter

class World(
    val metadata: Metadata,
    val camera: Camera,
    val viewPlane: ViewPlane,
    override val ambientLight: Ambient,
    override val lights: List<Light>,
    val materials: Map<String, IMaterial>,
    val objects: List<GeometricObject>,
    val compound: Compound
) : IWorld {

    override var backgroundColor: Color = Color.BLACK

    override var tracer: Tracer? = null
    var renderer: IRenderer? = null

    @Deprecated("unused")
    fun hit(ray: Ray): Shade {
        Counter.count("World.hit1")
        return compound.hitObjects(this, ray)
    }

    override fun hit(ray: Ray, sr: IHit): Boolean {
        Counter.count("World.hit2")
        return compound.hit(ray, sr)
    }

    @Deprecated("unused")
    fun shadowHit(ray: Ray, tmin: ShadowHit): Boolean {
        Counter.count("World.shadowHit")
        return compound.shadowHit(ray, tmin)
    }

    override fun inShadow(ray: Ray, sr: IShade, d: Double): Boolean {
        Counter.count("World.inShadow")
        return compound.inShadow(ray, sr, d)
    }

    override fun shouldStopRecursion(depth: Int): Boolean {
        return depth > viewPlane.maximalRecursionDepth
    }

    fun initialize() = compound.initialize()

    fun size(): Int = compound.size()

    fun add(obj: GeometricObject) = compound.add(obj)

    fun add(objects: List<GeometricObject>) = compound.add(objects)

    override fun toString(): String = "World ($metadata)"
}
