package net.dinkla.raytracer.math

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe

internal class Vector2DTest : AnnotationSpec() {

    private val v0 = Vector2D(0.0, 0.0)
    private val v = Vector2D(2.0, 3.0)
    private val w = Vector2D(-2.0, -3.0)

    private val a = 3.0
    private val b = 5.0
    private val c = 7.0
    private val d = 11.0

    @Test
    fun `add vector`() {
        v + w shouldBe v0
    }

    @Test
    fun `subtract vector`() {
        v - v shouldBe v0
    }

    @Test
    fun `left scalar multiplication`() {
        val s = 2.0
        v * s shouldBe Vector2D(v.x * s, v.y * s)
    }

    @Test
    fun `right scalar multiplication`() {
        val s = 2.0
        s * v shouldBe Vector2D(v.x * s, v.y * s)
    }

    @Test
    fun `dot product with vector`() {
        Vector2D(a, b) dot Vector2D(c, d) shouldBe a * c + b * d
    }

    @Test
    fun `dot product with normal`() {
        Vector2D(a, b) dot Normal(c, d, 0.0) shouldBe a * c + b * d
    }

    @Test
    fun normalize() {
        val x = v.x / v.length()
        val y = v.y / v.length()
        v.normalize() shouldBe Vector2D(x, y)
    }

    @Test
    fun unaryMinus() {
        val v = Vector2D(a, b)
        -v shouldBe Vector2D(-a, -b)
    }

    @Test
    fun combined() {
        val v = Vector2D(1.0, 2.0)
        val w = Vector2D(3.0, -1.0)
        val x = Vector2D(-1.0, 1.0)
        val result = -v dot (2.0 * (w + x))
        result shouldBe -4.0
    }
}