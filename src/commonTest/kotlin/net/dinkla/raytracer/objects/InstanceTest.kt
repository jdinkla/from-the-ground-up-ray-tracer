package net.dinkla.raytracer.objects

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.Shadow
import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.materials.Matte
import net.dinkla.raytracer.math.AffineTransformation
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.shouldBeApprox

class InstanceTest : StringSpec({

    // A translation by (5,0,0) makes an Instance of a unit sphere at the origin behave like a
    // sphere centred at (5,0,0). A pure translation leaves the surface normal unchanged.
    fun translatedUnitSphere(material: IMaterial? = null): Instance {
        val sphere = Sphere(Point3D.ORIGIN, 1.0)
        if (material != null) {
            sphere.material = material
        }
        val trans = AffineTransformation()
        trans.translate(Vector3D(5.0, 0.0, 0.0))
        return Instance(sphere, trans)
    }

    "instance translates the ray so a hit on the moved sphere records t and the world-space normal" {
        // From (5,3,0) toward -y: hits the translated sphere's top at (5,1,0), t = 2.0, normal +y.
        val instance = translatedUnitSphere()
        val ray = Ray(Point3D(5.0, 3.0, 0.0), Vector3D(0.0, -1.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        instance.hit(ray, sr) shouldBe true
        sr.t shouldBeApprox 2.0
        sr.normal shouldBeApprox Normal.UP
    }

    "instance records the wrapped object as the hit object when it carries a material" {
        val sphere = Sphere(Point3D.ORIGIN, 1.0)
        sphere.material = Matte()
        val trans = AffineTransformation()
        trans.translate(Vector3D(5.0, 0.0, 0.0))
        val instance = Instance(sphere, trans)
        val ray = Ray(Point3D(5.0, 3.0, 0.0), Vector3D(0.0, -1.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        instance.hit(ray, sr) shouldBe true
        sr.geometricObject shouldBe sphere
    }

    "instance leaves the hit object null when the wrapped object has no material" {
        val instance = translatedUnitSphere(material = null)
        val ray = Ray(Point3D(5.0, 3.0, 0.0), Vector3D(0.0, -1.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        instance.hit(ray, sr) shouldBe true
        sr.geometricObject shouldBe null
    }

    "instance reports a miss when the transformed ray misses the wrapped object" {
        // From (0,3,0) toward -y: in object space the sphere sits at the origin, but the inverse
        // ray runs down the line x = -5, which never reaches the unit sphere.
        val instance = translatedUnitSphere()
        val ray = Ray(Point3D(0.0, 3.0, 0.0), Vector3D(0.0, -1.0, 0.0))

        instance.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
    }

    "instance shadowHit delegates to the wrapped object through the inverse ray" {
        val instance = translatedUnitSphere()
        val ray = Ray(Point3D(5.0, 3.0, 0.0), Vector3D(0.0, -1.0, 0.0))

        val shadow = instance.shadowHit(ray)

        shadow.shouldBeInstanceOf<Shadow.Hit>()
        shadow.t shouldBeApprox 2.0
    }

    "instance shadowHit returns none when the transformed ray misses" {
        val instance = translatedUnitSphere()
        val ray = Ray(Point3D(0.0, 3.0, 0.0), Vector3D(0.0, -1.0, 0.0))

        instance.shadowHit(ray) shouldBe Shadow.None
    }

    "instance bounding box spans the transformed object's box" {
        val instance = translatedUnitSphere()

        val bbox = instance.boundingBox

        bbox.p shouldBeApprox Point3D(4.0, -1.0, -1.0)
        bbox.q shouldBeApprox Point3D(6.0, 1.0, 1.0)
    }
})
