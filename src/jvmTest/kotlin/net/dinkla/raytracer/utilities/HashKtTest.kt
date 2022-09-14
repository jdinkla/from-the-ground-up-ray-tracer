package net.dinkla.raytracer.utilities

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldNotBe

class HashTest : StringSpec({

    "should hash to value other than zero" {
        hash(0) shouldNotBe 0
    }

    "should consider all arguments" {
        hash(1, 1) shouldNotBe hash(1)
    }

})
