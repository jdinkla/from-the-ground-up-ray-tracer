package net.dinkla.raytracer.objects.mesh

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.shouldBeGreaterThanOrEqual
import io.kotest.matchers.doubles.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.Shadow
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.shouldBeApprox

class MeshTriangleTest : StringSpec({
    // A right triangle in z = 0: v0 = origin, v1 on +x, v2 on +y.
    fun mesh() =
        Mesh().apply {
            vertices.add(Point3D(0.0, 0.0, 0.0))
            vertices.add(Point3D(1.0, 0.0, 0.0))
            vertices.add(Point3D(0.0, 1.0, 0.0))
        }

    "base mesh triangle hit always returns false (overridden by Flat/Smooth subclasses)" {
        val triangle = MeshTriangle(mesh(), 0, 1, 2)
        val ray = Ray(Point3D(0.25, 0.25, -1.0), Vector3D(0.0, 0.0, 1.0))

        triangle.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
    }

    "mesh triangle shadowHit reports a shadow for a ray through the interior" {
        // From (0.25, 0.25, -1) toward +z the ray meets the triangle at t = 1.
        val triangle = MeshTriangle(mesh(), 0, 1, 2)
        val ray = Ray(Point3D(0.25, 0.25, -1.0), Vector3D(0.0, 0.0, 1.0))

        val shadow = triangle.shadowHit(ray)

        shadow.shouldBeInstanceOf<Shadow.Hit>()
        shadow.t shouldBeApprox 1.0
    }

    "mesh triangle shadowHit returns None beyond the hypotenuse (beta + gamma > 1)" {
        val triangle = MeshTriangle(mesh(), 0, 1, 2)
        val ray = Ray(Point3D(0.8, 0.8, -1.0), Vector3D(0.0, 0.0, 1.0))

        triangle.shadowHit(ray) shouldBe Shadow.None
    }

    "mesh triangle shadowHit returns None to the left of the v0-v2 edge (beta < 0)" {
        val triangle = MeshTriangle(mesh(), 0, 1, 2)
        val ray = Ray(Point3D(-0.5, 0.25, -1.0), Vector3D(0.0, 0.0, 1.0))

        triangle.shadowHit(ray) shouldBe Shadow.None
    }

    "mesh triangle shadowHit returns None below the v0-v1 edge (gamma < 0)" {
        val triangle = MeshTriangle(mesh(), 0, 1, 2)
        val ray = Ray(Point3D(0.25, -0.5, -1.0), Vector3D(0.0, 0.0, 1.0))

        triangle.shadowHit(ray) shouldBe Shadow.None
    }

    "mesh triangle shadowHit returns None when the intersection is behind the origin (t < epsilon)" {
        val triangle = MeshTriangle(mesh(), 0, 1, 2)
        val ray = Ray(Point3D(0.25, 0.25, 1.0), Vector3D(0.0, 0.0, 1.0))

        triangle.shadowHit(ray) shouldBe Shadow.None
    }

    "mesh triangle computeNormal yields the right-handed face normal" {
        val triangle = MeshTriangle(mesh(), 0, 1, 2)

        triangle.computeNormal(reverseNormal = false)

        triangle.normal shouldBe Normal.FORWARD
    }

    "mesh triangle computeNormal reverses the face normal when asked" {
        val triangle = MeshTriangle(mesh(), 0, 1, 2)

        triangle.computeNormal(reverseNormal = true)

        // Negating the face normal yields -0.0 in x/y, which data-class equality distinguishes from 0.0;
        // compare with the component-wise approximate matcher instead.
        triangle.normal!! shouldBeApprox Normal.BACKWARD
    }

    "mesh triangle bounding box encloses all three vertices" {
        val triangle = MeshTriangle(mesh(), 0, 1, 2)

        val bbox = triangle.boundingBox

        bbox.p.x shouldBeLessThanOrEqual 0.0
        bbox.p.y shouldBeLessThanOrEqual 0.0
        bbox.q.x shouldBeGreaterThanOrEqual 1.0
        bbox.q.y shouldBeGreaterThanOrEqual 1.0
    }
})
