package net.dinkla.raytracer.utilities

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

internal class HistogramTest : StringSpec({

    "an empty histogram has no keys" {
        val h = Histogram()
        h.keys().size shouldBe 0
    }

    "adding one element" {
        val h = Histogram()
        h.add(3)
        h.keys().size shouldBe 1
        h[3] shouldBe 1
    }

    "adding one element twice" {
        val h = Histogram()
        h.add(3)
        h.add(3)
        h.keys().size shouldBe 1
        h[3] shouldBe 2
    }

    "adding two different elements" {
        val h = Histogram()
        h.add(3)
        h.add(4)
        h.keys().size shouldBe 2
        h[3] shouldBe 1
        h[4] shouldBe 1
    }

    "adding multiple different elements" {
        val h = Histogram()
        h.add(3)
        h.add(4)
        h.add(3)
        h.add(4)
        h.add(3)
        h.keys().size shouldBe 2
        h[3] shouldBe 3
        h[4] shouldBe 2
    }

    "keys returns the keys" {
        val h = Histogram()
        h.add(3)
        h.keys() shouldContainExactly setOf(3)
    }
})
