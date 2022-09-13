package net.dinkla.raytracer.math

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe

internal class RayTest : AnnotationSpec() {

    private val origin = Point3D(1.0, 1.0, 1.0)
    private val direction = Vector3D.UP
    private val ray = Ray(origin, direction)

    @Test
    fun `construct from ray`() {
        val copy = Ray(ray)
        copy.origin shouldBe origin
        copy.direction shouldBe direction
    }

    @Test
    fun linear() {
        ray.linear(0.5) shouldBe origin + (direction * 0.5)
    }
}