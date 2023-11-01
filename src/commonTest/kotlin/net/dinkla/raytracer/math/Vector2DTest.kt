package net.dinkla.raytracer.math

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

internal class Vector2DTest : StringSpec({

    val v0 = Vector2D(0.0, 0.0)
    val v = Vector2D(2.0, 3.0)
    val w = Vector2D(-2.0, -3.0)

    val a = 3.0
    val b = 5.0
    val c = 7.0
    val d = 11.0

    "add vector" {
        v + w shouldBe v0
    }

    "subtract vector" {
        v - v shouldBe v0
    }

    "left scalar multiplication" {
        val s = 2.0
        v * s shouldBe Vector2D(v.x * s, v.y * s)
    }

    "right scalar multiplication" {
        val s = 2.0
        s * v shouldBe Vector2D(v.x * s, v.y * s)
    }

    "dot product with vector" {
        Vector2D(a, b) dot Vector2D(c, d) shouldBe a * c + b * d
    }

    "dot product with normal" {
        Vector2D(a, b) dot Normal(c, d, 0.0) shouldBe a * c + b * d
    }

    "normalize" {
        val x = v.x / v.length
        val y = v.y / v.length
        v.normalize() shouldBe Vector2D(x, y)
    }

    "unaryMinus" {
        val someVector = Vector2D(a, b)
        -someVector shouldBe Vector2D(-a, -b)
    }

    "combined" {
        // Given
        val vec = Vector2D(1.0, 2.0)
        val wec = Vector2D(3.0, -1.0)
        val xec = Vector2D(-1.0, 1.0)

        // When
        val result = -vec dot (2.0 * (wec + xec))

        // Then
        result shouldBe -4.0
    }
})
