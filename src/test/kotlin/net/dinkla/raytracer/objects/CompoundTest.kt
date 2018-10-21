package net.dinkla.raytracer.objects

import net.dinkla.raytracer.math.MathUtils
import net.dinkla.raytracer.objects.compound.Compound
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.assertEquals


class CompoundTest {

    @Test
    @Throws(Exception::class)
    fun testGetBoundingBox() {
        val s = Sphere(radius = 1.0)
        val c = Compound()
        c.add(s)

        val bboxC = c.boundingBox
        val bboxS = s.boundingBox

        assertEquals(bboxC.p, bboxS.p.minus(MathUtils.K_EPSILON))
        assertEquals(bboxC.q, bboxS.q.plus(MathUtils.K_EPSILON))
    }

}
