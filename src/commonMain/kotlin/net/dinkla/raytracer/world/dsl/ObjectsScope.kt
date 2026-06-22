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

/**
 * DSL receiver for the `objects { ... }` block. Each shape function constructs a primitive, looks up
 * its material id in [materials] (populated by the `materials { ... }` block), and adds it both to a
 * local list and to the enclosing [compound] (the world's root container or a nested grid/kd-tree).
 *
 * The nesting functions [grid], [kdtree] and [instance] open a child scope so objects declared inside
 * them are collected into a sub-container instead of the root compound.
 */
@Suppress("TooManyFunctions")
class ObjectsScope(
    internal val materials: Map<String, IMaterial>,
    private val compound: Compound,
) {
    private val mutableObjects: MutableList<GeometricObject> = mutableListOf()

    /** The objects declared so far at this scope level, as an immutable snapshot. */
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

    /** Adds an axis-aligned box spanning corners [p] and [q]. */
    fun alignedBox(
        material: String,
        p: Point3D = Point3D.ORIGIN,
        q: Point3D = Point3D.ORIGIN,
    ) = AlignedBox(p, q).add(material)

    /** Adds a box with rounded (beveled) edges of radius [rb] between corners [p0] and [p1]. */
    fun beveledBox(
        material: String,
        p0: Point3D = Point3D.ORIGIN,
        p1: Point3D = Point3D.ORIGIN,
        rb: Double = 0.0,
        isWiredFrame: Boolean = false,
    ) = BeveledBox(p0, p1, rb, isWiredFrame).add(material)

    /** Adds a parallelepiped box at [p0] spanned by edge vectors [a], [b], [c]. */
    fun box(
        material: String,
        p0: Point3D = Point3D.ORIGIN,
        a: Vector3D = Vector3D.RIGHT,
        b: Vector3D = Vector3D.UP,
        c: Vector3D = Vector3D.FORWARD,
    ) = Box(p0, a, b, c).add(material)

    /** Adds a flat disk of the given [radius] centred at [center] facing [normal]. */
    fun disk(
        material: String,
        center: Point3D = Point3D.ORIGIN,
        radius: Double = 0.0,
        normal: Normal = Normal.UP,
    ) = Disk(center, radius, normal).add(material)

    /**
     * Collects the objects declared in [block] into a uniform [Grid] acceleration structure, which is
     * then added to this scope as a single object.
     */
    fun grid(block: ObjectsScope.() -> Unit) {
        val compound = Grid()
        val scope = ObjectsScope(materials, compound)
        scope.block()
        compound.add()
    }

    /**
     * Adds a transformed [Instance] of the geometry [of] with the given [material]; [block] runs
     * against an [InstanceScope] to apply the affine transformation (translate/rotate/scale).
     */
    fun instance(
        material: String,
        of: GeometricObject,
        block: InstanceScope.() -> Unit,
    ) {
        val instance = Instance(of)
        instance.apply {
            this.material = materials[material]
        }
        InstanceScope(instance).block()
        instance.add()
    }

    /**
     * Collects the objects declared in [block] into a [KDTree] built with [builder]
     * (default [SpatialMedianBuilder]), then adds the tree to this scope as a single object.
     */
    fun kdtree(
        builder: TreeBuilder = SpatialMedianBuilder(),
        block: ObjectsScope.() -> Unit,
    ) {
        val compound = KDTree(builder)
        val scope = ObjectsScope(materials, compound)
        scope.block()
        compound.add()
    }

    /** Adds an open (capless) cylinder of the given [radius] spanning the y-range [y0]..[y1]. */
    fun openCylinder(
        material: String,
        y0: Double = 0.0,
        y1: Double = 1.0,
        radius: Double = 0.0,
    ) = OpenCylinder(y0, y1, radius).add(material)

    /** Adds an infinite plane through [point] with the given surface [normal]. */
    fun plane(
        material: String,
        point: Point3D = Point3D.ORIGIN,
        normal: Normal = Normal.UP,
    ) = Plane(point, normal).add(material)

    /**
     * Loads a triangle mesh from the PLY file [fileName], assigns it [material], and adds it wrapped in
     * the [type] acceleration structure (default [Acceleration.GRID]). [isSmooth] selects smooth-shaded
     * triangles with interpolated normals; [reverseNormal] flips face orientation. Throws if [material]
     * is not declared.
     */
    fun ply(
        material: String,
        fileName: String,
        isSmooth: Boolean = false,
        reverseNormal: Boolean = false,
        type: Acceleration = Acceleration.GRID,
    ) {
        val m = requireNotNull(materials[material]) { "Material '$material' not found in materials map" }
        val ply =
            Ply.fromFile(
                fileName = fileName,
                reverseNormal = reverseNormal,
                material = m,
                isSmooth = isSmooth,
                type = type,
            )
        ply.compound.add()
    }

    /** Adds a rectangle at corner [p0] spanned by edge vectors [a] and [b]. */
    fun rectangle(
        material: String,
        p0: Point3D = Point3D.ORIGIN,
        a: Vector3D = Vector3D.RIGHT,
        b: Vector3D = Vector3D.UP,
    ) = Rectangle(p0, a, b).add(material)

    /** Adds a closed (capped) cylinder of the given [radius] spanning the y-range [y0]..[y1]. */
    fun solidCylinder(
        material: String,
        y0: Double = 0.0,
        y1: Double = 1.0,
        radius: Double = 0.0,
    ) = SolidCylinder(y0, y1, radius).add(material)

    /** Adds a sphere of the given [radius] centred at [center]. */
    fun sphere(
        material: String,
        center: Point3D = Point3D.ORIGIN,
        radius: Double = 0.0,
    ) = Sphere(center, radius).add(material)

    /** Adds a torus with sweep radius [a] and tube radius [b], centred at the origin in the xz-plane. */
    fun torus(
        material: String,
        a: Double = 1.0,
        b: Double = 1.0,
    ) = Torus(a, b).add(material)

    /**
     * Adds a triangle with vertices [a], [b], [c]. When [smooth] is true a [SmoothTriangle]
     * (interpolated vertex normals) is used instead of a flat-shaded [Triangle].
     */
    fun triangle(
        material: String,
        a: Point3D,
        b: Point3D,
        c: Point3D,
        smooth: Boolean = false,
    ) = if (smooth) {
        SmoothTriangle(a, b, c).add(material)
    } else {
        Triangle(a, b, c).add(material)
    }

    /** Adds a smooth-shaded triangle (interpolated vertex normals) with vertices [a], [b], [c]. */
    fun smoothTriangle(
        material: String,
        a: Point3D,
        b: Point3D,
        c: Point3D,
    ) = SmoothTriangle(a, b, c).add(material)
}
