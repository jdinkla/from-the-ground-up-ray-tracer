package net.dinkla.raytracer.worlds

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.films.IFilm
import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.hits.ShadowHit
import net.dinkla.raytracer.ViewPlane
import net.dinkla.raytracer.cameras.Camera
import net.dinkla.raytracer.lights.Ambient
import net.dinkla.raytracer.lights.Light
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.objects.acceleration.kdtree.InnerNode
import net.dinkla.raytracer.objects.compound.Compound
import net.dinkla.raytracer.objects.GeometricObject
import net.dinkla.raytracer.tracers.Tracer
import net.dinkla.raytracer.tracers.Whitted
import net.dinkla.raytracer.utilities.Counter
import net.dinkla.raytracer.utilities.StepCounter

import java.util.LinkedList

class World {

    protected var compound: Compound
    var backgroundColor: Color
        protected set
    var errorColor: Color
        protected set
    var lights: List<Light>
    var ambientLight: Ambient
    var viewPlane: ViewPlane
        protected set
    var tracer: Tracer
        protected set
    var camera: Camera? = null
        protected set
    var isDynamic: Boolean = false
    var stepCounter: StepCounter? = null
        protected set

    init {
        lights = LinkedList()
        ambientLight = Ambient()
        viewPlane = ViewPlane()
        tracer = Whitted(this)
        compound = Compound()
        isDynamic = false
        stepCounter = null

        // TODO color
        backgroundColor = Color.BLACK
        errorColor = Color.errorColor
    }

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

    fun initialize() {
        compound.initialize()
    }

    fun size(): Int {
        return compound.size()
    }

    fun add(`object`: GeometricObject) {
        compound.add(`object`)
    }

    fun add(objects: List<GeometricObject>) {
        this.compound.add(objects)
    }

    operator fun hasNext(): Boolean {
        return stepCounter!!.hasNext()
    }

    fun step() {
        val t = stepCounter!!.current
        if (isDynamic) {
            val q = camera!!.lens.eye
            val p = if (q == null) Point3D.ORIGIN else q
            val p2 = Point3D(p.x + 0.1, p.y + 0.1, p.z)
            camera!!.lens.eye = p2
        }
        stepCounter!!.step()
    }

    fun set() {}

    fun render(imf: IFilm) {

        val timer = net.dinkla.raytracer.utilities.Timer()
        timer.start()
        camera!!.render(imf, 0)
        timer.stop()

        Counter.stats(30)      // ???

        println("Hits")
        InnerNode.hits.println()

        println("fails")
        InnerNode.fails.println()

        println("took " + timer.duration + " [ms]")
    }

    override fun toString(): String {
        return "World"
    }
}