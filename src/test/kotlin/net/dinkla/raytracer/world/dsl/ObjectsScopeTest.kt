package net.dinkla.raytracer.world.dsl

import net.dinkla.raytracer.assertType
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.materials.Matte
import net.dinkla.raytracer.math.AffineTransformation
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.objects.*
import net.dinkla.raytracer.objects.acceleration.Grid
import net.dinkla.raytracer.objects.acceleration.kdtree.KDTree
import net.dinkla.raytracer.objects.beveled.BeveledBox
import net.dinkla.raytracer.objects.compound.Box
import net.dinkla.raytracer.objects.compound.Compound
import net.dinkla.raytracer.objects.compound.SolidCylinder
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
    private val someVector3 = Vector3D(3.1, -2.1, 2.4)
    private val y0 = 1.0
    private val y1 = 2.0
    private val someDouble = 1.23

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
    fun `should handle alignedBox`() {
        // given
        val compound = Compound()
        val scope = ObjectsScope(materials, compound)
        val expected = AlignedBox(somePoint, somePoint2).apply {
            material = someMaterial
        }

        // when
        scope.alignedBox(material = someMaterialId, p = somePoint, q = somePoint2)

        // then
        assertType<GeometricObject, AlignedBox>(scope.objects, 0)
        val created = scope.objects[0] as AlignedBox
        assertEquals(expected, created)
    }

    @Test
    fun `should handle openCylinder`() {
        // given
        val compound = Compound()
        val scope = ObjectsScope(materials, compound)
        val expected = OpenCylinder(y0 = y0, y1 = y1, radius = someRadius).apply {
            material = someMaterial
        }

        // when
        scope.openCylinder(material = someMaterialId, y0 = y0, y1 = y1, radius = someRadius)

        // then
        assertType<GeometricObject, OpenCylinder>(scope.objects, 0)
        val created = scope.objects[0] as OpenCylinder
        assertEquals(expected, created)
    }

    @Test
    fun `should handle solidCylinder`() {
        // given
        val compound = Compound()
        val scope = ObjectsScope(materials, compound)
        val expected = SolidCylinder(y0 = y0, y1 = y1, radius = someRadius).apply {
            material = someMaterial
        }

        // when
        scope.solidCylinder(material = someMaterialId, y0 = y0, y1 = y1, radius = someRadius)

        // then
        assertType<GeometricObject, SolidCylinder>(scope.objects, 0)
        val created = scope.objects[0] as SolidCylinder
        assertEquals(expected, created)
    }

    @Test
    fun `should handle instance`() {
        // given
        val compound = Compound()
        val scope = ObjectsScope(materials, compound)
        val someOtherMaterial = Matte(Color.RED, 0.12, 0.34)
        val expected = Sphere(center = somePoint, radius = someRadius).apply {
            material = someOtherMaterial
        }
        val trans = AffineTransformation()
        trans.translate(someVector)

        // when
        scope.instance(material = someMaterialId, of = expected) {
            translate(someVector)
        }

        // then
        assertType<GeometricObject, Instance>(scope.objects, 0)
        val instance = scope.objects[0] as Instance
        assertEquals(trans, instance.trans)
        assertEquals(someMaterial, instance.material)
        assertEquals(someOtherMaterial, expected.material)
    }

    @Test
    fun `should handle box`() {
        // given
        val compound = Compound()
        val scope = ObjectsScope(materials, compound)
        val expected = Box(somePoint, someVector, someVector2, someVector3).apply {
            material = someMaterial
        }

        // when
        scope.box(material = someMaterialId, p0 = somePoint, a = someVector, b = someVector2, c = someVector3)

        // then
        assertType<GeometricObject, Box>(scope.objects, 0)
        val created = scope.objects[0] as Box
        assertEquals(expected, created)
    }

    @Test
    fun `should handle torus`() {
        // given
        val compound = Compound()
        val scope = ObjectsScope(materials, compound)
        val expected = Torus(y0, y1).apply {
            material = someMaterial
        }

        // when
        scope.torus(material = someMaterialId, a = y0, b = y1)

        // then
        assertType<GeometricObject, Torus>(scope.objects, 0)
        val created = scope.objects[0] as Torus
        assertEquals(expected, created)
    }

    @Test
    fun `should handle beveledBox`() {
        // given
        val compound = Compound()
        val scope = ObjectsScope(materials, compound)
        val expected = BeveledBox(somePoint, somePoint2, someDouble).apply {
            material = someMaterial
        }

        // when
        scope.beveledBox(material = someMaterialId, p0 = somePoint, p1 = somePoint2, rb = someDouble)

        // then
        assertType<GeometricObject, BeveledBox>(scope.objects, 0)
        val created = scope.objects[0] as BeveledBox
        assertEquals(expected, created)
    }

    @Test
    fun `should handle kdtree`() {
        // given
        val compound = Compound()
        val scope = ObjectsScope(materials, compound)

        // precondition
        assertEquals(0, scope.objects.size)

        // when
        scope.kdtree() {
            sphere(material = someMaterialId, center = somePoint, radius = someRadius)
        }

        // then
        assertType<GeometricObject, KDTree>(scope.objects, 0)
        val grid = scope.objects[0] as KDTree
        assertType<GeometricObject, Sphere>(grid.objects, 0)
        val sphere = grid.objects[0] as Sphere
        assertEquals(Sphere(somePoint, someRadius).apply { material = someMaterial }, sphere)
    }

    @Test
    fun `should handle x`() {
    }
}