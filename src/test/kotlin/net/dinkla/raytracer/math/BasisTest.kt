package net.dinkla.raytracer.math

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldNotBe

internal class BasisTest : AnnotationSpec() {

    private val eye = Point3D(1.0, 2.0, 3.0)
    private val lookAt = Point3D(3.0, 2.0, 1.0)
    private val up = Vector3D(0.0, 1.0, 0.0)
    private val b = Basis(eye, lookAt, up)

    @Test
    fun `construct an instance`() {
        b.u shouldNotBe null
        b.v shouldNotBe null
        b.w shouldNotBe null
    }
}