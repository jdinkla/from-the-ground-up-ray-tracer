package net.dinkla.raytracer.math

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

@Suppress("EqualsNullCall")
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

        "createMax wraps Double.MAX_VALUE" {
            WrappedDouble.createMax().value shouldBe Double.MAX_VALUE
        }

        "isLessThan is true for a strictly smaller value" {
            WrappedDouble(1.0).isLessThan(WrappedDouble(2.0)) shouldBe true
        }

        "isLessThan is false for an equal value" {
            WrappedDouble(2.0).isLessThan(WrappedDouble(2.0)) shouldBe false
        }

        "isLessThan is false for a larger value" {
            WrappedDouble(3.0).isLessThan(WrappedDouble(2.0)) shouldBe false
        }

        "value can be updated after construction" {
            val f = WrappedDouble(1.0)
            f.value = 5.0
            f.value shouldBe 5.0
        }

        "equals is false against null" {
            (WrappedDouble(1.0).equals(null)) shouldBe false
        }

        "equals is false against a different type" {
            (WrappedDouble(1.0).equals("1.0")) shouldBe false
        }

        "equals is false for a different value" {
            WrappedDouble(1.0) shouldNotBe WrappedDouble(2.0)
        }

        "hashCode matches the wrapped value's hashCode" {
            WrappedDouble(1.23).hashCode() shouldBe 1.23.hashCode()
        }

        "toString renders the wrapped value" {
            WrappedDouble(1.23).toString() shouldBe "1.23"
        }
    })
