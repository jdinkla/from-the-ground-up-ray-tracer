package net.dinkla.raytracer.math

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe

internal class HistogramTest : AnnotationSpec() {

    @Test
    fun `an empty histogram has no keys`() {
        val h = Histogram()
        h.keys().size shouldBe 0
    }

    @Test
    fun `adding one element`() {
        val h = Histogram()
        h.add(3)
        h.keys().size shouldBe 1
        h[3] shouldBe 1
    }

    @Test
    fun `adding one element twice`() {
        val h = Histogram()
        h.add(3)
        h.add(3)
        h.keys().size shouldBe 1
        h[3] shouldBe 2
    }

    @Test
    fun `adding two different elements`() {
        val h = Histogram()
        h.add(3)
        h.add(4)
        h.keys().size shouldBe 2
        h[3] shouldBe 1
        h[4] shouldBe 1
    }

    @Test
    fun `adding multiple different elements`() {
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

    @Test
    fun `keys returns the keys`() {
        val h = Histogram()
        h.add(3)
        h.keys().size shouldBe 1
        h.keys() shouldBe setOf(3)
    }
}