package net.dinkla.raytracer.math

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

internal class Vector3DTest : StringSpec({

    val v0 = Vector3D(0.0, 0.0, 0.0)
    val v = Vector3D(2.0, 3.0, 5.0)
    val w = Vector3D(-2.0, -3.0, -5.0)

    val a = 3.0
    val b = 5.0
    val c = 7.0
    val d = 11.0
    val e = 13.0
    val f = 17.0

    val v1 = Vector3D(a, b, c)
    val v2 = Vector3D(d, e, f)

    "construct from integers" {
        Vector3D(2.0, 3.0, 5.0) shouldBe v
    }

    "construct from Point3D" {
        Vector3D(Point3D(a, b, c)) shouldBe v1
    }

    "add vector" {
        v + w shouldBe v0
    }

    "subtract vector" {
        v - v shouldBe v0
    }

    "right scalar multiplication" {
        val s = 2.0
        v * s shouldBe Vector3D(v.x * s, v.y * s, v.z * s)
    }

    "left scalar multiplication" {
        val s = 2.0
        s * v shouldBe Vector3D(v.x * s, v.y * s, v.z * s)
    }

    "dot product with vector" {
        v1 dot v2 shouldBe a * d + b * e + c * f
    }

    "dot product with normal" {
        v1 dot Normal(d, e, f) shouldBe a * d + b * e + c * f
    }

    "cross product" {
        val expected = Vector3D(v1.y * v2.z - v1.z * v2.y, v1.z * v2.x - v1.x * v2.z, v1.x * v2.y - v1.y * v2.x)
        v1 cross v2 shouldBe expected
    }

    "normalize" {
        val x = v.x / v.length()
        val y = v.y / v.length()
        val z = v.z / v.length()
        v.normalize() shouldBe Vector3D(x, y, z)
    }

    "negate" {
        -w shouldBe v
    }

    "volume" {
        v1.volume() shouldBe a * b * c
    }

    "unaryMinus" {
        val v = Vector3D(a, b, -c)
        -v shouldBe Vector3D(-a, -b, c)
    }
})