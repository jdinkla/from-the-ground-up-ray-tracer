package net.dinkla.raytracer.objects.acceleration

import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.ShadowHit
import net.dinkla.raytracer.math.*
import net.dinkla.raytracer.objects.compound.Compound
import net.dinkla.raytracer.objects.GeometricObject
import net.dinkla.raytracer.objects.NullObject
import net.dinkla.raytracer.objects.mesh.Mesh
import net.dinkla.raytracer.utilities.Counter
import net.dinkla.raytracer.utilities.Timer
import org.apache.log4j.Logger

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 01.05.2010
 * Time: 15:05:01
 * To change this template use File | Settings | File Templates.
 */
open class Grid : Compound {

    var mesh: Mesh = Mesh()

    protected var cells: Array<GeometricObject> = Array(0, { i -> NullObject() } )

    protected var nx: Int = 0
    protected var ny: Int = 0
    protected var nz: Int = 0

    var multiplier: Double = 0.toDouble()

    protected var depth: Int = 0

    constructor() : super() {
        multiplier = 2.0
        depth = 0
        boundingBox = BBox()
    }

    constructor(mesh: Mesh) : super() {
        this.mesh = mesh
        multiplier = 2.0
        depth = 0
        // TODO  setBoundingBox(mesh.get);
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
        // setBoundingBox(getBoundingBox());        // TODO: wird in initialize() aufgerufen !!!
        val bbox = boundingBox

        val wx = bbox.q!!.x - bbox.p!!.x
        val wy = bbox.q.y - bbox.p.y
        val wz = bbox.q.z - bbox.p.z

        val s = Math.pow(wx * wy * wz / objects.size, 1.0 / 3)
        nx = (multiplier * wx / s + 1).toInt()
        ny = (multiplier * wy / s + 1).toInt()
        nz = (multiplier * wz / s + 1).toInt()

        val numCells = nx * ny * nz

        LOGGER.info("Grid: numCells=$numCells = $nx*$ny*$nz")

        cells = Array<GeometricObject>(numCells, { i -> NullObject() })

        val counts = IntArray(numCells)

        // initialize
        for (i in 0 until numCells) {
            //cells[i] = null;
            counts[i] = 0
        }

        var objectsToGo = objects.size

        // insert the objects into the cells
        for (`object` in objects) {

            if (objectsToGo % logInterval == 0) {
                LOGGER.info("Grid: $objectsToGo objects to grid")
            }
            objectsToGo--

            val objBbox = `object`.boundingBox

            val ixmin = MathUtils.clamp((objBbox.p!!.x - bbox.p.x) * nx / wx, 0.0, (nx - 1).toDouble()).toInt()
            val iymin = MathUtils.clamp((objBbox.p.y - bbox.p.y) * ny / wy, 0.0, (ny - 1).toDouble()).toInt()
            val izmin = MathUtils.clamp((objBbox.p.z - bbox.p.z) * nz / wz, 0.0, (nz - 1).toDouble()).toInt()

            val ixmax = MathUtils.clamp((objBbox.q!!.x - bbox.p.x) * nx / wx, 0.0, (nx - 1).toDouble()).toInt()
            val iymax = MathUtils.clamp((objBbox.q.y - bbox.p.y) * ny / wy, 0.0, (ny - 1).toDouble()).toInt()
            val izmax = MathUtils.clamp((objBbox.q.z - bbox.p.z) * nz / wz, 0.0, (nz - 1).toDouble()).toInt()

            for (iz in izmin..izmax) {
                for (iy in iymin..iymax) {
                    for (ix in ixmin..ixmax) {
                        val index = iz * nx * ny + iy * nx + ix
                        if (null == cells[index]) {
                            cells[index] = `object`
                        } else if (cells[index] is Grid) {
                            val c = cells[index] as Grid
                            c.add(`object`)
                        } else if (cells[index] is Compound) {
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
                        } else {
                            val c = Compound()
                            c.add(cells[index])
                            c.add(`object`)
                            cells[index] = c
                        }
                        counts[index]++
                    }
                }
            }
        }

        timer.stop()
        LOGGER.info("Creating grid took " + timer.duration + " ms")

        timer.start()
        for (go in cells) {
            (go as? Grid)?.initialize()
        }
        timer.stop()
        LOGGER.info("Creating subgrids took " + timer.duration + " ms")

        if (0 == depth) {
            statistics(numCells, counts)
        }

    }

    protected fun statistics(numCells: Int, counts: IntArray) {
        val hist = Histogram()

        var numInCells = 0
        for (j in 0 until numCells) {
            val count = counts[j]
            numInCells += count
            hist.add(count)
        }

        LOGGER.info("Grid statistics")
        LOGGER.info("multiplier=" + multiplier
                + ", numObjects=" + objects.size
                + ", numCells=" + numCells
                + ", numObjects in cells=" + numInCells)

        for (key in hist.counts.keys) {
            val value = hist[key]!!
            LOGGER.info("Grid: " + key + ": " + value + " [" + value * 100.0 / numInCells + "%]")
        }
    }

    override fun hit(ray: Ray, sr: Hit): Boolean {
        //if (depth > 0) return false;
        if (!boundingBox.hit(ray)) {
            Counter.count("Grid.hit.bbox")
            return false
        }
        Counter.count("Grid.hit")

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

        if (t0 > t1) {
            Counter.count("Grid.hit.t0>t1")
            return false
        }

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
            Counter.count("Grid.hit.traverse")
            val idx = ix + nx * iy + nx * ny * iz
            val `object` = cells[ix + nx * iy + nx * ny * iz]
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

    //    @Override
    //    public boolean shadowHitX(Ray ray, ShadowHit hit) {
    //        if (!bbox.hit(ray)) {
    //            return false;
    //        }
    //
    //        double ox = ray.origin.x;
    //        double oy = ray.origin.y;
    //        double oz = ray.origin.z;
    //        double dx = ray.direction.x;
    //        double dy = ray.direction.y;
    //        double dz = ray.direction.z;
    //
    //        double x0 = bbox.p.x;
    //        double y0 = bbox.p.y;
    //        double z0 = bbox.p.z;
    //        double x1 = bbox.q.x;
    //        double y1 = bbox.q.y;
    //        double z1 = bbox.q.z;
    //
    //        double tx_min, ty_min, tz_min;
    //        double tx_max, ty_max, tz_max;
    //
    //        // the following code includes modifications from Shirley and Morley (2003)
    //
    //        final double a = 1.0 / dx;
    //        if (a >= 0) {
    //            tx_min = (x0 - ox) * a;
    //            tx_max = (x1 - ox) * a;
    //        } else {
    //            tx_min = (x1 - ox) * a;
    //            tx_max = (x0 - ox) * a;
    //        }
    //
    //        final double b = 1.0 / dy;
    //        if (b >= 0) {
    //            ty_min = (y0 - oy) * b;
    //            ty_max = (y1 - oy) * b;
    //        } else {
    //            ty_min = (y1 - oy) * b;
    //            ty_max = (y0 - oy) * b;
    //        }
    //
    //        final double c = 1.0 / dz;
    //        if (c >= 0) {
    //            tz_min = (z0 - oz) * c;
    //            tz_max = (z1 - oz) * c;
    //        } else {
    //            tz_min = (z1 - oz) * c;
    //            tz_max = (z0 - oz) * c;
    //        }
    //
    //        double t0, t1;
    //
    //        if (tx_min > ty_min)
    //            t0 = tx_min;
    //        else
    //            t0 = ty_min;
    //
    //        if (tz_min > t0)
    //            t0 = tz_min;
    //
    //        if (tx_max < ty_max)
    //            t1 = tx_max;
    //        else
    //            t1 = ty_max;
    //
    //        if (tz_max < t1)
    //            t1 = tz_max;
    //
    //        if (t0 > t1)
    //            return false;
    //
    //        // initial cell coordinates
    //
    //        int ix, iy, iz;
    //
    //        if (bbox.inside(ray.origin)) {              // does the ray start inside the grid?
    //            ix = (int) MathUtils.clamp((ox - x0) * nx / (x1 - x0), 0, nx - 1);
    //            iy = (int) MathUtils.clamp((oy - y0) * ny / (y1 - y0), 0, ny - 1);
    //            iz = (int) MathUtils.clamp((oz - z0) * nz / (z1 - z0), 0, nz - 1);
    //        } else {
    //            Point3D p = ray.linear(t0);  // initial hit point with grid's bounding box
    //            ix = (int) MathUtils.clamp((p.x - x0) * nx / (x1 - x0), 0, nx - 1);
    //            iy = (int) MathUtils.clamp((p.y - y0) * ny / (y1 - y0), 0, ny - 1);
    //            iz = (int) MathUtils.clamp((p.z - z0) * nz / (z1 - z0), 0, nz - 1);
    //        }
    //
    //        // ray parameter increments per cell in the x, y, and z directions
    //
    //        double dtx = (tx_max - tx_min) / nx;
    //        double dty = (ty_max - ty_min) / ny;
    //        double dtz = (tz_max - tz_min) / nz;
    //
    //        double tx_next, ty_next, tz_next;
    //        int ix_step, iy_step, iz_step;
    //        int ix_stop, iy_stop, iz_stop;
    //
    //        if (dx > 0) {
    //            tx_next = tx_min + (ix + 1) * dtx;
    //            ix_step = +1;
    //            ix_stop = nx;
    //        } else {
    //            tx_next = tx_min + (nx - ix) * dtx;
    //            ix_step = -1;
    //            ix_stop = -1;
    //        }
    //        if (dx == 0.0) {
    //            tx_next = MathUtils.K_HUGEVALUE;
    //            ix_step = -1;
    //            ix_stop = -1;
    //        }
    //        if (dy > 0) {
    //            ty_next = ty_min + (iy + 1) * dty;
    //            iy_step = +1;
    //            iy_stop = ny;
    //        } else {
    //            ty_next = ty_min + (ny - iy) * dty;
    //            iy_step = -1;
    //            iy_stop = -1;
    //        }
    //        if (dy == 0.0) {
    //            ty_next = MathUtils.K_HUGEVALUE;
    //            iy_step = -1;
    //            iy_stop = -1;
    //        }
    //        if (dz > 0) {
    //            tz_next = tz_min + (iz + 1) * dtz;
    //            iz_step = +1;
    //            iz_stop = nz;
    //        } else {
    //            tz_next = tz_min + (nz - iz) * dtz;
    //            iz_step = -1;
    //            iz_stop = -1;
    //        }
    //        if (dz == 0.0) {
    //            tz_next = MathUtils.K_HUGEVALUE;
    //            iz_step = -1;
    //            iz_stop = -1;
    //        }
    //
    //        // traverse the grid
    //        while (true) {
    //            int idx = ix + nx * iy + nx * ny * iz;
    //            GeometricObject object = cells[ix + nx * iy + nx * ny * iz];
    //            ShadowHit hit2 = new ShadowHit();
    //            hit2.setT(hit.getT());
    //            if (tx_next < ty_next && tx_next < tz_next) {
    //                if (null != object && object.shadowHit(ray, hit2) && hit2.getT() < tx_next) {
    //                    hit.setT(hit2.getT());
    //                    return true;
    //                }
    //
    //                tx_next += dtx;
    //                ix += ix_step;
    //
    //                if (ix == ix_stop)
    //                    return (false);
    //            } else {
    //                if (ty_next < tz_next) {
    //                    if (null != object && object.shadowHit(ray, hit2) && hit2.getT() < ty_next) {
    //                        hit.setT(hit2.getT());
    //                        return (true);
    //                    }
    //
    //                    ty_next += dty;
    //                    iy += iy_step;
    //
    //                    if (iy == iy_stop)
    //                        return (false);
    //                } else {
    //                    if (null != object && object.shadowHit(ray, hit2) && hit2.getT() < tz_next) {
    //                        hit.setT(hit2.getT());
    //                        return true;
    //                    }
    //
    //                    tz_next += dtz;
    //                    iz += iz_step;
    //
    //                    if (iz == iz_stop)
    //                        return (false);
    //                }
    //            }
    //        }
    //    }

    override fun shadowHit(ray: Ray, tmin: ShadowHit): Boolean {
        Counter.count("Grid.shadowHit")
        val h = Hit()
        h.t = tmin.t
        val b = hit(ray, h)
        tmin.t = h.t
        return b
    }

    override fun toString(): String {
        return super.toString() + " (" + objects.size + " elements)"
    }

    companion object {

        internal val LOGGER = Logger.getLogger(Grid::class.java)

        internal var logInterval = 1000

        protected var factorSize = 500

        protected var maxDepth = 0
    }

}
