package net.dinkla.raytracer.math

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

internal class Point3DTest : StringSpec({

    val p = Point3D(2.0, 3.0, 5.0)
    val v = Vector3D(-2.0, -3.0, -5.0)

    val x = 1.0
    val y = 2.0
    val z = 3.0
    val e = Point3D(x, y, z)

    "add a vector" {
        p + v shouldBe Point3D.ORIGIN
    }

    "add a scalar" {
        p + 2.0 shouldBe Point3D(4.0, 5.0, 7.0)
    }

    "subtract a vector" {
        p - v shouldBe Point3D(4.0, 6.0, 10.0)
    }

    "subtract a point" {
        p - p shouldBe Vector3D.ZERO
    }

    "subtract a scalar" {
        p - 2.0 shouldBe Point3D(0.0, 1.0, 3.0)
    }

    "points with same values are equal" {
        p shouldBe Point3D(p.x, p.y, p.z)
    }

    "points with different values are not equal" {
        p shouldNotBe Point3D(0.0, p.y, p.z)
        p shouldNotBe Point3D(p.x, 0.0, p.z)
        p shouldNotBe Point3D(p.x, p.y, 0.0)
    }

    "equals" {
        p shouldBe Point3D(p.x, p.y, p.z)
        p shouldNotBe Point3D(0.0, p.y, p.z)
        p shouldNotBe Point3D(p.x, 0.0, p.z)
        p shouldNotBe Point3D(p.x, p.y, 0.0)
    }

    "a point is not equal to a vector" {
        p shouldNotBe Vector3D(p.x, p.y, p.z)
    }

    "unaryMinus" {
        val somePoint = Point3D(v.x, v.y, -v.z)
        -somePoint shouldBe Vector3D(-v.x, -v.y, v.z)
    }

    "sqrDistance" {
        e.sqrDistance(Point3D(0.0, 1.0, 2.0)) shouldBe 1.0 + 1.0 + 1.0
    }

    "ith" {
        e.ith(Axis.X) shouldBe x
        e.ith(Axis.Y) shouldBe y
        e.ith(Axis.Z) shouldBe z
    }

    "times" {
        2.0 * e shouldBe Point3D(2.0 * e.x, 2.0 * e.y, 2.0 * e.z)
    }
})
