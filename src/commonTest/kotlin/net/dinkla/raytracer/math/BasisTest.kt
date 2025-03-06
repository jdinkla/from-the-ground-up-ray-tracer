package net.dinkla.raytracer.math

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldNotBe

internal class BasisTest :
    StringSpec({

        val eye = Point3D(1.0, 2.0, 3.0)
        val lookAt = Point3D(3.0, 2.0, 1.0)
        val up = Vector3D(0.0, 1.0, 0.0)

        "construct an instance" {
            val b = Basis.create(eye, lookAt, up)
            b.u shouldNotBe null
            b.v shouldNotBe null
            b.w shouldNotBe null
        }
    })
