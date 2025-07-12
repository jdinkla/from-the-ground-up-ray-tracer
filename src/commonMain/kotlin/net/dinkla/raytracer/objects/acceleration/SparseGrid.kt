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

        val ox = ray.origin.x
        val oy = ray.origin.y
        val oz = ray.origin.z
        val dx = ray.direction.x
        val dy = ray.direction.y
        val dz = ray.direction.z

        val x0 = boundingBox.p.x
        val y0 = boundingBox.p.y
        val z0 = boundingBox.p.z
        val x1 = boundingBox.q.x
        val y1 = boundingBox.q.y
        val z1 = boundingBox.q.z

        val txMin: Double
        val tyMin: Double
        val tzMin: Double
        val txMax: Double
        val tyMax: Double
        val tzMax: Double

        // the following code includes modifications from Shirley and Morley (2003)

        val a = 1.0 / dx
        if (a >= 0) {
            txMin = (x0 - ox) * a
            txMax = (x1 - ox) * a
        } else {
            txMin = (x1 - ox) * a
            txMax = (x0 - ox) * a
        }

        val b = 1.0 / dy
        if (b >= 0) {
            tyMin = (y0 - oy) * b
            tyMax = (y1 - oy) * b
        } else {
            tyMin = (y1 - oy) * b
            tyMax = (y0 - oy) * b
        }

        val c = 1.0 / dz
        if (c >= 0) {
            tzMin = (z0 - oz) * c
            tzMax = (z1 - oz) * c
        } else {
            tzMin = (z1 - oz) * c
            tzMax = (z0 - oz) * c
        }

        var t0: Double
        var t1: Double

        t0 =
            if (txMin > tyMin) {
                txMin
            } else {
                tyMin
            }

        if (tzMin > t0) {
            t0 = tzMin
        }

        t1 =
            if (txMax < tyMax) {
                txMax
            } else {
                tyMax
            }

        if (tzMax < t1) {
            t1 = tzMax
        }

        if (t0 > t1) {
            return false
        }

        // initial cell coordinates

        var ix: Int
        var iy: Int
        var iz: Int

        if (boundingBox.isInside(ray.origin)) { // does the ray start inside the grid?
            ix = MathUtils.clamp((ox - x0) * nx / (x1 - x0), 0.0, (nx - 1.0)).toInt()
            iy = MathUtils.clamp((oy - y0) * ny / (y1 - y0), 0.0, (ny - 1.0)).toInt()
            iz = MathUtils.clamp((oz - z0) * nz / (z1 - z0), 0.0, (nz - 1.0)).toInt()
        } else {
            val p = ray.linear(t0) // initial hit point with grid's bounding box
            ix = MathUtils.clamp((p.x - x0) * nx / (x1 - x0), 0.0, (nx - 1.0)).toInt()
            iy = MathUtils.clamp((p.y - y0) * ny / (y1 - y0), 0.0, (ny - 1.0)).toInt()
            iz = MathUtils.clamp((p.z - z0) * nz / (z1 - z0), 0.0, (nz - 1.0)).toInt()
        }

        // ray parameter increments per cell in the x, y, and z directions

        val dtx = (txMax - txMin) / nx
        val dty = (tyMax - tyMin) / ny
        val dtz = (tzMax - tzMin) / nz

        var txNext: Double
        var tyNext: Double
        var tzNext: Double
        var ixStep: Int
        var iyStep: Int
        var izStep: Int
        var ixStop: Int
        var iyStop: Int
        var izStop: Int

        if (dx > 0) {
            txNext = txMin + (ix + 1) * dtx
            ixStep = 1
            ixStop = nx
        } else {
            txNext = txMin + (nx - ix) * dtx
            ixStep = -1
            ixStop = -1
        }
        if (dx == 0.0) {
            txNext = MathUtils.K_HUGE_VALUE
            ixStep = -1
            ixStop = -1
        }
        if (dy > 0) {
            tyNext = tyMin + (iy + 1) * dty
            iyStep = 1
            iyStop = ny
        } else {
            tyNext = tyMin + (ny - iy) * dty
            iyStep = -1
            iyStop = -1
        }
        if (dy == 0.0) {
            tyNext = MathUtils.K_HUGE_VALUE
            iyStep = -1
            iyStop = -1
        }
        if (dz > 0) {
            tzNext = tzMin + (iz + 1) * dtz
            izStep = 1
            izStop = nz
        } else {
            tzNext = tzMin + (nz - iz) * dtz
            izStep = -1
            izStop = -1
        }
        if (dz == 0.0) {
            tzNext = MathUtils.K_HUGE_VALUE
            izStep = -1
            izStop = -1
        }

        // traverse the grid
        while (true) {
            val idx = ix + nx * iy + nx * ny * iz
            val theObject = cellsX[idx]
            val sr2 = Hit(sr.t)
            if (txNext < tyNext && txNext < tzNext) {
                if (null != theObject && theObject.hit(ray, sr2) && sr2.t < txNext) {
                    sr.t = sr2.t
                    sr.normal = sr2.normal
                    if (theObject !is Compound) {
                        sr.geometricObject = theObject
                    } else {
                        sr.geometricObject = sr2.geometricObject
                    }
                    return true
                }

                txNext += dtx
                ix += ixStep

                if (ix == ixStop) {
                    return false
                }
            } else {
                if (tyNext < tzNext) {
                    if (null != theObject && theObject.hit(ray, sr2) && sr2.t < tyNext) {
                        sr.t = sr2.t
                        sr.normal = sr2.normal
                        if (theObject !is Compound) {
                            sr.geometricObject = theObject
                        } else {
                            sr.geometricObject = sr2.geometricObject
                        }
                        return true
                    }

                    tyNext += dty
                    iy += iyStep

                    if (iy == iyStop) {
                        return false
                    }
                } else {
                    if (null != theObject && theObject.hit(ray, sr2) && sr2.t < tzNext) {
                        sr.t = sr2.t
                        sr.normal = sr2.normal
                        if (theObject !is Compound) {
                            sr.geometricObject = theObject
                        } else {
                            sr.geometricObject = sr2.geometricObject
                        }
                        return true
                    }

                    tzNext += dtz
                    iz += izStep

                    if (iz == izStop) {
                        return false
                    }
                }
            }
        }
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
