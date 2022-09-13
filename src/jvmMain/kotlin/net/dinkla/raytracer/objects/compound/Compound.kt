package net.dinkla.raytracer.objects.compound

import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.hits.ShadowHit
import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.objects.GeometricObject
import net.dinkla.raytracer.interfaces.Counter
import net.dinkla.raytracer.math.*
import net.dinkla.raytracer.world.World

open class Compound : GeometricObject() {

    var objects: ArrayList<GeometricObject> = ArrayList()

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

    override fun hit(ray: Ray, sr: Hit): Boolean {
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

    fun hitObjects(world: World, ray: Ray): Shade {
        Counter.count("Compound.hitObjects")
        val tmin = WrappedDouble.createMax()
        val sr = Shade()
        val b = hit(ray, sr)
        return sr
    }

    override fun shadowHit(ray: Ray, tmin: ShadowHit): Boolean {
        Counter.count("Compound.shadowHit")
        //WrappedDouble t = WrappedDouble.createMax();
        for (geoObj in objects) {
            Counter.count("Compound.shadowHit.geometricObject")
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

    fun add(geometricObject: GeometricObject) {
        isInitialized = false
        geometricObject.initialize()
        objects.add(geometricObject)
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
        for (geometricObject in objects) {
            geometricObject.initialize()
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
