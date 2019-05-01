package net.dinkla.raytracer.world.dsl

import net.dinkla.raytracer.assertType
import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.materials.Matte
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.objects.*
import net.dinkla.raytracer.objects.acceleration.Grid
import net.dinkla.raytracer.objects.compound.Compound
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class ObjectsScopeTest {

    private val somePoint = Point3D(1.0, 2.0, 3.0)
    private val somePoint2 = Point3D(1.1, 2.1, 4.1)
    private val somePoint3 = Point3D(1.2, 3.2, 5.2)
    private val someRadius = 12.3
    private val someNormal = Normal(5.0, 6.0, 7.0)
    val someMaterialId = "material"

    @Test
    fun `should handle sphere`() {
        // given
        val compound = Compound()
        val someMaterial = Matte()
        val materials = mapOf(Pair(someMaterialId, someMaterial))
        val scope = ObjectsScope(materials, compound)
        val expected = Sphere(somePoint, someRadius).apply {
            material = someMaterial
        }

        // when
        scope.sphere(material = someMaterialId, center = somePoint, radius = someRadius)

        // then
        assertEquals(1, scope.objects.size)
        val created = scope.objects[0] as Sphere
        assertEquals(expected, created)
    }

    @Test
    fun `should handle plane`() {
        // given
        val compound = Compound()
        val someMaterial = Matte()
        val materials = mapOf(Pair(someMaterialId, someMaterial))
        val scope = ObjectsScope(materials, compound)
        val expected = Plane(somePoint, someNormal).apply {
            material = someMaterial
        }

        // when
        scope.plane(material = someMaterialId, point = somePoint, normal = someNormal)

        // then
        assertEquals(1, scope.objects.size)
        val created = scope.objects[0] as Plane
        assertEquals(expected, created)
    }

    @Test
    fun `should handle triangle`() {
        // given
        val compound = Compound()
        val someMaterial = Matte()
        val materials = mapOf(Pair(someMaterialId, someMaterial))
        val scope = ObjectsScope(materials, compound)
        val expected = Triangle(somePoint, somePoint2, somePoint3).apply {
            material = someMaterial
        }

        // when
        scope.triangle(material = someMaterialId, a = somePoint, b = somePoint2, c = somePoint3)

        // then
        assertEquals(1, scope.objects.size)
        val created = scope.objects[0] as Triangle
        assertEquals(expected, created)
    }

    @Test
    fun `should handle smoothTriangle`() {
        // given
        val compound = Compound()
        val someMaterial = Matte()
        val materials = mapOf(Pair(someMaterialId, someMaterial))
        val scope = ObjectsScope(materials, compound)
        val expected = SmoothTriangle(somePoint, somePoint2, somePoint3).apply {
            material = someMaterial
        }

        // when
        scope.smoothTriangle(material = someMaterialId, a = somePoint, b = somePoint2, c = somePoint3)

        // then
        assertEquals(1, scope.objects.size)
        val created = scope.objects[0] as SmoothTriangle
        assertEquals(expected, created)
    }

    @Test
    fun `should handle ply`() {
    }

    @Test
    fun `should handle grid`() {
        // given
        val compound = Compound()
        val someMaterial = Matte()
        val materials = mapOf<String, IMaterial>(Pair(someMaterialId, someMaterial))
        val scope = ObjectsScope(materials, compound)

        // precondition
        assertEquals(0, scope.objects.size)

        // when
        scope.grid() {
            sphere(material = someMaterialId, center = somePoint, radius = someRadius)
        }

        // then
        assertType<GeometricObject, Grid>(scope.objects, 0)
        val grid = scope.objects[0] as Grid
        assertType<GeometricObject, Sphere>(grid.objects, 0)
        val sphere = grid.objects[0] as Sphere
        assertEquals(Sphere(somePoint, someRadius).apply { material = someMaterial }, sphere)
    }

    @Test
    fun `should handle x`() {
    }
}