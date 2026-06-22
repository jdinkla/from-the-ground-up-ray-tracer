package net.dinkla.raytracer.objects.acceleration

import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.hits.ShadowHit
import net.dinkla.raytracer.math.MathUtils
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.objects.IGeometricObject
import net.dinkla.raytracer.objects.compound.Compound
import net.dinkla.raytracer.utilities.Logger
import net.dinkla.raytracer.utilities.Timer
import kotlin.math.pow

class SparseGrid : Grid() {
    // protected GeometricObject[] cells;
    private var cellsX: MutableMap<Int, IGeometricObject> = mutableMapOf()

    override fun initialize() {
        if (isInitialized) {
            return
        }
        isInitialized = true
        // super.initialize();

        if (objects.size == 0) {
            return
        }

        val timer = Timer()
        timer.start()

        val wx = boundingBox.q.x - boundingBox.p.x
        val wy = boundingBox.q.y - boundingBox.p.y
        val wz = boundingBox.q.z - boundingBox.p.z

        val s = (wx * wy * wz / objects.size).pow(1.0 / 3)
        nx = (multiplier * wx / s + 1).toInt()
        ny = (multiplier * wy / s + 1).toInt()
        nz = (multiplier * wz / s + 1).toInt()

        val numCells = nx * ny * nz

        Logger.info("Grid: numCells=$numCells = $nx*$ny*$nz")

        // cells = new GeometricObject[numCells];
        cellsX = mutableMapOf()
        // cellsX.ensureCapacity(numCells/10);

        val counts = IntArray(numCells)

        // initialize
        for (i in 0 until numCells) {
            // cells[i] = null;
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

            val ixmin = MathUtils.clamp((objBbox.p.x - boundingBox.p.x) * nx / wx, 0.0, (nx - 1.0)).toInt()
            val iymin = MathUtils.clamp((objBbox.p.y - boundingBox.p.y) * ny / wy, 0.0, (ny - 1.0)).toInt()
            val izmin = MathUtils.clamp((objBbox.p.z - boundingBox.p.z) * nz / wz, 0.0, (nz - 1.0)).toInt()

            val ixmax = MathUtils.clamp((objBbox.q.x - boundingBox.p.x) * nx / wx, 0.0, (nx - 1.0)).toInt()
            val iymax = MathUtils.clamp((objBbox.q.y - boundingBox.p.y) * ny / wy, 0.0, (ny - 1.0)).toInt()
            val izmax = MathUtils.clamp((objBbox.q.z - boundingBox.p.z) * nz / wz, 0.0, (nz - 1.0)).toInt()

            for (iz in izmin..izmax) {
                for (iy in iymin..iymax) {
                    for (ix in ixmin..ixmax) {
                        val index = iz * nx * ny + iy * nx + ix
                        val go = cellsX[index]
                        when (go) {
                            null -> {
                                cellsX[index] = `object`
                            }
                            is Compound -> {
                                val c = go as Compound?
                                c!!.add(`object`)
                            }
                            else -> {
                                val c = Compound()
                                c.add(go)
                                c.add(`object`)
                                cellsX[index] = c
                            }
                        }
                        counts[index]++
                    }
                }
            }
        }

        timer.stop()
        Logger.info("Creating grid took " + timer.duration + " ms")

        statistics(numCells, counts)
    }

    override fun hit(
        ray: Ray,
        sr: IHit,
    ): Boolean {
        if (!boundingBox.isHit(ray)) {
            return false
        }

        val slab = GridTraversal.computeSlab(ray, boundingBox, nx, ny, nz)
        if (!slab.hits) {
            return false
        }

        val walk = GridTraversal.initialWalk(ray, boundingBox, slab, nx, ny, nz)
        return GridTraversal.traverse(
            ray = ray,
            sr = sr,
            walk = walk,
            nx = nx,
            ny = ny,
            cellAt = { cellsX[it] },
            onTraverse = {},
        )
    }

    @Deprecated("sparseGrid shadowHit uses tmin as input?")
    override fun shadowHit(
        ray: Ray,
        tmin: ShadowHit,
    ): Boolean {
        val h = Hit(tmin.t)
        val b = hit(ray, h)
        tmin.t = h.t
        return b
    }
}
