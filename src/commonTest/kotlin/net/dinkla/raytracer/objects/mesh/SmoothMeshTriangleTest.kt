package net.dinkla.raytracer.objects.mesh

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.shouldBeGreaterThan
import io.kotest.matchers.doubles.shouldBeLessThan
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.shouldBeApprox

class SmoothMeshTriangleTest : StringSpec({
    // A right triangle in z = 0 (v0 = origin, v1 on +x, v2 on +y). Per-vertex normals drive smooth shading.
    fun mesh(
        n0: Normal,
        n1: Normal,
        n2: Normal,
    ) = Mesh().apply {
        vertices.add(Point3D(0.0, 0.0, 0.0))
        vertices.add(Point3D(1.0, 0.0, 0.0))
        vertices.add(Point3D(0.0, 1.0, 0.0))
        normals.add(n0)
        normals.add(n1)
        normals.add(n2)
    }

    "smooth mesh triangle with identical vertex normals returns that normal at any hit" {
        // All three vertex normals are +z, so the interpolated shading normal is +z everywhere.
        val triangle = SmoothMeshTriangle(mesh(Normal.FORWARD, Normal.FORWARD, Normal.FORWARD), 0, 1, 2)
        val ray = Ray(Point3D(0.25, 0.25, -1.0), Vector3D(0.0, 0.0, 1.0))
        val sr = Hit(Double.MAX_VALUE)

        triangle.hit(ray, sr) shouldBe true
        sr.t shouldBeApprox 1.0
        sr.normal shouldBeApprox Normal.FORWARD
    }

    "smooth mesh triangle interpolates between differing vertex normals" {
        // v0 tilts toward -x, v1 and v2 toward +x. A hit nearer v1/v2 (beta + gamma large) should produce
        // a shading normal whose x leans positive; the interpolated normal stays unit length.
        val triangle =
            SmoothMeshTriangle(
                mesh(Normal.LEFT, Normal.RIGHT, Normal.RIGHT),
                0,
                1,
                2,
            )
        val ray = Ray(Point3D(0.45, 0.45, -1.0), Vector3D(0.0, 0.0, 1.0))
        val sr = Hit(Double.MAX_VALUE)

        triangle.hit(ray, sr) shouldBe true
        sr.normal.x shouldBeGreaterThan 0.0 // weighted toward the +x vertices v1, v2
        sr.normal.length() shouldBeApprox 1.0
    }

    "smooth mesh triangle interpolation leans toward the v0 normal near v0" {
        // Close to v0 (small beta, small gamma) the -x vertex normal dominates, so the shading normal's x
        // turns negative.
        val triangle =
            SmoothMeshTriangle(
                mesh(Normal.LEFT, Normal.RIGHT, Normal.RIGHT),
                0,
                1,
                2,
            )
        val ray = Ray(Point3D(0.05, 0.05, -1.0), Vector3D(0.0, 0.0, 1.0))
        val sr = Hit(Double.MAX_VALUE)

        triangle.hit(ray, sr) shouldBe true
        sr.normal.x shouldBeLessThan 0.0
    }

    "smooth mesh triangle misses a ray outside the triangle" {
        val triangle = SmoothMeshTriangle(mesh(Normal.FORWARD, Normal.FORWARD, Normal.FORWARD), 0, 1, 2)
        val ray = Ray(Point3D(0.8, 0.8, -1.0), Vector3D(0.0, 0.0, 1.0))

        triangle.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
    }

    "smooth mesh triangle misses a ray whose intersection is behind the origin" {
        val triangle = SmoothMeshTriangle(mesh(Normal.FORWARD, Normal.FORWARD, Normal.FORWARD), 0, 1, 2)
        val ray = Ray(Point3D(0.25, 0.25, 1.0), Vector3D(0.0, 0.0, 1.0))

        triangle.hit(ray, Hit(Double.MAX_VALUE)) shouldBe false
    }
})
