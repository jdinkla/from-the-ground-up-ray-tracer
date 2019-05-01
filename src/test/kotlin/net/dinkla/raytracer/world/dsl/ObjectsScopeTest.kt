package net.dinkla.raytracer.world.dsl

import net.dinkla.raytracer.assertType
import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.materials.Matte
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.objects.*
import net.dinkla.raytracer.objects.acceleration.Grid
import net.dinkla.raytracer.objects.compound.Compound
import net.dinkla.raytracer.objects.mesh.MeshTriangle
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class ObjectsScopeTest {

    private val somePoint = Point3D(1.0, 2.0, 3.0)
    private val somePoint2 = Point3D(1.1, 2.1, 4.1)
    private val somePoint3 = Point3D(1.2, 3.2, 5.2)
    private val someRadius = 12.3
    private val someNormal = Normal(5.0, 6.0, 7.0)
    private val someFileName = "resources/TwoTriangles.ply"
    private val someMaterialId = "material"
    private val someVector = Vector3D(1.0, 2.0, 3.0)
    private val someVector2 = Vector3D(1.1, 2.1, 4.1)

    private val someMaterial = Matte()
    private val materials = mapOf(Pair(someMaterialId, someMaterial))

    @Test
    fun `should handle sphere`() {
        // given
        val compound = Compound()
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
        val scope = ObjectsScope(materials, compound)
        val expected = Plane(somePoint, someNormal).apply {
            material = someMaterial
        }

        // when
        scope.plane(material = someMaterialId, point = somePoint, normal = someNormal)

        // then
        assertType<GeometricObject, Plane>(scope.objects, 0)
        val created = scope.objects[0] as Plane
        assertEquals(expected, created)
    }

    @Test
    fun `should handle triangle`() {
        // given
        val compound = Compound()
        val scope = ObjectsScope(materials, compound)
        val expected = Triangle(somePoint, somePoint2, somePoint3).apply {
            material = someMaterial
        }

        // when
        scope.triangle(material = someMaterialId, a = somePoint, b = somePoint2, c = somePoint3)

        // then
        assertType<GeometricObject, Triangle>(scope.objects, 0)
        val created = scope.objects[0] as Triangle
        assertEquals(expected, created)
    }

    @Test
    fun `should handle smoothTriangle`() {
        // given
        val compound = Compound()
        val scope = ObjectsScope(materials, compound)
        val expected = SmoothTriangle(somePoint, somePoint2, somePoint3).apply {
            material = someMaterial
        }

        // when
        scope.triangle(material = someMaterialId, a = somePoint, b = somePoint2, c = somePoint3, smooth = true)

        // then
        assertType<GeometricObject, SmoothTriangle>(scope.objects, 0)
        val created = scope.objects[0] as SmoothTriangle
        assertEquals(expected, created)
    }

    @Test
    fun `should handle ply`() {
        // given
        val compound = Compound()
        val scope = ObjectsScope(materials, compound)

        // when
        scope.ply(material = someMaterialId, fileName = someFileName)

        // then
        assertType<GeometricObject, Grid>(scope.objects, 0)
        val grid = scope.objects[0] as Grid
        assertEquals(2, grid.objects.size)
        assertType<GeometricObject, MeshTriangle>(grid.objects, 0)
        assertType<GeometricObject, MeshTriangle>(grid.objects, 1)
        assertEquals(someMaterial, grid.material)
    }

    @Test
    fun `should handle grid`() {
        // given
        val compound = Compound()
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
    fun `should handle disk`() {
        // given
        val compound = Compound()
        val scope = ObjectsScope(materials, compound)
        val expected = Disk(somePoint, radius = someRadius, normal = someNormal).apply {
            material = someMaterial
        }

        // when
        scope.disk(material = someMaterialId, center = somePoint, radius = someRadius, normal = someNormal)

        // then
        assertType<GeometricObject, Disk>(scope.objects, 0)
        val created = scope.objects[0] as Disk
        assertEquals(expected, created)
    }

    @Test
    fun `should handle rectangle`() {
        // given
        val compound = Compound()
        val scope = ObjectsScope(materials, compound)
        val expected = Rectangle(somePoint, someVector, someVector2).apply {
            material = someMaterial
        }

        // when
        scope.rectangle(material = someMaterialId, p0 = somePoint, a = someVector, b = someVector2)

        // then
        assertType<GeometricObject, Rectangle>(scope.objects, 0)
        val created = scope.objects[0] as Rectangle
        assertEquals(expected, created)
    }

    @Test
    fun `should handle x`() {
    }
}