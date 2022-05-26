package net.dinkla.raytracer.world.dsl

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
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

internal class ObjectsScopeTest : AnnotationSpec() {

    private val somePoint = Point3D(1.0, 2.0, 3.0)
    private val somePoint2 = Point3D(1.1, 2.1, 4.1)
    private val somePoint3 = Point3D(1.2, 3.2, 5.2)
    private val someRadius = 12.3
    private val someNormal = Normal(5.0, 6.0, 7.0)
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
        val scope = ObjectsScope(materials, Compound())
        val expected = Sphere(somePoint, someRadius).apply {
            material = someMaterial
        }

        // when
        scope.sphere(material = someMaterialId, center = somePoint, radius = someRadius)

        // then
        scope.objects.size shouldBe 1
        scope.objects[0] as Sphere shouldBe expected
    }

    @Test
    fun `should handle plane`() {
        // given
        val scope = ObjectsScope(materials, Compound())
        val expected = Plane(somePoint, someNormal).apply {
            material = someMaterial
        }

        // when
        scope.plane(material = someMaterialId, point = somePoint, normal = someNormal)

        // then
        scope.objects.size shouldBe 1
        scope.objects[0].shouldBeInstanceOf<Plane>()
        scope.objects[0] as Plane shouldBe expected
    }

    @Test
    fun `should handle triangle`() {
        // given
        val scope = ObjectsScope(materials, Compound())
        val expected = Triangle(somePoint, somePoint2, somePoint3).apply {
            material = someMaterial
        }

        // when
        scope.triangle(material = someMaterialId, a = somePoint, b = somePoint2, c = somePoint3)

        // then
        scope.objects.size shouldBe 1
        scope.objects[0].shouldBeInstanceOf<Triangle>()
        scope.objects[0] as Triangle shouldBe expected
    }

    @Test
    fun `should handle smoothTriangle`() {
        // given
        val scope = ObjectsScope(materials, Compound())
        val expected = SmoothTriangle(somePoint, somePoint2, somePoint3).apply {
            material = someMaterial
        }

        // when
        scope.triangle(material = someMaterialId, a = somePoint, b = somePoint2, c = somePoint3, smooth = true)

        // then
        scope.objects.size shouldBe 1
        scope.objects[0].shouldBeInstanceOf<SmoothTriangle>()
        scope.objects[0] as SmoothTriangle shouldBe expected
    }

    @Test
    fun `should handle disk`() {
        // given
        val scope = ObjectsScope(materials, Compound())
        val expected = Disk(somePoint, radius = someRadius, normal = someNormal).apply {
            material = someMaterial
        }

        // when
        scope.disk(material = someMaterialId, center = somePoint, radius = someRadius, normal = someNormal)

        // then
        scope.objects.size shouldBe 1
        scope.objects[0].shouldBeInstanceOf<Disk>()
        scope.objects[0] as Disk shouldBe expected
    }

    @Test
    fun `should handle rectangle`() {
        // given
        val scope = ObjectsScope(materials, Compound())
        val expected = Rectangle(somePoint, someVector, someVector2).apply {
            material = someMaterial
        }

        // when
        scope.rectangle(material = someMaterialId, p0 = somePoint, a = someVector, b = someVector2)

        // then
        scope.objects.size shouldBe 1
        scope.objects[0].shouldBeInstanceOf<Rectangle>()
        scope.objects[0] as Rectangle shouldBe expected
    }

    @Test
    fun `should handle alignedBox`() {
        // given
        val scope = ObjectsScope(materials, Compound())
        val expected = AlignedBox(somePoint, somePoint2).apply {
            material = someMaterial
        }

        // when
        scope.alignedBox(material = someMaterialId, p = somePoint, q = somePoint2)

        // then
        scope.objects.size shouldBe 1
        scope.objects[0].shouldBeInstanceOf<AlignedBox>()
        scope.objects[0] as AlignedBox shouldBe expected
    }

    @Test
    fun `should handle openCylinder`() {
        // given
        val scope = ObjectsScope(materials, Compound())
        val expected = OpenCylinder(y0 = y0, y1 = y1, radius = someRadius).apply {
            material = someMaterial
        }

        // when
        scope.openCylinder(material = someMaterialId, y0 = y0, y1 = y1, radius = someRadius)

        // then
        scope.objects.size shouldBe 1
        scope.objects[0].shouldBeInstanceOf<OpenCylinder>()
        scope.objects[0] as OpenCylinder shouldBe expected
    }

    @Test
    fun `should handle solidCylinder`() {
        // given
        val scope = ObjectsScope(materials, Compound())
        val expected = SolidCylinder(y0 = y0, y1 = y1, radius = someRadius).apply {
            material = someMaterial
        }

        // when
        scope.solidCylinder(material = someMaterialId, y0 = y0, y1 = y1, radius = someRadius)

        // then
        scope.objects.size shouldBe 1
        scope.objects[0].shouldBeInstanceOf<SolidCylinder>()
        scope.objects[0] as SolidCylinder shouldBe expected
    }

    @Test
    fun `should handle box`() {
        // given
        val scope = ObjectsScope(materials, Compound())
        val expected = Box(somePoint, someVector, someVector2, someVector3).apply {
            material = someMaterial
        }

        // when
        scope.box(material = someMaterialId, p0 = somePoint, a = someVector, b = someVector2, c = someVector3)

        // then
        scope.objects.size shouldBe 1
        scope.objects[0].shouldBeInstanceOf<Box>()
        scope.objects[0] as Box shouldBe expected
    }

    @Test
    fun `should handle torus`() {
        // given
        val scope = ObjectsScope(materials, Compound())
        val expected = Torus(y0, y1).apply {
            material = someMaterial
        }

        // when
        scope.torus(material = someMaterialId, a = y0, b = y1)

        // then
        scope.objects.size shouldBe 1
        scope.objects[0].shouldBeInstanceOf<Torus>()
        scope.objects[0] as Torus shouldBe expected
    }

    @Test
    fun `should handle beveledBox`() {
        // given
        val scope = ObjectsScope(materials, Compound())
        val expected = BeveledBox(somePoint, somePoint2, someDouble).apply {
            material = someMaterial
        }

        // when
        scope.beveledBox(material = someMaterialId, p0 = somePoint, p1 = somePoint2, rb = someDouble)

        // then
        scope.objects.size shouldBe 1
        scope.objects[0].shouldBeInstanceOf<BeveledBox>()
        scope.objects[0] as BeveledBox shouldBe expected
    }

    @Test
    fun `should handle instance`() {
        // given
        val scope = ObjectsScope(materials, Compound())
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
        scope.objects.size shouldBe 1
        scope.objects[0].shouldBeInstanceOf<Instance>()
        val instance = scope.objects[0] as Instance
        instance.trans shouldBe trans
        instance.material shouldBe someMaterial
        expected.material shouldBe someOtherMaterial
    }

    @Test
    fun `should handle grid`() {
        // given
        val scope = ObjectsScope(materials, Compound())
        scope.objects.size shouldBe 0

        // when
        scope.grid() {
            sphere(material = someMaterialId, center = somePoint, radius = someRadius)
        }

        // then
        scope.objects.size shouldBe 1
        scope.objects[0].shouldBeInstanceOf<Grid>()
        val grid = scope.objects[0] as Grid
        grid.objects.size shouldBe 1
        grid.objects[0].shouldBeInstanceOf<Sphere>()
        val sphere = grid.objects[0] as Sphere
        sphere shouldBe Sphere(somePoint, someRadius).apply { material = someMaterial }
    }

    @Test
    fun `should handle kdtree`() {
        // given
        val scope = ObjectsScope(materials, Compound())
        scope.objects.size shouldBe 0

        // when
        scope.kdtree() {
            sphere(material = someMaterialId, center = somePoint, radius = someRadius)
        }

        // then
        scope.objects.size shouldBe 1
        scope.objects[0].shouldBeInstanceOf<KDTree>()
        val grid = scope.objects[0] as KDTree
        grid.objects.size shouldBe 1
        grid.objects[0].shouldBeInstanceOf<Sphere>()
        val sphere = grid.objects[0] as Sphere
        sphere shouldBe Sphere(somePoint, someRadius).apply { material = someMaterial }
    }

    @Test
    fun `should handle ply`() {
        // given
        val scope = ObjectsScope(materials, Compound())

        // when
        scope.ply(material = someMaterialId, fileName = "resources/TwoTriangles.ply")

        // then
        scope.objects.size shouldBe 1
        scope.objects[0].shouldBeInstanceOf<Grid>()
        val grid = scope.objects[0] as Grid
        grid.objects.size shouldBe 2
        grid.objects[0].shouldBeInstanceOf<MeshTriangle>()
        grid.objects[1].shouldBeInstanceOf<MeshTriangle>()
        grid.material shouldBe someMaterial
    }

}
