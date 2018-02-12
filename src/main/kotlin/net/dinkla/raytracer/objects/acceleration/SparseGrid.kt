package net.dinkla.raytracer.objects.acceleration

import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.ShadowHit
import net.dinkla.raytracer.math.MathUtils
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.objects.compound.Compound
import net.dinkla.raytracer.objects.GeometricObject
import net.dinkla.raytracer.objects.mesh.Mesh
import net.dinkla.raytracer.utilities.Timer
import org.apache.log4j.Logger
import java.util.TreeMap

class SparseGrid : Grid {

    //protected GeometricObject[] cells;
    protected var cellsX: MutableMap<Int, GeometricObject> = mutableMapOf()

    constructor() : super() {}

    constructor(mesh: Mesh) : super(mesh) {}

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

        val wx = (boundingBox.q?.x ?: 0.0) - (boundingBox.p?.x ?: 0.0)
        val wy = (boundingBox.q?.y ?: 0.0) - (boundingBox.p?.y ?: 0.0)
        val wz = (boundingBox.q?.z ?: 0.0) - (boundingBox.p?.z ?: 0.0)

        val s = Math.pow(wx * wy * wz / objects.size, 1.0 / 3)
        nx = (multiplier * wx / s + 1).toInt()
        ny = (multiplier * wy / s + 1).toInt()
        nz = (multiplier * wz / s + 1).toInt()

        val numCells = nx * ny * nz

        LOGGER.info("Grid: numCells=$numCells = $nx*$ny*$nz")

        //cells = new GeometricObject[numCells];
        cellsX = TreeMap()
        //cellsX.ensureCapacity(numCells/10);

        val counts = IntArray(numCells)

        // initialize
        for (i in 0 until numCells) {
            //cells[i] = null;
            counts[i] = 0
        }

        var objectsToGo = objects.size

        // insert the objects into the cells
        for (`object` in objects) {

            if (objectsToGo % Grid.Companion.logInterval === 0) {
                LOGGER.info("Grid: $objectsToGo objects to grid")
            }
            objectsToGo--

            val objBbox = `object`.boundingBox

            val ixmin = MathUtils.clamp((objBbox.p!!.x - (boundingBox.p?.x ?: 0.0)) * nx / wx, 0.0, (nx - 1).toDouble()).toInt()
            val iymin = MathUtils.clamp((objBbox.p.y - (boundingBox.p?.y ?: 0.0)) * ny / wy, 0.0, (ny - 1).toDouble()).toInt()
            val izmin = MathUtils.clamp((objBbox.p.z - (boundingBox.p?.z?: 0.0)) * nz / wz, 0.0, (nz - 1).toDouble()).toInt()

            val ixmax = MathUtils.clamp((objBbox.q!!.x - (boundingBox.p?.x?: 0.0)) * nx / wx, 0.0, (nx - 1).toDouble()).toInt()
            val iymax = MathUtils.clamp((objBbox.q.y - (boundingBox.p?.y?: 0.0)) * ny / wy, 0.0, (ny - 1).toDouble()).toInt()
            val izmax = MathUtils.clamp((objBbox.q.z - (boundingBox.p?.z?: 0.0)) * nz / wz, 0.0, (nz - 1).toDouble()).toInt()

            for (iz in izmin..izmax) {
                for (iy in iymin..iymax) {
                    for (ix in ixmin..ixmax) {
                        val index = iz * nx * ny + iy * nx + ix
                        val go = cellsX[index]
                        if (null == go) {
                            cellsX[index] = `object`
                        } else if (go is Compound) {
                            val c = go as Compound?
                            c!!.add(`object`)
                        } else {
                            val c = Compound()
                            c.add(go)
                            c.add(`object`)
                            cellsX[index] = c
                        }
                        /*
                        if (null == cells[index]) {
                            cells[index] = object;
                        } else if (cells[index] instanceof Compound) {
                            Compound c = (Compound) cells[index];
                            c.add(object);
                        } else {
                            Compound c = new Compound();
                            c.add(cells[index]);
                            c.add(object);
                            cells[index] = c;
                        }
*/
                        counts[index]++
                    }
                }
            }
        }

        timer.stop()
        LOGGER.info("Creating grid took " + timer.duration + " ms")

        statistics(numCells, counts)

    }

    override fun hit(ray: Ray, sr: Hit): Boolean {
        if (!boundingBox.hit(ray)) {
            return false
        }

        val ox = ray.origin.x
        val oy = ray.origin.y
        val oz = ray.origin.z
        val dx = ray.direction.x
        val dy = ray.direction.y
        val dz = ray.direction.z

        val x0 = boundingBox.p?.x ?: 0.0
        val y0 = boundingBox.p?.y ?: 0.0
        val z0 = boundingBox.p?.z ?: 0.0
        val x1 = boundingBox.q?.x ?: 0.0
        val y1 = boundingBox.q?.y ?: 0.0
        val z1 = boundingBox.q?.z ?: 0.0

        val tx_min: Double
        val ty_min: Double
        val tz_min: Double
        val tx_max: Double
        val ty_max: Double
        val tz_max: Double

        // the following code includes modifications from Shirley and Morley (2003)

        val a = 1.0 / dx
        if (a >= 0) {
            tx_min = (x0 - ox) * a
            tx_max = (x1 - ox) * a
        } else {
            tx_min = (x1 - ox) * a
            tx_max = (x0 - ox) * a
        }

        val b = 1.0 / dy
        if (b >= 0) {
            ty_min = (y0 - oy) * b
            ty_max = (y1 - oy) * b
        } else {
            ty_min = (y1 - oy) * b
            ty_max = (y0 - oy) * b
        }

        val c = 1.0 / dz
        if (c >= 0) {
            tz_min = (z0 - oz) * c
            tz_max = (z1 - oz) * c
        } else {
            tz_min = (z1 - oz) * c
            tz_max = (z0 - oz) * c
        }

        var t0: Double
        var t1: Double

        if (tx_min > ty_min)
            t0 = tx_min
        else
            t0 = ty_min

        if (tz_min > t0)
            t0 = tz_min

        if (tx_max < ty_max)
            t1 = tx_max
        else
            t1 = ty_max

        if (tz_max < t1)
            t1 = tz_max

        if (t0 > t1)
            return false

        // initial cell coordinates

        var ix: Int
        var iy: Int
        var iz: Int

        if (boundingBox.inside(ray.origin)) {              // does the ray start inside the grid?
            ix = MathUtils.clamp((ox - x0) * nx / (x1 - x0), 0.0, (nx - 1).toDouble()).toInt()
            iy = MathUtils.clamp((oy - y0) * ny / (y1 - y0), 0.0, (ny - 1).toDouble()).toInt()
            iz = MathUtils.clamp((oz - z0) * nz / (z1 - z0), 0.0, (nz - 1).toDouble()).toInt()
        } else {
            val p = ray.linear(t0)  // initial hit point with grid's bounding box
            ix = MathUtils.clamp((p.x - x0) * nx / (x1 - x0), 0.0, (nx - 1).toDouble()).toInt()
            iy = MathUtils.clamp((p.y - y0) * ny / (y1 - y0), 0.0, (ny - 1).toDouble()).toInt()
            iz = MathUtils.clamp((p.z - z0) * nz / (z1 - z0), 0.0, (nz - 1).toDouble()).toInt()
        }

        // ray parameter increments per cell in the x, y, and z directions

        val dtx = (tx_max - tx_min) / nx
        val dty = (ty_max - ty_min) / ny
        val dtz = (tz_max - tz_min) / nz

        var tx_next: Double
        var ty_next: Double
        var tz_next: Double
        var ix_step: Int
        var iy_step: Int
        var iz_step: Int
        var ix_stop: Int
        var iy_stop: Int
        var iz_stop: Int

        if (dx > 0) {
            tx_next = tx_min + (ix + 1) * dtx
            ix_step = +1
            ix_stop = nx
        } else {
            tx_next = tx_min + (nx - ix) * dtx
            ix_step = -1
            ix_stop = -1
        }
        if (dx == 0.0) {
            tx_next = MathUtils.K_HUGEVALUE
            ix_step = -1
            ix_stop = -1
        }
        if (dy > 0) {
            ty_next = ty_min + (iy + 1) * dty
            iy_step = +1
            iy_stop = ny
        } else {
            ty_next = ty_min + (ny - iy) * dty
            iy_step = -1
            iy_stop = -1
        }
        if (dy == 0.0) {
            ty_next = MathUtils.K_HUGEVALUE
            iy_step = -1
            iy_stop = -1
        }
        if (dz > 0) {
            tz_next = tz_min + (iz + 1) * dtz
            iz_step = +1
            iz_stop = nz
        } else {
            tz_next = tz_min + (nz - iz) * dtz
            iz_step = -1
            iz_stop = -1
        }
        if (dz == 0.0) {
            tz_next = MathUtils.K_HUGEVALUE
            iz_step = -1
            iz_stop = -1
        }

        // traverse the grid
        while (true) {
            val idx = ix + nx * iy + nx * ny * iz
            val `object` = cellsX[idx]
            //GeometricObject object = cells[ix + nx * iy + nx * ny * iz];
            val sr2 = Hit(sr.t)
            if (tx_next < ty_next && tx_next < tz_next) {
                if (null != `object` && `object`.hit(ray, sr2) && sr2.t < tx_next) {
                    sr.t = sr2.t
                    sr.normal = sr2.normal
                    if (`object` !is Compound) {
                        sr.`object` = `object`
                    } else {
                        sr.`object` = sr2.`object`
                    }
                    return true
                }

                tx_next += dtx
                ix += ix_step

                if (ix == ix_stop)
                    return false
            } else {
                if (ty_next < tz_next) {
                    if (null != `object` && `object`.hit(ray, sr2) && sr2.t < ty_next) {
                        sr.t = sr2.t
                        sr.normal = sr2.normal
                        if (`object` !is Compound) {
                            sr.`object` = `object`
                        } else {
                            sr.`object` = sr2.`object`
                        }
                        return true
                    }

                    ty_next += dty
                    iy += iy_step

                    if (iy == iy_stop)
                        return false
                } else {
                    if (null != `object` && `object`.hit(ray, sr2) && sr2.t < tz_next) {
                        sr.t = sr2.t
                        sr.normal = sr2.normal
                        if (`object` !is Compound) {
                            sr.`object` = `object`
                        } else {
                            sr.`object` = sr2.`object`
                        }
                        return true
                    }

                    tz_next += dtz
                    iz += iz_step

                    if (iz == iz_stop)
                        return false
                }
            }
        }
    }

    override fun shadowHit(ray: Ray, tmin: ShadowHit): Boolean {
        val h = Hit()
        h.t = tmin.t
        val b = hit(ray, h)
        tmin.t = h.t
        return b
    }

    companion object {

        internal val LOGGER = Logger.getLogger(SparseGrid::class.java)
    }

}