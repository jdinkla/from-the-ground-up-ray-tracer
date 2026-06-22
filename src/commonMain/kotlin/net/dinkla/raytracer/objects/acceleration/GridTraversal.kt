package net.dinkla.raytracer.objects.acceleration

import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.MathUtils
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.objects.IGeometricObject
import net.dinkla.raytracer.objects.compound.Compound

/**
 * Shared, behaviour-preserving traversal logic for the uniform [Grid] and the map-backed
 * [SparseGrid]. The two grids differ only in how a cell is stored (dense array vs. sparse
 * map) and in which diagnostic [net.dinkla.raytracer.utilities.Counter] events they emit;
 * the ray/box slab maths and the 3D-DDA stepping are identical, so they live here.
 *
 * This is a straight extraction of the original inlined `hit()` bodies — no behaviour change.
 */
internal object GridTraversal {
    /**
     * The ray/box slab intersection (Shirley & Morley 2003): per-axis entry/exit t-values,
     * the overall entry [t0] / exit [t1], and the per-cell t-increments. [hits] is false when
     * the ray misses the box (`t0 > t1`).
     */
    class Slab(
        val txMin: Double,
        val tyMin: Double,
        val tzMin: Double,
        val txMax: Double,
        val tyMax: Double,
        val tzMax: Double,
        val t0: Double,
        val t1: Double,
        val dtx: Double,
        val dty: Double,
        val dtz: Double,
    ) {
        val hits: Boolean get() = t0 <= t1
    }

    /** Mutable 3D-DDA cursor over the grid cells. */
    class Walk(
        var ix: Int,
        var iy: Int,
        var iz: Int,
        var txNext: Double,
        var tyNext: Double,
        var tzNext: Double,
        val ixStep: Int,
        val iyStep: Int,
        val izStep: Int,
        val ixStop: Int,
        val iyStop: Int,
        val izStop: Int,
        val dtx: Double,
        val dty: Double,
        val dtz: Double,
    )

    fun computeSlab(
        ray: Ray,
        bbox: BBox,
        nx: Int,
        ny: Int,
        nz: Int,
    ): Slab {
        val ox = ray.origin.x
        val oy = ray.origin.y
        val oz = ray.origin.z

        val a = 1.0 / ray.direction.x
        val txMin: Double
        val txMax: Double
        if (a >= 0) {
            txMin = (bbox.p.x - ox) * a
            txMax = (bbox.q.x - ox) * a
        } else {
            txMin = (bbox.q.x - ox) * a
            txMax = (bbox.p.x - ox) * a
        }

        val b = 1.0 / ray.direction.y
        val tyMin: Double
        val tyMax: Double
        if (b >= 0) {
            tyMin = (bbox.p.y - oy) * b
            tyMax = (bbox.q.y - oy) * b
        } else {
            tyMin = (bbox.q.y - oy) * b
            tyMax = (bbox.p.y - oy) * b
        }

        val c = 1.0 / ray.direction.z
        val tzMin: Double
        val tzMax: Double
        if (c >= 0) {
            tzMin = (bbox.p.z - oz) * c
            tzMax = (bbox.q.z - oz) * c
        } else {
            tzMin = (bbox.q.z - oz) * c
            tzMax = (bbox.p.z - oz) * c
        }

        var t0 = if (txMin > tyMin) txMin else tyMin
        if (tzMin > t0) t0 = tzMin

        var t1 = if (txMax < tyMax) txMax else tyMax
        if (tzMax < t1) t1 = tzMax

        return Slab(
            txMin, tyMin, tzMin,
            txMax, tyMax, tzMax,
            t0, t1,
            (txMax - txMin) / nx,
            (tyMax - tyMin) / ny,
            (tzMax - tzMin) / nz,
        )
    }

    fun initialWalk(
        ray: Ray,
        bbox: BBox,
        slab: Slab,
        nx: Int,
        ny: Int,
        nz: Int,
    ): Walk {
        val x0 = bbox.p.x
        val y0 = bbox.p.y
        val z0 = bbox.p.z
        val x1 = bbox.q.x
        val y1 = bbox.q.y
        val z1 = bbox.q.z

        // does the ray start inside the grid?
        val start = if (bbox.isInside(ray.origin)) ray.origin else ray.linear(slab.t0)
        val ix = MathUtils.clamp((start.x - x0) * nx / (x1 - x0), 0.0, (nx - 1.0)).toInt()
        val iy = MathUtils.clamp((start.y - y0) * ny / (y1 - y0), 0.0, (ny - 1.0)).toInt()
        val iz = MathUtils.clamp((start.z - z0) * nz / (z1 - z0), 0.0, (nz - 1.0)).toInt()

        val (txNext, ixStep, ixStop) = axisStep(ray.direction.x, slab.txMin, ix, slab.dtx, nx)
        val (tyNext, iyStep, iyStop) = axisStep(ray.direction.y, slab.tyMin, iy, slab.dty, ny)
        val (tzNext, izStep, izStop) = axisStep(ray.direction.z, slab.tzMin, iz, slab.dtz, nz)

        return Walk(
            ix, iy, iz,
            txNext, tyNext, tzNext,
            ixStep, iyStep, izStep,
            ixStop, iyStop, izStop,
            slab.dtx, slab.dty, slab.dtz,
        )
    }

    /** Per-axis stepping setup: (next-crossing t, step direction, stop index). */
    private fun axisStep(
        dir: Double,
        tMin: Double,
        i: Int,
        dt: Double,
        n: Int,
    ): Triple<Double, Int, Int> =
        when {
            dir == 0.0 -> Triple(MathUtils.K_HUGE_VALUE, -1, -1)
            dir > 0 -> Triple(tMin + (i + 1) * dt, 1, n)
            else -> Triple(tMin + (n - i) * dt, -1, -1)
        }

    /** Copies a successful inner [Hit] into the caller's hit record, preserving Compound dispatch. */
    fun recordHit(
        sr: IHit,
        sr2: Hit,
        theObject: IGeometricObject,
    ) {
        sr.t = sr2.t
        sr.normal = sr2.normal
        sr.geometricObject = if (theObject !is Compound) theObject else sr2.geometricObject
    }

    /** Which axis the ray crosses first out of the current cell. */
    private enum class Axis { X, Y, Z }

    /**
     * Drives the 3D-DDA traversal. [cellAt] resolves a linear cell index to its object (or null
     * for an empty sparse cell); [onTraverse] is invoked once per visited cell (diagnostic
     * counter hook). Returns true on the first accepted hit, mutating [sr] in place.
     */
    fun traverse(
        ray: Ray,
        sr: IHit,
        walk: Walk,
        nx: Int,
        ny: Int,
        cellAt: (Int) -> IGeometricObject?,
        onTraverse: () -> Unit,
    ): Boolean {
        while (true) {
            onTraverse()
            val idx = walk.ix + nx * walk.iy + nx * ny * walk.iz
            val theObject = cellAt(idx)
            val axis = nextAxis(walk)
            if (acceptsHit(ray, sr, theObject, exitT(walk, axis))) {
                return true
            }
            if (stepOut(walk, axis)) {
                return false
            }
        }
    }

    private fun nextAxis(walk: Walk): Axis =
        if (walk.txNext < walk.tyNext && walk.txNext < walk.tzNext) {
            Axis.X
        } else if (walk.tyNext < walk.tzNext) {
            Axis.Y
        } else {
            Axis.Z
        }

    private fun exitT(
        walk: Walk,
        axis: Axis,
    ): Double =
        when (axis) {
            Axis.X -> walk.txNext
            Axis.Y -> walk.tyNext
            Axis.Z -> walk.tzNext
        }

    /** Tests the cell object and, on an accepted hit closer than the cell exit, records it into [sr]. */
    private fun acceptsHit(
        ray: Ray,
        sr: IHit,
        theObject: IGeometricObject?,
        exitT: Double,
    ): Boolean {
        if (theObject == null) {
            return false
        }
        val sr2 = Hit(sr.t)
        val accepted = theObject.hit(ray, sr2) && sr2.t < exitT
        if (accepted) {
            recordHit(sr, sr2, theObject)
        }
        return accepted
    }

    /** Advances the cursor along [axis]; returns true when it has stepped out of the grid. */
    private fun stepOut(
        walk: Walk,
        axis: Axis,
    ): Boolean =
        when (axis) {
            Axis.X -> {
                walk.txNext += walk.dtx
                walk.ix += walk.ixStep
                walk.ix == walk.ixStop
            }
            Axis.Y -> {
                walk.tyNext += walk.dty
                walk.iy += walk.iyStep
                walk.iy == walk.iyStop
            }
            Axis.Z -> {
                walk.tzNext += walk.dtz
                walk.iz += walk.izStep
                walk.iz == walk.izStop
            }
        }
}
