package net.dinkla.raytracer.objects

import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class AlignedBoxTest {

    private val p = Point3D.ORIGIN
    private val q = Point3D.UNIT

    @Test
    fun hit() {
        val ab = AlignedBox(p, q)
        val ray = Ray(Point3D(0.5, 0.5, -1.0), Vector3D.FORWARD)
        val sr = Hit()
        val hit = ab.hit(ray, sr)
        assertTrue(hit)
        assertEquals(1.0, sr.t)
    }

    @Test
    fun shadowHit() {
        val ab = AlignedBox(p, q)
        val ray = Ray(Point3D(0.5, 0.5, -1.0), Vector3D.FORWARD)
        val sr = Hit()
        val hit = ab.shadowHit(ray, sr)
        assertTrue(hit)
        assertEquals(1.0, sr.t)
    }

}