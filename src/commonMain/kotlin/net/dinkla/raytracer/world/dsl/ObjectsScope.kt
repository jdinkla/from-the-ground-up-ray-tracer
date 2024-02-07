package net.dinkla.raytracer.world.dsl

import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.objects.AlignedBox
import net.dinkla.raytracer.objects.Disk
import net.dinkla.raytracer.objects.GeometricObject
import net.dinkla.raytracer.objects.Instance
import net.dinkla.raytracer.objects.OpenCylinder
import net.dinkla.raytracer.objects.Plane
import net.dinkla.raytracer.objects.Rectangle
import net.dinkla.raytracer.objects.SmoothTriangle
import net.dinkla.raytracer.objects.Sphere
import net.dinkla.raytracer.objects.Torus
import net.dinkla.raytracer.objects.Triangle
import net.dinkla.raytracer.objects.acceleration.Acceleration
import net.dinkla.raytracer.objects.acceleration.Grid
import net.dinkla.raytracer.objects.acceleration.kdtree.KDTree
import net.dinkla.raytracer.objects.acceleration.kdtree.builder.SpatialMedianBuilder
import net.dinkla.raytracer.objects.acceleration.kdtree.builder.TreeBuilder
import net.dinkla.raytracer.objects.beveled.BeveledBox
import net.dinkla.raytracer.objects.compound.Box
import net.dinkla.raytracer.objects.compound.Compound
import net.dinkla.raytracer.objects.compound.SolidCylinder
import net.dinkla.raytracer.utilities.Ply

@Suppress("TooManyFunctions")
class ObjectsScope(internal val materials: Map<String, IMaterial>, private val compound: Compound) {

    private val mutableObjects: MutableList<GeometricObject> = mutableListOf()

    val objects: List<GeometricObject>
        get() = mutableObjects.toList()

    private fun GeometricObject.add() {
        mutableObjects.add(this)
        compound.add(this)
    }

    private fun GeometricObject.add(material: String) {
        this.material = materials[material]
        mutableObjects.add(this)
        compound.add(this)
    }

    fun alignedBox(
        material: String,
        p: Point3D = Point3D.ORIGIN,
        q: Point3D = Point3D.ORIGIN
    ) = AlignedBox(p, q).add(material)

    fun beveledBox(
        material: String,
        p0: Point3D = Point3D.ORIGIN,
        p1: Point3D = Point3D.ORIGIN,
        rb: Double = 0.0,
        isWiredFrame: Boolean = false
    ) = BeveledBox(p0, p1, rb, isWiredFrame).add(material)

    fun box(
        material: String,
        p0: Point3D = Point3D.ORIGIN,
        a: Vector3D = Vector3D.RIGHT,
        b: Vector3D = Vector3D.UP,
        c: Vector3D = Vector3D.FORWARD
    ) = Box(p0, a, b, c).add(material)

    fun disk(
        material: String,
        center: Point3D = Point3D.ORIGIN,
        radius: Double = 0.0,
        normal: Normal = Normal.UP
    ) = Disk(center, radius, normal).add(material)

    fun grid(block: ObjectsScope.() -> Unit) {
        val compound = Grid()
        val scope = ObjectsScope(materials, compound)
        scope.block()
        compound.add()
    }

    fun instance(
        material: String,
        of: GeometricObject,
        block: InstanceScope.() -> Unit
    ) {
        val instance = Instance(of)
        instance.apply {
            this.material = materials[material]
        }
        InstanceScope(instance).block()
        instance.add()
    }

    fun kdtree(builder: TreeBuilder = SpatialMedianBuilder(), block: ObjectsScope.() -> Unit) {
        val compound = KDTree(builder)
        val scope = ObjectsScope(materials, compound)
        scope.block()
        compound.add()
    }

    fun openCylinder(
        material: String,
        y0: Double = 0.0,
        y1: Double = 1.0,
        radius: Double = 0.0
    ) = OpenCylinder(y0, y1, radius).add(material)

    fun plane(
        material: String,
        point: Point3D = Point3D.ORIGIN,
        normal: Normal = Normal.UP
    ) = Plane(point, normal).add(material)

    fun ply(
        material: String,
        fileName: String,
        isSmooth: Boolean = false,
        reverseNormal: Boolean = false,
        type: Acceleration = Acceleration.GRID
    ) {
        val m = materials[material]!!
        val ply = Ply.fromFile(
            fileName = fileName,
            reverseNormal = reverseNormal,
            material = m,
            isSmooth = isSmooth,
            type = type
        )
        ply.compound.add()
    }

    fun rectangle(
        material: String,
        p0: Point3D = Point3D.ORIGIN,
        a: Vector3D = Vector3D.RIGHT,
        b: Vector3D = Vector3D.UP
    ) = Rectangle(p0, a, b).add(material)

    fun solidCylinder(material: String, y0: Double = 0.0, y1: Double = 1.0, radius: Double = 0.0) =
        SolidCylinder(y0, y1, radius).add(material)

    fun sphere(
        material: String,
        center: Point3D = Point3D.ORIGIN,
        radius: Double = 0.0
    ) = Sphere(center, radius).add(material)

    fun torus(
        material: String,
        a: Double = 1.0,
        b: Double = 1.0
    ) = Torus(a, b).add(material)

    fun triangle(material: String, a: Point3D, b: Point3D, c: Point3D, smooth: Boolean = false) = if (smooth) {
        SmoothTriangle(a, b, c).add(material)
    } else {
        Triangle(a, b, c).add(material)
    }

    fun smoothTriangle(material: String, a: Point3D, b: Point3D, c: Point3D) = SmoothTriangle(a, b, c).add(material)
}
