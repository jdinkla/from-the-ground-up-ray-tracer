package net.dinkla.raytracer.utilities

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

internal class ResolutionTest :
    StringSpec({
        "calculate hres from vres" {
            Resolution(1080).width shouldBe 1920
        }

        "fromId resolves a known resolution id to its dimensions" {
            Resolution.fromId("1080p") shouldBe Resolution(1080)
        }

        "fromId fails fast on an unknown id, naming the bad value and listing valid options" {
            val ex = shouldThrow<IllegalArgumentException> { Resolution.fromId("999p") }

            ex.message shouldContain "999p"
            ex.message shouldContain "720p"
            ex.message shouldContain "1080p"
        }
    })
