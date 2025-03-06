package net.dinkla.raytracer.math

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class WrappedDoubleTest :
    StringSpec({
        "testMethodParameter" {
            val f = WrappedDouble(1.23)
            //        set(f);
            f.value shouldBe 1.23
        }

        "testComparable" {
            val f1 = WrappedDouble(0.0)
            val f2 = WrappedDouble(0.0)
            val f3 = WrappedDouble(1.0)
            val f4 = WrappedDouble(2.0)

            f1 shouldBe f2
            f3.compareTo(f4) shouldBe -1
            f1.compareTo(f4) shouldBe -1
            f2.compareTo(f4) shouldBe -1
            f4.compareTo(f3) shouldBe 1
        }
    })
