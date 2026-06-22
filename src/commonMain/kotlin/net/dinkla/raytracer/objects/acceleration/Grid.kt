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
import net.dinkla.raytracer.utilities.Timer
import kotlin.math.pow

open class Grid : CompoundWithMesh() {
    private var cells: Array<IGeometricObject> = Array(0) { _ -> NullObject() }

    protected var nx: Int = 0
    protected var ny: Int = 0
    protected var nz: Int = 0

    var multiplier: Double = 2.0

    protected var depth: Int = 0

    init {
        boundingBox = BBox()
    }

    override fun initialize() {
        if (isInitialized) {
            return
        }
        super.initialize()

        if (objects.size == 0) {
            return
        }

        val timer = Timer()
        timer.start()
        val bbox = boundingBox

        val wx = bbox.q.x - bbox.p.x
        val wy = bbox.q.y - bbox.p.y
        val wz = bbox.q.z - bbox.p.z

        val s = (wx * wy * wz / objects.size).pow(1.0 / 3)
        nx = (multiplier * wx / s + 1).toInt()
        ny = (multiplier * wy / s + 1).toInt()
        nz = (multiplier * wz / s + 1).toInt()

        val numCells = nx * ny * nz

        Logger.info("Grid: numCells=$numCells = $nx*$ny*$nz")

        cells = Array(numCells) { _ -> NullObject() }

        val counts = IntArray(numCells)

        for (i in 0 until numCells) {
            counts[i] = 0
        }

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
                        when {
                            cells[index] is Grid -> {
                                val c = cells[index] as Grid
                                c.add(`object`)
                            }
                            cells[index] is Compound -> {
                                val c = cells[index] as Compound
                                if (c.size() > factorSize && depth < maxDepth) {
                                    val g = Grid()
                                    g.add(c.objects)
                                    g.add(`object`)
                                    g.depth = depth + 1

                                    cells[index] = g
                                } else {
                                    c.add(`object`)
                                }
                            }
                            else -> {
                                val c = Compound()
                                c.add(cells[index])
                                c.add(`object`)
                                cells[index] = c
                            }
                        }
                        counts[index]++
                    }
                }
            }
        }

        timer.stop()
        Logger.info("Creating grid took " + timer.duration + " ms")

        timer.start()
        for (go in cells) {
            (go as? Grid)?.initialize()
        }
        timer.stop()
        Logger.info("Creating subgrids took " + timer.duration + " ms")

        if (0 == depth) {
            statistics(numCells, counts)
        }
    }

    protected fun statistics(
        numCells: Int,
        counts: IntArray,
    ) {
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

    override fun hit(
        ray: Ray,
        sr: IHit,
    ): Boolean {
        // if (depth > 0) return false;
        if (!boundingBox.isHit(ray)) {
            Counter.count("Grid.hit.bbox")
            return false
        }
        Counter.count("Grid.hit")

        val slab = GridTraversal.computeSlab(ray, boundingBox, nx, ny, nz)
        if (!slab.hits) {
            Counter.count("Grid.hit.t0>t1")
            return false
        }

        val walk = GridTraversal.initialWalk(ray, boundingBox, slab, nx, ny, nz)
        return GridTraversal.traverse(
            ray = ray,
            sr = sr,
            walk = walk,
            nx = nx,
            ny = ny,
            cellAt = { cells[it] },
            onTraverse = { Counter.count("Grid.hit.traverse") },
        )
    }

    override fun shadowHit(
        ray: Ray,
        tmin: ShadowHit,
    ): Boolean {
        Counter.count("Grid.shadowHit")
        val h = Hit(tmin.t)
        val b = hit(ray, h)
        tmin.t = h.t
        return b
    }

    override fun toString(): String = "Grid(#objs=${objects.size})"

    companion object {
        internal var logInterval = 1000
        protected var factorSize = 500
        protected var maxDepth = 0
    }
}
