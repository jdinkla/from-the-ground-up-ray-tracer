package net.dinkla.raytracer.objects.acceleration

import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.hits.ShadowHit
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.MathUtils
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.objects.IGeometricObject
import net.dinkla.raytracer.objects.NullObject
import net.dinkla.raytracer.objects.compound.Compound
import net.dinkla.raytracer.utilities.Counter
import net.dinkla.raytracer.utilities.Histogram
import net.dinkla.raytracer.utilities.Logger
import kotlin.math.max
import kotlin.math.pow
import kotlin.time.TimeSource
import kotlin.time.measureTime

/**
 * A uniform (regular) grid acceleration structure.
 *
 * `Grid` owns the logic shared with [SparseGrid]: the grid-resolution heuristic, the object
 * insertion scaffold (clamped cell-index ranges, the triple-nested cell loop, the per-cell
 * counts/statistics) and the 3D-DDA traversal driving [hit]. Subclasses specialise **only the
 * cell storage** and the per-cell insertion/lookup that follows from it, via the small set of
 * `protected open` hooks below.
 *
 * The dense default stores one cell per array slot ([NullObject] for empty cells) and additionally
 * promotes crowded cells into nested grids; [SparseGrid] replaces the array with a map and drops
 * the promotion. No other behaviour differs.
 *
 * The promotion tuning ([factorSize], [maxDepth]) is supplied per instance as constructor
 * parameters rather than shared mutable global state, so concurrent renders cannot interfere with
 * one another's configuration. Nested sub-grids inherit the parent's tuning so multi-level nesting
 * behaves identically regardless of grid depth.
 */
@Suppress("TooManyFunctions")
open class Grid(
    protected val factorSize: Int = DEFAULT_FACTOR_SIZE,
    protected val maxDepth: Int = DEFAULT_MAX_DEPTH,
    private val maxNumCells: Int = DEFAULT_MAX_NUM_CELLS,
) : CompoundWithMesh() {
    private var cells: Array<IGeometricObject> = Array(0) { _ -> NullObject() }

    protected var nx: Int = 0
    protected var ny: Int = 0
    protected var nz: Int = 0

    var multiplier: Double = 2.0

    protected var depth: Int = 0

    init {
        boundingBox = BBox()
    }

    /**
     * Builds the grid: derives the cell resolution `nx*ny*nz` from the scene's bounding box and object
     * count (a standard density heuristic, scaled by [multiplier]), allocates the cells, and inserts
     * every object into each cell its bounding box overlaps. Idempotent — re-running on an already
     * initialised grid is a no-op.
     */
    override fun initialize() {
        if (isInitialized) {
            return
        }
        prepareInitialization()

        if (objects.size == 0) {
            return
        }

        val started = TimeSource.Monotonic.markNow()
        val bbox = boundingBox

        val wx = bbox.q.x - bbox.p.x
        val wy = bbox.q.y - bbox.p.y
        val wz = bbox.q.z - bbox.p.z

        val s = (wx * wy * wz / objects.size).pow(1.0 / 3)
        nx = (multiplier * wx / s + 1).toInt()
        ny = (multiplier * wy / s + 1).toInt()
        nz = (multiplier * wz / s + 1).toInt()

        clampResolutionToCellCap()

        val numCells = nx * ny * nz

        Logger.info("Grid: numCells=$numCells = $nx*$ny*$nz")

        allocateCells(numCells)

        val counts = IntArray(numCells)

        var objectsToGo = objects.size

        // insert the objects into the cells
        for (`object` in objects) {
            if (objectsToGo % logInterval == 0) {
                Logger.info("Grid: $objectsToGo objects to grid")
            }
            objectsToGo--

            val objBbox = `object`.boundingBox

            val ixmin = MathUtils.clamp((objBbox.p.x - bbox.p.x) * nx / wx, 0.0, (nx - 1.0)).toInt()
            val iymin = MathUtils.clamp((objBbox.p.y - bbox.p.y) * ny / wy, 0.0, (ny - 1.0)).toInt()
            val izmin = MathUtils.clamp((objBbox.p.z - bbox.p.z) * nz / wz, 0.0, (nz - 1.0)).toInt()

            val ixmax = MathUtils.clamp((objBbox.q.x - bbox.p.x) * nx / wx, 0.0, (nx - 1.0)).toInt()
            val iymax = MathUtils.clamp((objBbox.q.y - bbox.p.y) * ny / wy, 0.0, (ny - 1.0)).toInt()
            val izmax = MathUtils.clamp((objBbox.q.z - bbox.p.z) * nz / wz, 0.0, (nz - 1.0)).toInt()

            for (iz in izmin..izmax) {
                for (iy in iymin..iymax) {
                    for (ix in ixmin..ixmax) {
                        val index = iz * nx * ny + iy * nx + ix
                        insertIntoCell(index, `object`)
                        counts[index]++
                    }
                }
            }
        }

        Logger.info("Creating grid took ${started.elapsedNow()}")

        initializeSubcells()

        statistics(numCells, counts)
    }

    /**
     * Guards against a pathological cell count (a huge bounding box relative to the object count, or
     * an extreme [multiplier]) driving the `Array(numCells)` allocation in [allocateCells] to exhaust
     * the heap. If the derived `nx*ny*nz` exceeds [maxNumCells], the resolution is scaled down
     * uniformly via [clampGridResolution] so the product fits under the cap, and a warning is logged.
     * The grid still builds — only coarser. The cap is set far above any realistic scene, so normal
     * renders are never clamped and their grid resolution is unchanged.
     */
    private fun clampResolutionToCellCap() {
        val (cx, cy, cz) = clampGridResolution(nx, ny, nz, maxNumCells)
        if (cx != nx || cy != ny || cz != nz) {
            Logger.warn(
                "Grid: cell count ${nx.toLong() * ny * nz} ($nx*$ny*$nz) exceeds cap $maxNumCells; " +
                    "clamping resolution to $cx*$cy*$cz to avoid excessive allocation",
            )
            nx = cx
            ny = cy
            nz = cz
        }
    }

    /**
     * Per-instance setup that precedes grid construction. The dense grid delegates to
     * [Compound.initialize] (recomputing the bounding box); [SparseGrid] only flags itself
     * initialised, deliberately keeping the bounding box it already had.
     */
    protected open fun prepareInitialization() {
        super.initialize()
    }

    /** Allocates the backing cell storage for [numCells] cells. */
    protected open fun allocateCells(numCells: Int) {
        cells = Array(numCells) { _ -> NullObject() }
    }

    /**
     * Inserts [object] into the cell at the linear [index]. The dense grid keeps a [NullObject]
     * placeholder in empty cells, wraps the second occupant in a [Compound], and promotes a
     * crowded [Compound] into a nested [Grid] once it exceeds [factorSize] (while under
     * [maxDepth]); [SparseGrid] overrides this with a map-backed, promotion-free variant.
     */
    protected open fun insertIntoCell(
        index: Int,
        `object`: IGeometricObject,
    ) {
        val current = cells[index]
        cells[index] =
            if (current.promotableToSubgrid() && current.objectCount() > factorSize && depth < maxDepth) {
                val g = Grid(factorSize, maxDepth, maxNumCells)
                g.add(current.childrenForRegrid())
                g.add(`object`)
                g.depth = depth + 1
                g
            } else {
                current.combineInCell(`object`)
            }
    }

    /**
     * A nested grid is itself a [Compound] but must never be re-promoted: once a cell holds a sub-grid
     * it absorbs further objects directly (the original `cells[index] is Grid` branch). Overriding back
     * to `false` keeps a crowded sub-grid out of the promotion path.
     */
    override fun promotableToSubgrid(): Boolean = false

    /** Recursively initialises any nested grids produced during insertion (dense grid only). */
    protected open fun initializeSubcells() {
        val duration =
            measureTime {
                for (go in cells) {
                    (go as? Grid)?.initialize()
                }
            }
        Logger.info("Creating subgrids took $duration")
    }

    /** Resolves a linear cell index to its occupant, or `null` for an empty cell. */
    protected open fun cellAt(index: Int): IGeometricObject? = cells[index]

    /**
     * Emits histogram statistics for the freshly built grid, but only for the top-level grid:
     * nested dense sub-grids (depth > 0) stay quiet. [SparseGrid] is never nested, so it always
     * reports here without needing to override — matching its original unconditional behaviour.
     */
    protected fun statistics(
        numCells: Int,
        counts: IntArray,
    ) {
        if (0 != depth) {
            return
        }
        val hist = Histogram()

        var numInCells = 0
        for (j in 0 until numCells) {
            val count = counts[j]
            numInCells += count
            hist.add(count)
        }

        Logger.info("Grid statistics")
        Logger.info(
            "multiplier=" + multiplier +
                ", numObjects=" + objects.size +
                ", numCells=" + numCells +
                ", numObjects in cells=" + numInCells,
        )

        for (key in hist.keys()) {
            val value = hist[key]
            Logger.info("Grid: " + key + ": " + value + " [" + value * 100.0 / numInCells + "%]")
        }
    }

    /**
     * Intersects [ray] with the grid using a 3D-DDA traversal (the Amanatides–Woo grid walk): after a
     * bounding-box and slab test it steps cell by cell along the ray via [GridTraversal], testing only
     * the objects in each visited cell and stopping at the first hit closer than the cell exit. The
     * cell-storage details are deferred to the [cellAt] hook so [SparseGrid] can reuse this unchanged.
     */
    override fun hit(
        ray: Ray,
        sr: IHit,
    ): Boolean {
        // if (depth > 0) return false;
        if (!boundingBox.isHit(ray)) {
            count("Grid.hit.bbox")
            return false
        }
        count("Grid.hit")

        val slab = GridTraversal.computeSlab(ray, boundingBox, nx, ny, nz)
        if (!slab.hits) {
            count("Grid.hit.t0>t1")
            return false
        }

        val walk = GridTraversal.initialWalk(ray, boundingBox, slab, nx, ny, nz)
        return GridTraversal.traverse(
            ray = ray,
            sr = sr,
            walk = walk,
            nx = nx,
            ny = ny,
            cellAt = { cellAt(it) },
            onTraverse = { count("Grid.hit.traverse") },
        )
    }

    /**
     * Diagnostic [Counter] hook. The dense grid records each [event]; [SparseGrid] overrides this
     * to a no-op, which is its only behavioural difference in [hit]/[shadowHit] beyond cell storage.
     */
    protected open fun count(event: String) {
        Counter.count(event)
    }

    /** Shadow-ray test: runs the same grid traversal as [hit] and reports whether anything was struck. */
    override fun shadowHit(
        ray: Ray,
        tmin: ShadowHit,
    ): Boolean {
        count("Grid.shadowHit")
        val h = Hit(tmin.t)
        val b = hit(ray, h)
        tmin.t = h.t
        return b
    }

    override fun toString(): String = "Grid(#objs=${objects.size})"

    companion object {
        internal const val logInterval = 1000

        /** Default object-count threshold above which a crowded cell is promoted to a sub-grid. */
        private const val DEFAULT_FACTOR_SIZE = 500

        /** Default maximum nesting depth for sub-grid promotion (0 disables nesting). */
        private const val DEFAULT_MAX_DEPTH = 0

        /**
         * Default upper bound on the number of grid cells (`nx*ny*nz`). At ~64 million cells the
         * backing `Array<IGeometricObject>` plus the parallel `IntArray` of counts cost on the order
         * of half a gigabyte, which is the point past which a pathological scene risks an
         * `OutOfMemoryError`. Real scenes derive cell counts orders of magnitude below this (the
         * heuristic targets roughly a few cells per object), so the cap never engages for them.
         */
        const val DEFAULT_MAX_NUM_CELLS: Int = 64 * 1024 * 1024

        /**
         * Scales a grid resolution `nx*ny*nz` down uniformly so its product does not exceed [cap],
         * leaving it untouched when already within bounds. Each dimension stays at least 1. Pure and
         * side-effect free so the clamping rule can be unit-tested in isolation; [clampResolutionToCellCap]
         * is the sole production caller. The product is computed in [Long] arithmetic to stay correct
         * even when an [Int] `nx*ny*nz` would overflow.
         */
        internal fun clampGridResolution(
            nx: Int,
            ny: Int,
            nz: Int,
            cap: Int,
        ): Triple<Int, Int, Int> {
            val product = nx.toLong() * ny * nz
            if (product <= cap) {
                return Triple(nx, ny, nz)
            }
            val factor = (cap.toDouble() / product).pow(1.0 / 3)
            val cx = max(1, (nx * factor).toInt())
            val cy = max(1, (ny * factor).toInt())
            val cz = max(1, (nz * factor).toInt())
            return Triple(cx, cy, cz)
        }
    }
}
