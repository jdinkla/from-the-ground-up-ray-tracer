package net.dinkla.raytracer.objects

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import org.junit.jupiter.api.Assertions.assertTrue

internal class AlignedBoxTest : AnnotationSpec() {

    private val p = Point3D.ORIGIN
    private val q = Point3D.UNIT

    @Test
    fun hit() {
        val ab = AlignedBox(p, q)
        val ray = Ray(Point3D(0.5, 0.5, -1.0), Vector3D.FORWARD)
        val sr = Hit()
        val hit = ab.hit(ray, sr)
        hit shouldBe true
        assertTrue(hit)
        sr.t shouldBe 1.0
    }

    @Test
    fun shadowHit() {
        val ab = AlignedBox(p, q)
        val ray = Ray(Point3D(0.5, 0.5, -1.0), Vector3D.FORWARD)
        val sr = Hit()
        val hit = ab.shadowHit(ray, sr)
        hit shouldBe true
        sr.t shouldBe 1.0
    }

}