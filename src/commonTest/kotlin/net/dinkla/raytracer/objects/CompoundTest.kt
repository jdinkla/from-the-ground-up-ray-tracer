package net.dinkla.raytracer.objects

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.hits.Shadow
import net.dinkla.raytracer.materials.Emissive
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

/** A leaf that is never hit and never occludes — used to pin the "no child in the way" branches. */
private class MissingStub(
    box: BBox = BBox(ORIGIN, Point3D(1.0, 1.0, 1.0)),
) : IGeometricObject {
    override var isShadows: Boolean = false
    override var boundingBox: BBox = box
    override var material: IMaterial? = null

    override fun initialize() {
        // no-op for tests
    }

    override fun hit(
        ray: Ray,
        sr: IHit,
    ): Boolean = false

    override fun shadowHit(ray: Ray): Shadow = Shadow.None
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

        "compound hit returns false without testing children when the ray misses the bounding box" {
            val compound = Compound()
            compound.add(ReportingStub(t = 0.5))
            compound.initialize()

            // The children's bbox is around the unit cube; this ray runs far away from it.
            val ray = Ray(Point3D(10.0, 10.0, 10.0), Vector3D(0.0, 0.0, 1.0))

            compound.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
        }

        "compound hit keeps the nearest of several children" {
            val far = ReportingStub(t = 0.9)
            val near = ReportingStub(t = 0.2)
            val compound = Compound()
            compound.add(far)
            compound.add(near)
            compound.initialize()

            val sr = Hit(Double.MAX_VALUE)
            val hit = compound.hit(Ray(Point3D(0.5, 0.5, -1.0), Vector3D(0.0, 0.0, 1.0)), sr)

            hit shouldBe true
            sr.t shouldBe 0.2
            sr.geometricObject shouldBe near
        }

        "compound shadowHit reports a shadow as soon as a child occludes" {
            val compound = Compound()
            compound.add(MissingStub())
            compound.add(ReportingStub(t = 0.4))
            compound.initialize()

            val shadow = compound.shadowHit(Ray(Point3D(0.5, 0.5, -1.0), Vector3D(0.0, 0.0, 1.0)))

            shadow.shouldBeInstanceOf<Shadow.Hit>()
            shadow.t shouldBe 0.4
        }

        "compound shadowHit returns None when no child occludes" {
            val compound = Compound()
            compound.add(MissingStub())
            compound.add(MissingStub())
            compound.initialize()

            compound.shadowHit(Ray(Point3D(0.5, 0.5, -1.0), Vector3D(0.0, 0.0, 1.0))) shouldBe Shadow.None
        }

        "compound inShadow is true when a child occluder lies nearer than the light" {
            val compound = Compound()
            compound.add(ReportingStub(t = 0.4))
            compound.initialize()

            compound.inShadow(Ray(Point3D(0.5, 0.5, -1.0), Vector3D(0.0, 0.0, 1.0)), d = 1.0) shouldBe true
        }

        "compound inShadow is false when the only occluder is farther than the light" {
            val compound = Compound()
            compound.add(ReportingStub(t = 5.0))
            compound.initialize()

            compound.inShadow(Ray(Point3D(0.5, 0.5, -1.0), Vector3D(0.0, 0.0, 1.0)), d = 1.0) shouldBe false
        }

        "compound inShadow is false when no child is in the way" {
            val compound = Compound()
            compound.add(MissingStub())
            compound.initialize()

            compound.inShadow(Ray(Point3D(0.5, 0.5, -1.0), Vector3D(0.0, 0.0, 1.0)), d = 1.0) shouldBe false
        }

        "setting a compound's material propagates it to every child" {
            val child = ReportingStub(t = 0.5)
            val compound = Compound()
            compound.add(child)
            val material = Emissive()

            compound.material = material

            compound.material shouldBe material
            child.material shouldBe material
        }

        "setting a compound's shadow flag propagates it to every child" {
            val child = ReportingStub(t = 0.5)
            val compound = Compound()
            compound.add(child)

            compound.isShadows = true

            compound.isShadows shouldBe true
            child.isShadows shouldBe true
        }

        "an empty compound has an empty default bounding box and no objects" {
            val compound = Compound()
            compound.initialize()

            compound.size() shouldBe 0
            compound.boundingBox shouldBe BBox()
        }

        "compound add of a list registers all of them" {
            val compound = Compound()

            compound.add(listOf(ReportingStub(t = 0.1), ReportingStub(t = 0.2), ReportingStub(t = 0.3)))

            compound.size() shouldBe 3
        }

        "compound exposes its children for regridding and absorbs new objects in place" {
            val a = ReportingStub(t = 0.1)
            val compound = Compound()
            compound.add(a)

            compound.promotableToSubgrid() shouldBe true
            compound.childrenForRegrid() shouldBe compound.objects

            val b = ReportingStub(t = 0.2)
            val combined = compound.combineInCell(b)

            // a Compound absorbs the new object and returns itself, rather than wrapping in a fresh one
            combined shouldBe compound
            compound.size() shouldBe 2
        }
    })
