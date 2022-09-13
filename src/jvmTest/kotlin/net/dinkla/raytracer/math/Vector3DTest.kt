package net.dinkla.raytracer.math

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe

internal class Vector3DTest : AnnotationSpec() {

    private val v0 = Vector3D(0.0, 0.0, 0.0)
    private val v = Vector3D(2.0, 3.0, 5.0)
    private val w = Vector3D(-2.0, -3.0, -5.0)

    private val a = 3.0
    private val b = 5.0
    private val c = 7.0
    private val d = 11.0
    private val e = 13.0
    private val f = 17.0

    private val v1 = Vector3D(a, b, c)
    private val v2 = Vector3D(d, e, f)

    @Test
    fun `construct from integers`() {
        Vector3D(2.0, 3.0, 5.0) shouldBe v
    }

    @Test
    fun `construct from Point3D`() {
        Vector3D(Point3D(a, b, c)) shouldBe v1
    }

    @Test
    fun `add vector`() {
        v + w shouldBe v0
    }

    @Test
    fun `subtract vector`() {
        v - v shouldBe v0
    }

    @Test
    fun `right scalar multiplication`() {
        val s = 2.0
        v * s shouldBe Vector3D(v.x * s, v.y * s, v.z * s)
    }

    @Test
    fun `left scalar multiplication`() {
        val s = 2.0
        s*v shouldBe Vector3D(v.x * s, v.y * s, v.z * s)
    }

    @Test
    fun `dot product with vector`() {
        v1 dot v2 shouldBe a * d + b * e + c * f
    }

    @Test
    fun `dot product with normal`() {
        v1 dot Normal(d, e, f) shouldBe a * d + b * e + c * f
    }

    @Test
    fun `cross product`() {
        val expected = Vector3D(v1.y * v2.z - v1.z * v2.y, v1.z * v2.x - v1.x * v2.z, v1.x * v2.y - v1.y * v2.x)
        v1 cross v2 shouldBe expected
    }

    @Test
    fun normalize() {
        val x = v.x / v.length()
        val y = v.y / v.length()
        val z = v.z / v.length()
        v.normalize() shouldBe Vector3D(x, y, z)
    }

    @Test
    fun negate() {
        -w shouldBe v
    }

    @Test
    fun volume() {
        v1.volume() shouldBe a * b * c
    }

    @Test
    fun unaryMinus() {
        val v = Vector3D(a, b, -c)
        -v shouldBe Vector3D(-a, -b, c)
    }

}