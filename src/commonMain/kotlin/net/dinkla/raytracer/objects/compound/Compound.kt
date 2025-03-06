package net.dinkla.raytracer.objects.compound

import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.hits.Shadow
import net.dinkla.raytracer.hits.ShadowHit
import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.objects.GeometricObject
import net.dinkla.raytracer.objects.IGeometricObject
import net.dinkla.raytracer.utilities.Counter
import net.dinkla.raytracer.utilities.GeometricObjectUtilities

open class Compound : GeometricObject() {
    init {
        boundingBox = BBox()
    }

    var objects: ArrayList<IGeometricObject> = ArrayList()

    private var isUnit: Boolean = false

    override var material: IMaterial?
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

    override fun hit(
        ray: Ray,
        sr: IHit,
    ): Boolean {
        if (!boundingBox.hit(ray)) {
            Counter.count("Compound.hit.bbox")
            return false
        }
        Counter.count("Compound.hit")

        var hit = false
        for (geoObj in objects) {
            Counter.count("Compound.hit.geometricObject")
            val sr2 = Hit(sr.t)
            val b = geoObj.hit(ray, sr2)
            if (b && sr2.t < sr.t) {
                hit = true
                sr.t = sr2.t
                sr.normal = sr2.normal
                if (geoObj !is Compound) {
                    sr.geometricObject = geoObj
                } else {
                    sr.geometricObject = sr2.geometricObject
                }
            }
        }
        return hit
    }

    fun hitObjects(): Shade {
        Counter.count("Compound.hitObjects")
        return Shade()
    }

    override fun shadowHit(ray: Ray): Shadow {
        Counter.count("Compound.shadowHit")
        // WrappedDouble t = WrappedDouble.createMax();
        for (geoObj in objects) {
            Counter.count("Compound.shadowHit.geometricObject")
            val shadow = geoObj.shadowHit(ray)
            if (shadow.isHit()) {
                return shadow
            }
        }
        return Shadow.None
    }

    fun inShadow(
        ray: Ray,
        d: Double,
    ): Boolean {
        Counter.count("Compound.inShadow")
        val t = ShadowHit(d)
        for (geoObj in objects) {
            val b = geoObj.shadowHit(ray, t)
            if (b && t.t < d) {
                return true
            }
        }
        return false
    }

    fun add(geometricObject: IGeometricObject) {
        isInitialized = false
        geometricObject.initialize()
        objects.add(geometricObject)
        calcBoundingBox()
    }

    fun add(objects: List<IGeometricObject>) {
        isInitialized = false
        this.objects.addAll(objects)
        for (`object` in this.objects) {
            `object`.initialize()
        }
        calcBoundingBox()
    }

    override fun initialize() {
        super.initialize()
        for (geometricObject in objects) {
            geometricObject.initialize()
        }
        calcBoundingBox()
    }

    fun size(): Int {
        if (isUnit) {
            return 1
        } else {
            var size = 0
            for (geoObj in objects) {
                size +=
                    if (geoObj is Compound) {
                        geoObj.size()
                    } else {
                        1
                    }
            }
            return size
        }
    }

    private fun calcBoundingBox() {
        boundingBox = BBox()
        if (objects.size > 0) {
            val (p0, p1) = GeometricObjectUtilities.minMaxCoordinates(objects)
            boundingBox = BBox(p0, p1)
        } else {
            boundingBox = BBox()
        }
    }
}
