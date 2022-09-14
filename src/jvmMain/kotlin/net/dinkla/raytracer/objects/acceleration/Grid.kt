package net.dinkla.raytracer.objects.acceleration

import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.hits.ShadowHit
import net.dinkla.raytracer.interfaces.Counter
import net.dinkla.raytracer.interfaces.Timer
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.MathUtils
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.objects.IGeometricObject
import net.dinkla.raytracer.objects.NullObject
import net.dinkla.raytracer.objects.compound.Compound
import net.dinkla.raytracer.utilities.Histogram
import net.dinkla.raytracer.utilities.Logger
import kotlin.math.pow

open class Grid : CompoundWithMesh() {

    private var cells: Array<IGeometricObject> = Array(0) { i -> NullObject() }

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
        // setBoundingBox(getBoundingBox());        // TODO: wird in initialize() aufgerufen !!!
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

        cells = Array(numCells) { i -> NullObject() }

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
                Logger.info("Grid: $objectsToGo objects to grid")
            }
            objectsToGo--

            val objBbox = `object`.boundingBox

            val ixmin = MathUtils.clamp((objBbox.p.x - bbox.p.x) * nx / wx, 0.0, (nx - 1).toDouble()).toInt()
            val iymin = MathUtils.clamp((objBbox.p.y - bbox.p.y) * ny / wy, 0.0, (ny - 1).toDouble()).toInt()
            val izmin = MathUtils.clamp((objBbox.p.z - bbox.p.z) * nz / wz, 0.0, (nz - 1).toDouble()).toInt()

            val ixmax = MathUtils.clamp((objBbox.q.x - bbox.p.x) * nx / wx, 0.0, (nx - 1).toDouble()).toInt()
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

    protected fun statistics(numCells: Int, counts: IntArray) {
        val hist = Histogram()

        var numInCells = 0
        for (j in 0 until numCells) {
            val count = counts[j]
            numInCells += count
            hist.add(count)
        }

        Logger.info("Grid statistics")
        Logger.info("multiplier=" + multiplier
                + ", numObjects=" + objects.size
                + ", numCells=" + numCells
                + ", numObjects in cells=" + numInCells)

        for (key in hist.keys()) {
            val value = hist[key]
            Logger.info("Grid: " + key + ": " + value + " [" + value * 100.0 / numInCells + "%]")
        }
    }

    override fun hit(ray: Ray, sr: IHit): Boolean {
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

        t0 = if (txMin > tyMin)
            txMin
        else
            tyMin

        if (tzMin > t0)
            t0 = tzMin

        t1 = if (txMax < tyMax)
            txMax
        else
            tyMax

        if (tzMax < t1)
            t1 = tzMax

        if (t0 > t1) {
            Counter.count("Grid.hit.t0>t1")
            return false
        }

        // initial cell coordinates

        var ix: Int
        var iy: Int
        var iz: Int

        if (boundingBox.inside(ray.origin)) {              // does the ray start inside the grid?
            ix = MathUtils.clamp((ox - x0) * nx / (x1 - x0), 0.0, (nx - 1.0)).toInt()
            iy = MathUtils.clamp((oy - y0) * ny / (y1 - y0), 0.0, (ny - 1.0)).toInt()
            iz = MathUtils.clamp((oz - z0) * nz / (z1 - z0), 0.0, (nz - 1.0)).toInt()
        } else {
            val p = ray.linear(t0)  // initial hit point with grid's bounding box
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
            ixStep = +1
            ixStop = nx
        } else {
            txNext = txMin + (nx - ix) * dtx
            ixStep = -1
            ixStop = -1
        }
        if (dx == 0.0) {
            txNext = MathUtils.K_HUGEVALUE
            ixStep = -1
            ixStop = -1
        }
        if (dy > 0) {
            tyNext = tyMin + (iy + 1) * dty
            iyStep = +1
            iyStop = ny
        } else {
            tyNext = tyMin + (ny - iy) * dty
            iyStep = -1
            iyStop = -1
        }
        if (dy == 0.0) {
            tyNext = MathUtils.K_HUGEVALUE
            iyStep = -1
            iyStop = -1
        }
        if (dz > 0) {
            tzNext = tzMin + (iz + 1) * dtz
            izStep = +1
            izStop = nz
        } else {
            tzNext = tzMin + (nz - iz) * dtz
            izStep = -1
            izStop = -1
        }
        if (dz == 0.0) {
            tzNext = MathUtils.K_HUGEVALUE
            izStep = -1
            izStop = -1
        }

        // traverse the grid
        while (true) {
            Counter.count("Grid.hit.traverse")
            val idx = ix + nx * iy + nx * ny * iz
            val `object` = cells[ix + nx * iy + nx * ny * iz]
            val sr2 = Hit(sr.t)
            if (txNext < tyNext && txNext < tzNext) {
                if (null != `object` && `object`.hit(ray, sr2) && sr2.t < txNext) {
                    sr.t = sr2.t
                    sr.normal = sr2.normal
                    if (`object` !is Compound) {
                        sr.geometricObject = `object`
                    } else {
                        sr.geometricObject = sr2.geometricObject
                    }
                    return true
                }

                txNext += dtx
                ix += ixStep

                if (ix == ixStop)
                    return false
            } else {
                if (tyNext < tzNext) {
                    if (null != `object` && `object`.hit(ray, sr2) && sr2.t < tyNext) {
                        sr.t = sr2.t
                        sr.normal = sr2.normal
                        if (`object` !is Compound) {
                            sr.geometricObject = `object`
                        } else {
                            sr.geometricObject = sr2.geometricObject
                        }
                        return true
                    }

                    tyNext += dty
                    iy += iyStep

                    if (iy == iyStop)
                        return false
                } else {
                    if (null != `object` && `object`.hit(ray, sr2) && sr2.t < tzNext) {
                        sr.t = sr2.t
                        sr.normal = sr2.normal
                        if (`object` !is Compound) {
                            sr.geometricObject = `object`
                        } else {
                            sr.geometricObject = sr2.geometricObject
                        }
                        return true
                    }

                    tzNext += dtz
                    iz += izStep

                    if (iz == izStop)
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
    //            GeometricObject geometricObject = cells[ix + nx * iy + nx * ny * iz];
    //            ShadowHit hit2 = new ShadowHit();
    //            hit2.setT(hit.getT());
    //            if (tx_next < ty_next && tx_next < tz_next) {
    //                if (null != geometricObject && geometricObject.shadowHit(ray, hit2) && hit2.getT() < tx_next) {
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
    //                    if (null != geometricObject && geometricObject.shadowHit(ray, hit2) && hit2.getT() < ty_next) {
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
    //                    if (null != geometricObject && geometricObject.shadowHit(ray, hit2) && hit2.getT() < tz_next) {
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

    override fun toString(): String = "Grid(#objs=${objects.size})"

    companion object {
        internal var logInterval = 1000
        protected var factorSize = 500
        protected var maxDepth = 0
    }

}
