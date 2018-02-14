package net.dinkla.raytracer.objects.compound

import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.hits.ShadowHit
import net.dinkla.raytracer.materials.Material
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.PointUtilities
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.WrappedFloat
import net.dinkla.raytracer.objects.GeometricObject
import net.dinkla.raytracer.utilities.Counter
import net.dinkla.raytracer.worlds.World
import java.util.*


open class Compound : GeometricObject() {

    open var objects: MutableList<GeometricObject> = ArrayList()

    var isUnit: Boolean = false

    init {
        objects = ArrayList()
        isUnit = false
    }

    override var material: Material?
        get() = super.material
        set(material) {
            super.material = material
            for (geoObj in objects) {
                geoObj.material = material
            }
        }

    override var isShadows: Boolean
        get() = super.isShadows
        set(shadows) {
            super.isShadows = shadows
            for (geoObj in objects) {
                geoObj.isShadows = shadows
            }
        }

    override fun hit(ray: Ray, sr: Hit): Boolean {
        if (!boundingBox.hit(ray)) {
            Counter.count("Compound.hit.bbox")
            return false
        }
        Counter.count("Compound.hit")

        var hit = false
        for (geoObj in objects) {
            Counter.count("Compound.hit.object")
            val sr2 = Hit(sr.t)
            val b = geoObj.hit(ray, sr2)
            if (b && sr2.t < sr.t) {
                hit = true
                sr.t = sr2.t
                sr.normal = sr2.normal
                if (geoObj !is Compound) {
                    sr.`object` = geoObj
                } else {
                    sr.`object` = sr2.`object`
                }
            }
        }
        return hit
    }

    fun hitObjects(world: World, ray: Ray): Shade {
        Counter.count("Compound.hitObjects")
        val tmin = WrappedFloat.createMax()
        val sr = Shade()
        val b = hit(ray, sr)
        return sr
    }

    override fun shadowHit(ray: Ray, tmin: ShadowHit): Boolean {
        Counter.count("Compound.shadowHit")
        //WrappedFloat t = WrappedFloat.createMax();
        for (geoObj in objects) {
            Counter.count("Compound.shadowHit.object")
            if (geoObj.shadowHit(ray, tmin)) {
                //                tmin.setT(t.getValue());
                return true
            }
        }
        return false
    }

    fun inShadow(ray: Ray, sr: Shade, d: Double): Boolean {
        Counter.count("Compound.inShadow")
        //TODO: Wieso hier createMax ? ShadowHit t = ShadowHit.createMax();
        val t = ShadowHit(d)
        for (geoObj in objects) {
            val b = geoObj.shadowHit(ray, t)
            if (b && t.t < d) {
                return true
            }
        }
        return false
    }

    fun add(`object`: GeometricObject) {
        isInitialized = false
        `object`.initialize()
        objects.add(`object`)
        calcBoundingBox()
    }

    fun add(objects: List<GeometricObject>) {
        isInitialized = false
        this.objects.addAll(objects)
        for (`object` in this.objects) {
            `object`.initialize()
        }
        calcBoundingBox()
    }

    override fun initialize() {
        super.initialize()
        for (`object` in objects) {
            `object`.initialize()
        }
        // TODO Warum wird das vorberechnet? Warum nicht lazy?
        calcBoundingBox()
    }

    fun size(): Int {
        if (isUnit) {
            return 1
        } else {
            var size = 0
            for (geoObj in objects) {
                if (geoObj is Compound) {
                    size += geoObj.size()
                } else {
                    size += 1
                }
            }
            return size
        }
    }

    fun calcBoundingBox() {
        if (objects.size > 0) {
            val p0 = PointUtilities.minCoordinates(objects)
            val p1 = PointUtilities.maxCoordinates(objects)
            boundingBox = BBox(p0, p1)
        } else {
            boundingBox = BBox()
        }
    }
}
