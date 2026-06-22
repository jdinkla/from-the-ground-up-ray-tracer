package net.dinkla.raytracer.objects

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.hits.Shadow
import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.MathUtils.K_EPSILON
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Point3D.Companion.ORIGIN
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.objects.compound.Compound

/**
 * A leaf object whose [hit] records [reportedObject] as `sr.geometricObject`. By default it reports
 * itself (the common case), but a caller can make it report something else — mirroring objects such
 * as `Instance` (without a material) that leave the inner object in the hit record. This lets the
 * tests pin how [Compound.hit] resolves the reported geometric object for different child shapes.
 */
private class ReportingStub(
    private val t: Double,
    box: BBox = BBox(ORIGIN, Point3D(1.0, 1.0, 1.0)),
) : IGeometricObject {
    override var isShadows: Boolean = false
    override var boundingBox: BBox = box
    override var material: IMaterial? = null
    var reportedObject: IGeometricObject? = this

    override fun initialize() {
        // no-op for tests
    }

    override fun hit(
        ray: Ray,
        sr: IHit,
    ): Boolean {
        sr.t = t
        sr.normal = Normal.UP
        sr.geometricObject = reportedObject
        return true
    }

    override fun shadowHit(ray: Ray): Shadow = Shadow.Hit(t)
}

class CompoundTest :
    StringSpec({
        "testGetBoundingBox" {
            val s = Sphere(radius = 1.0)
            val c = Compound()
            c.add(s)

            val bboxC = c.boundingBox
            val bboxS = s.boundingBox

            bboxC.p shouldBe bboxS.p.minus(K_EPSILON)
            bboxC.q shouldBe bboxS.q.plus(K_EPSILON)
        }

        "compound hit reports the child it left in the hit record for a plain child" {
            val child = ReportingStub(t = 0.5)
            val compound = Compound()
            compound.add(child)
            compound.initialize()

            val sr = Hit(Double.MAX_VALUE)
            val hit = compound.hit(Ray(Point3D(0.5, 0.5, -1.0), Vector3D(0.0, 0.0, 1.0)), sr)

            hit shouldBe true
            sr.geometricObject shouldBe child
            sr.t shouldBe 0.5
        }

        "compound hit reports the inner leaf resolved by a nested compound child" {
            val leaf = ReportingStub(t = 0.3)
            val inner = Compound()
            inner.add(leaf)
            val outer = Compound()
            outer.add(inner)
            outer.initialize()

            val sr = Hit(Double.MAX_VALUE)
            val hit = outer.hit(Ray(Point3D(0.5, 0.5, -1.0), Vector3D(0.0, 0.0, 1.0)), sr)

            // the nested compound resolves to its own leaf, and the outer compound must report that
            // leaf (not the inner compound) as the object actually struck
            hit shouldBe true
            sr.geometricObject shouldBe leaf
            sr.t shouldBe 0.3
        }

        "compound hit reports a plain child as itself, ignoring what that child left in the record" {
            // even when a plain (non-compound) child leaves some other object in the inner hit record,
            // the compound reports the child itself — pinning the original `is Compound` dispatch where
            // the non-compound branch used `geoObj`, not the inner record's geometricObject
            val proxyTarget = ReportingStub(t = 0.7)
            val child = ReportingStub(t = 0.4).apply { reportedObject = proxyTarget }
            val compound = Compound()
            compound.add(child)
            compound.initialize()

            val sr = Hit(Double.MAX_VALUE)
            val hit = compound.hit(Ray(Point3D(0.5, 0.5, -1.0), Vector3D(0.0, 0.0, 1.0)), sr)

            hit shouldBe true
            sr.geometricObject shouldBe child
            sr.t shouldBe 0.4
        }

        "compound size counts nested compound children recursively" {
            val inner = Compound()
            inner.add(ReportingStub(t = 0.1))
            inner.add(ReportingStub(t = 0.2))
            val outer = Compound()
            outer.add(ReportingStub(t = 0.3))
            outer.add(inner)

            // one plain leaf plus the two leaves inside the nested compound
            outer.size() shouldBe 3
        }
    })
