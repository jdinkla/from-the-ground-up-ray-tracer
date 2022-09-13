package net.dinkla.raytracer.math

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.math.Point3D.Companion.ORIGIN
import net.dinkla.raytracer.math.Point3D.Companion.UNIT

class BBoxTest : StringSpec({

    val p = ORIGIN
    val q = UNIT

    "should not construct if q < p" {
        shouldThrowAny {
            BBox(UNIT, ORIGIN)
        }
    }

    "should construct if p < q" {
        BBox(p, q) shouldBe BBox(Point3D(0.0, 0.0, 0.0), Point3D(1.0, 1.0, 1.0))
    }

})
