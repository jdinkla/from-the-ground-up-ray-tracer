package net.dinkla.raytracer.world.dsl

import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.math.MathUtils
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.objects.AlignedBox
import net.dinkla.raytracer.objects.Annulus
import net.dinkla.raytracer.objects.ConcaveSphere
import net.dinkla.raytracer.objects.Disk
import net.dinkla.raytracer.objects.GeometricObject
import net.dinkla.raytracer.objects.Instance
import net.dinkla.raytracer.objects.OpenCone
import net.dinkla.raytracer.objects.OpenCylinder
import net.dinkla.raytracer.objects.PartAnnulus
import net.dinkla.raytracer.objects.PartCylinder
import net.dinkla.raytracer.objects.PartSphere
import net.dinkla.raytracer.objects.PartTorus
import net.dinkla.raytracer.objects.Plane
import net.dinkla.raytracer.objects.Rectangle
import net.dinkla.raytracer.objects.SmoothTriangle
import net.dinkla.raytracer.objects.Sphere
import net.dinkla.raytracer.objects.Torus
import net.dinkla.raytracer.objects.Triangle
import net.dinkla.raytracer.objects.acceleration.Acceleration
import net.dinkla.raytracer.objects.acceleration.Grid
import net.dinkla.raytracer.objects.acceleration.SparseGrid
import net.dinkla.raytracer.objects.acceleration.kdtree.KDTree
import net.dinkla.raytracer.objects.acceleration.kdtree.builder.SpatialMedianBuilder
import net.dinkla.raytracer.objects.acceleration.kdtree.builder.TreeBuilder
import net.dinkla.raytracer.objects.beveled.BeveledBox
import net.dinkla.raytracer.objects.beveled.BeveledCylinder
import net.dinkla.raytracer.objects.beveled.BeveledWedge
import net.dinkla.raytracer.objects.compound.Bowl
import net.dinkla.raytracer.objects.compound.Box
import net.dinkla.raytracer.objects.compound.Compound
import net.dinkla.raytracer.objects.compound.GlassOfWater
import net.dinkla.raytracer.objects.compound.SolidCone
import net.dinkla.raytracer.objects.compound.SolidCylinder
import net.dinkla.raytracer.objects.compound.ThickRing
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

    /**
     * Adds a solid cylinder of [radius] spanning the y-range [y0]..[y1] whose top and bottom edges are
     * rounded by a bevel of radius [rb] (torus rims).
     */
    fun beveledCylinder(
        material: String,
        y0: Double = 0.0,
        y1: Double = 1.0,
        radius: Double = 1.0,
        rb: Double = 0.1,
    ) = BeveledCylinder(y0, y1, radius, rb).add(material)

    /**
     * Adds a beveled wedge: an angular sector ([phiMin]..[phiMax] radians) of a thick tube spanning
     * radii [innerRadius]..[outerRadius] and the y-range [y0]..[y1], with its curved top/bottom edges
     * rounded by a bevel of radius [rb].
     */
    fun beveledWedge(
        material: String,
        y0: Double = 0.0,
        y1: Double = 1.0,
        innerRadius: Double = 0.5,
        outerRadius: Double = 1.0,
        phiMin: Double = 0.0,
        phiMax: Double = MathUtils.PI / 2.0,
        rb: Double = 0.1,
    ) = BeveledWedge(y0, y1, innerRadius, outerRadius, phiMin, phiMax, rb).add(material)

    /**
     * Adds a bowl: a thick hemispherical shell (opening upward) between an inner sphere of
     * [innerRadius] and an outer sphere of [outerRadius].
     */
    fun bowl(
        material: String,
        innerRadius: Double = 0.9,
        outerRadius: Double = 1.0,
    ) = Bowl(innerRadius, outerRadius).add(material)

    /**
     * Adds a concave sphere of [radius] centred at [center] — a sphere whose normal points inward,
     * used for environment/skylight domes that enclose the scene.
     */
    fun concaveSphere(
        material: String,
        center: Point3D = Point3D.ORIGIN,
        radius: Double = 1.0,
    ) = ConcaveSphere(center, radius).add(material)

    /** Adds a parallelepiped box at [p0] spanned by edge vectors [a], [b], [c]. */
    fun box(
        material: String,
        p0: Point3D = Point3D.ORIGIN,
        a: Vector3D = Vector3D.RIGHT,
        b: Vector3D = Vector3D.UP,
        c: Vector3D = Vector3D.FORWARD,
    ) = Box(p0, a, b, c).add(material)

    /**
     * Adds a glass of water (Suffern §28.7): a [GlassOfWater] compound whose boundary surfaces carry
     * three separate dielectric materials — [glassAir], [waterGlass] and [waterAir] — resolved from
     * the materials block by id. Unlike the other shape adders this object is added with its per-part
     * materials intact (it is *not* re-assigned a single material), because each boundary must keep the
     * dielectric describing the two media it separates. The geometry parameters match
     * [GlassOfWater]'s constructor and default to a unit-sized glass.
     */
    @SuppressWarnings("LongParameterList")
    fun glassOfWater(
        glassAir: String,
        waterGlass: String,
        waterAir: String,
        bottomY: Double = 0.0,
        innerBottomY: Double = 0.2,
        waterY: Double = 1.4,
        topY: Double = 2.0,
        innerRadius: Double = 0.9,
        outerRadius: Double = 1.0,
        meniscusRadius: Double = 0.06,
    ) {
        val glass =
            GlassOfWater(
                glassAir = requireMaterial(glassAir),
                waterGlass = requireMaterial(waterGlass),
                waterAir = requireMaterial(waterAir),
                bottomY = bottomY,
                innerBottomY = innerBottomY,
                waterY = waterY,
                topY = topY,
                innerRadius = innerRadius,
                outerRadius = outerRadius,
                meniscusRadius = meniscusRadius,
            )
        glass.add()
    }

    /** Resolves [id] in [materials], throwing a clear error when the material was never declared. */
    private fun requireMaterial(id: String): IMaterial =
        requireNotNull(materials[id]) { "Material '$id' not found in materials map" }

    /** Adds a flat disk of the given [radius] centred at [center] facing [normal]. */
    fun disk(
        material: String,
        center: Point3D = Point3D.ORIGIN,
        radius: Double = 0.0,
        normal: Normal = Normal.UP,
    ) = Disk(center, radius, normal).add(material)

    /**
     * Adds a flat ring (annulus) centred at [center] facing [normal], spanning radii
     * [innerRadius]..[outerRadius] (the hole has radius [innerRadius]).
     */
    fun annulus(
        material: String,
        center: Point3D = Point3D.ORIGIN,
        innerRadius: Double = 0.0,
        outerRadius: Double = 1.0,
        normal: Normal = Normal.UP,
    ) = Annulus(center, innerRadius, outerRadius, normal).add(material)

    /**
     * Adds a part-annulus: a flat ring centred at [center] facing [normal], spanning radii
     * [innerRadius]..[outerRadius], restricted to the azimuth wedge [phiMin]..[phiMax] (radians).
     */
    fun partAnnulus(
        material: String,
        center: Point3D = Point3D.ORIGIN,
        innerRadius: Double = 0.0,
        outerRadius: Double = 1.0,
        normal: Normal = Normal.UP,
        phiMin: Double = 0.0,
        phiMax: Double = 2.0 * MathUtils.PI,
    ) = PartAnnulus(center, innerRadius, outerRadius, normal, phiMin, phiMax).add(material)

    /**
     * Adds an open cone (lateral surface only) with base radius [radius] at `y = 0` and apex at
     * `(0, height, 0)`.
     */
    fun openCone(
        material: String,
        height: Double = 1.0,
        radius: Double = 1.0,
    ) = OpenCone(height, radius).add(material)

    /**
     * Adds a part-cylinder: an open cylinder of [radius] spanning y-range [y0]..[y1] restricted to
     * the azimuth wedge [phiMin]..[phiMax] (radians).
     */
    fun partCylinder(
        material: String,
        y0: Double = 0.0,
        y1: Double = 1.0,
        radius: Double = 1.0,
        phiMin: Double = 0.0,
        phiMax: Double = 2.0 * MathUtils.PI,
    ) = PartCylinder(y0, y1, radius, phiMin, phiMax).add(material)

    /**
     * Adds a part-sphere of [radius] centred at [center] restricted to the azimuth wedge
     * [phiMin]..[phiMax] and the polar band [thetaMin]..[thetaMax] (all radians).
     */
    fun partSphere(
        material: String,
        center: Point3D = Point3D.ORIGIN,
        radius: Double = 1.0,
        phiMin: Double = 0.0,
        phiMax: Double = 2.0 * MathUtils.PI,
        thetaMin: Double = 0.0,
        thetaMax: Double = MathUtils.PI,
    ) = PartSphere(center, radius, phiMin, phiMax, thetaMin, thetaMax).add(material)

    /**
     * Adds a part-torus with sweep radius [a] and tube radius [b] restricted to the azimuth wedge
     * [phiMin]..[phiMax] (radians).
     */
    fun partTorus(
        material: String,
        a: Double = 1.0,
        b: Double = 1.0,
        phiMin: Double = 0.0,
        phiMax: Double = 2.0 * MathUtils.PI,
    ) = PartTorus(a, b, phiMin, phiMax).add(material)

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
     * Collects the objects declared in [block] into a [SparseGrid] acceleration structure — a [Grid]
     * variant that stores only its non-empty cells in a map — then adds it to this scope as a single
     * object.
     */
    fun sparseGrid(block: ObjectsScope.() -> Unit) {
        val compound = SparseGrid()
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

    /**
     * Adds a closed (solid) cone with base radius [radius] at `y = 0`, apex at `(0, height, 0)`,
     * capped by a base disk.
     */
    fun solidCone(
        material: String,
        height: Double = 1.0,
        radius: Double = 1.0,
    ) = SolidCone(height, radius).add(material)

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

    /**
     * Adds a thick ring (a capped tube): the solid region between two coaxial cylinders of radii
     * [innerRadius]..[outerRadius] spanning the y-range [y0]..[y1].
     */
    fun thickRing(
        material: String,
        y0: Double = 0.0,
        y1: Double = 1.0,
        innerRadius: Double = 0.5,
        outerRadius: Double = 1.0,
    ) = ThickRing(y0, y1, innerRadius, outerRadius).add(material)

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
