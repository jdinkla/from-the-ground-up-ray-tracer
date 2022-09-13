package net.dinkla.raytracer.utilities

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe

internal class ResolutionTest : AnnotationSpec() {
    @Test
    fun `calculate hres from vres`() {
        Resolution(1080).hres shouldBe 1920
    }
}