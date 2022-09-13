package net.dinkla.raytracer.objects

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.doubles.shouldBeLessThan
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.MathUtils.K_EPSILON
import net.dinkla.raytracer.math.Normal.Companion.BACKWARD
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import kotlin.math.abs

internal class SphereTest : AnnotationSpec() {

    private val sphere = Sphere(Point3D.ORIGIN, 1.0)

    @Test
    fun boundingBox() {
        val bbox = sphere.boundingBox
        bbox.p shouldBe Point3D(-1.0, -1.0, -1.0)
        bbox.q shouldBe Point3D(1.0, 1.0, 1.0)
    }

    @Test
    fun hit() {
        val sr = Shade()
        val o = Point3D(0.0, 0.0, -2.0)
        val d = Vector3D(0.0, 0.0, 1.0)
        val ray = Ray(o, d)
        val isHit = sphere.hit(ray, sr);

        isHit shouldBe true
        abs(sr.t - 1.0) shouldBeLessThan K_EPSILON
        sr.normal shouldBe BACKWARD
    }
}