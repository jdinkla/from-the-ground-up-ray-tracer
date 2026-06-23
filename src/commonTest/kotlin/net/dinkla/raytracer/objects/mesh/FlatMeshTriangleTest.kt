package net.dinkla.raytracer.objects.mesh

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.shouldBeApprox

class FlatMeshTriangleTest : StringSpec({
    // A single right triangle in the z = 0 plane: v0 = origin, v1 on +x, v2 on +y. Its outward normal
    // (right-handed (v1-v0) x (v2-v0)) points +z.
    fun mesh() =
        Mesh().apply {
            vertices.add(Point3D(0.0, 0.0, 0.0))
            vertices.add(Point3D(1.0, 0.0, 0.0))
            vertices.add(Point3D(0.0, 1.0, 0.0))
        }

    fun triangle() =
        FlatMeshTriangle(mesh(), 0, 1, 2).apply { computeNormal(reverseNormal = false) }

    "flat mesh triangle hit through the interior records t and the face normal" {
        // From (0.25, 0.25, -1) toward +z the ray meets the triangle at (0.25, 0.25, 0), t = 1.
        val ray = Ray(Point3D(0.25, 0.25, -1.0), Vector3D(0.0, 0.0, 1.0))
        val sr = Hit(Double.MAX_VALUE)

        triangle().hit(ray, sr) shouldBe true
        sr.t shouldBeApprox 1.0
        sr.normal shouldBeApprox Normal.FORWARD
    }

    "flat mesh triangle misses a ray that passes beyond the opposite edge (beta + gamma > 1)" {
        // (0.8, 0.8, *) lies outside the hypotenuse x + y = 1, so beta + gamma > 1 rejects the hit.
        val ray = Ray(Point3D(0.8, 0.8, -1.0), Vector3D(0.0, 0.0, 1.0))

        triangle().hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
    }

    "flat mesh triangle misses a ray to the left of the v0-v2 edge (beta < 0)" {
        // Negative x puts the barycentric beta below zero.
        val ray = Ray(Point3D(-0.5, 0.25, -1.0), Vector3D(0.0, 0.0, 1.0))

        triangle().hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
    }

    "flat mesh triangle misses a ray below the v0-v1 edge (gamma < 0)" {
        // Negative y puts the barycentric gamma below zero.
        val ray = Ray(Point3D(0.25, -0.5, -1.0), Vector3D(0.0, 0.0, 1.0))

        triangle().hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
    }

    "flat mesh triangle misses a ray whose intersection is behind the origin (t < epsilon)" {
        // Aimed toward -z from in front of the triangle: the plane crossing is behind the ray, t < 0.
        val ray = Ray(Point3D(0.25, 0.25, 1.0), Vector3D(0.0, 0.0, 1.0))

        triangle().hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
    }
})
