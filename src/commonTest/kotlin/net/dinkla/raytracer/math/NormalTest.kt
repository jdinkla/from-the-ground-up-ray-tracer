package net.dinkla.raytracer.math

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

internal class NormalTest : StringSpec({

    val x = 1.0
    val y = 2.0
    val z = 3.0
    val d = 2.0

    val n = Normal(x, y, z)
    val v = Vector3D(x, y, z)

    "construct from integers" {
        Normal(1.0, 2.0, 3.0) shouldBe n
    }

    "construct from numbers" {
        n.x shouldBe x
        n.y shouldBe y
        n.z shouldBe z
    }

    "construct from vector" {
        val normal = Normal.create(v)
        val len = v.length
        normal.x shouldBe x / len
        normal.y shouldBe y / len
        normal.z shouldBe z / len
    }

    "construct from three points" {
        val p0 = Point3D(x, y, z)
        val p1 = Point3D(y, z, x)
        val p2 = Point3D(z, x, y)
        val normal = Normal.create(p0, p1, p2)
        val otherNormal = Normal.create(((p1 - p0) cross (p2 - p0)).normalize())
        normal shouldBe otherNormal
    }

    "plus" {
        n + n shouldBe Vector3D(x + x, y + y, z + z)
    }

    "times" {
        n * d shouldBe Vector3D(d * x, d * y, d * z)
    }

    "dot" {
        n dot v shouldBe x * x + y * y + z * z
    }

    "normalize" {
        val l = n.length()
        n.normalize() shouldBe Normal(x / l, y / l, z / l)
    }

    "negate" {
        -n shouldBe Normal(-x, -y, -z)
    }

    "toVector3D" {
        n.toVector3D() shouldBe v
    }
})
