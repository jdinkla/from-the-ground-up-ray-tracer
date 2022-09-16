package net.dinkla.raytracer.objects

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.math.MathUtils.K_EPSILON
import net.dinkla.raytracer.objects.compound.Compound

class CompoundTest : StringSpec({
    "testGetBoundingBox" {
        val s = Sphere(radius = 1.0)
        val c = Compound()
        c.add(s)

        val bboxC = c.boundingBox
        val bboxS = s.boundingBox

        bboxC.p shouldBe bboxS.p.minus(K_EPSILON)
        bboxC.q shouldBe bboxS.q.plus(K_EPSILON)
    }
})
