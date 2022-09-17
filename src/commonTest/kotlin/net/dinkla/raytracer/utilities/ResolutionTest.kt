package net.dinkla.raytracer.utilities

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

internal class ResolutionTest : StringSpec({
    "calculate hres from vres" {
        Resolution(1080).width shouldBe 1920
    }
})
