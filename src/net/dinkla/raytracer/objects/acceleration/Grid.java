package net.dinkla.raytracer.objects.acceleration;

import net.dinkla.raytracer.hits.Hit;
import net.dinkla.raytracer.hits.ShadowHit;
import net.dinkla.raytracer.math.*;
import net.dinkla.raytracer.objects.compound.Compound;
import net.dinkla.raytracer.objects.GeometricObject;
import net.dinkla.raytracer.objects.mesh.Mesh;
import net.dinkla.raytracer.utilities.Counter;
import net.dinkla.raytracer.utilities.Timer;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 01.05.2010
 * Time: 15:05:01
 * To change this template use File | Settings | File Templates.
 */
public class Grid extends Compound {

    static final Logger LOGGER = Logger.getLogger(Grid.class);

    static int logInterval = 1000;

    protected Mesh mesh;

    protected GeometricObject[] cells;

    protected int nx, ny, nz;
    
    protected double multiplier;

    protected static int factorSize = 500;

    protected static int maxDepth = 0;

    protected int depth;

    public Grid() {
        super();
        multiplier = 2.0f;
        depth = 0;
    }

    public Grid(final Mesh mesh) {
        super();
        this.mesh = mesh;
        multiplier = 2.0f;
        depth = 0;
    }

    public void initialize() {
        if (isInitialized) {
            return;
        }
        super.initialize();

        if (objects.size() == 0) {
            return;
        }

        Timer timer = new Timer();
        timer.start();
        bbox = getBoundingBox();        // TODO: wird in initialize() aufgerufen !!!

        double wx = bbox.getQ().getX() - bbox.getP().getX();
        double wy = bbox.getQ().getY() - bbox.getP().getY();
        double wz = bbox.getQ().getZ() - bbox.getP().getZ();

        double s = Math.pow(wx * wy * wz / objects.size(), (1.0 / 3));
        nx = (int) (multiplier * wx / s + 1);
        ny = (int) (multiplier * wy / s + 1);
        nz = (int) (multiplier * wz / s + 1);

        int numCells = nx * ny * nz;

        LOGGER.info("Grid: numCells=" + numCells + " = " + nx + "*" + ny + "*" + nz);

        cells = new GeometricObject[numCells];

        int counts[] = new int[numCells];

        // initialize
        for (int i = 0; i < numCells; i++) {
            //cells[i] = null;
            counts[i] = 0;
        }

        int objectsToGo = objects.size(); 

        // insert the objects into the cells
        for (GeometricObject object : objects) {

            if (objectsToGo % logInterval == 0) {
                LOGGER.info("Grid: " + objectsToGo + " objects to grid");
            }
            objectsToGo--;

            BBox objBbox = object.getBoundingBox();

            int ixmin = (int) MathUtils.clamp((objBbox.getP().getX() - bbox.getP().getX()) * nx / wx, 0, nx - 1);
            int iymin = (int) MathUtils.clamp((objBbox.getP().getY() - bbox.getP().getY()) * ny / wy, 0, ny - 1);
            int izmin = (int) MathUtils.clamp((objBbox.getP().getZ() - bbox.getP().getZ()) * nz / wz, 0, nz - 1);

            int ixmax = (int) MathUtils.clamp((objBbox.getQ().getX() - bbox.getP().getX()) * nx / wx, 0, nx - 1);
            int iymax = (int) MathUtils.clamp((objBbox.getQ().getY() - bbox.getP().getY()) * ny / wy, 0, ny - 1);
            int izmax = (int) MathUtils.clamp((objBbox.getQ().getZ() - bbox.getP().getZ()) * nz / wz, 0, nz - 1);

            for (int iz = izmin; iz <= izmax; iz++) {
                for (int iy = iymin; iy <= iymax; iy++) {
                    for (int ix = ixmin; ix <= ixmax; ix++) {
                        int index = iz * nx * ny + iy * nx + ix;
                        if (null == cells[index]) {
                            cells[index] = object;
                        } else if (cells[index] instanceof Grid) {
                            Grid c = (Grid) cells[index];
                            c.add(object);
                        } else if (cells[index] instanceof Compound) {
                            Compound c = (Compound) cells[index];
                            if (c.size() > factorSize && depth < maxDepth) {
                                Grid g = new Grid();
                                g.add(c.getObjects());
                                g.add(object);
                                g.depth = depth + 1;

                                cells[index] = g;
                            } else {
                                c.add(object);
                            }
                        } else {
                            Compound c = new Compound();
                            c.add(cells[index]);
                            c.add(object);
                            cells[index] = c;
                        }
                        counts[index]++;
                    }
                }
            }
        }

        timer.stop();
        LOGGER.info("Creating grid took " + timer.getDuration() + " ms");

        timer.start();
        for (GeometricObject go : cells) {
            if (go instanceof Grid) {
                go.initialize();
            }
        }
        timer.stop();
        LOGGER.info("Creating subgrids took " + timer.getDuration() + " ms");

        if ( 0 == depth) {
            statistics(numCells, counts);
        }

    }

    protected void statistics(int numCells, int[] counts) {
        Histogram hist = new Histogram();
        
        int numInCells = 0;
        for (int j = 0; j < numCells; j++) {
            int count = counts[j];
            numInCells += count;
            hist.add(count);
        }

        LOGGER.info("Grid statistics");
        LOGGER.info("multiplier="  + multiplier
                + ", numObjects=" + objects.size()
                + ", numCells=" + numCells
                + ", numObjects in cells="  + numInCells);
        
        for (Integer key : hist.keySet()) {
            int value = hist.get(key);
            LOGGER.info("Grid: " + key + ": " + value + " [" + (value * 100.0 / numInCells) + "%]");
        }
    }

    @Override
    public boolean hit(final Ray ray, Hit sr) {
        //if (depth > 0) return false;
        if (!bbox.hit(ray)) {
            Counter.count("Grid.hit.bbox");
            return false;
        }
        Counter.count("Grid.hit");
        
        double ox = ray.getO().getX();
        double oy = ray.getO().getY();
        double oz = ray.getO().getZ();
        double dx = ray.getD().getX();
        double dy = ray.getD().getY();
        double dz = ray.getD().getZ();

        double x0 = bbox.getP().getX();
        double y0 = bbox.getP().getY();
        double z0 = bbox.getP().getZ();
        double x1 = bbox.getQ().getX();
        double y1 = bbox.getQ().getY();
        double z1 = bbox.getQ().getZ();

        double tx_min, ty_min, tz_min;
        double tx_max, ty_max, tz_max;

        // the following code includes modifications from Shirley and Morley (2003)

        final double a = 1.0 / dx;
        if (a >= 0) {
            tx_min = (x0 - ox) * a;
            tx_max = (x1 - ox) * a;
        } else {
            tx_min = (x1 - ox) * a;
            tx_max = (x0 - ox) * a;
        }

        final double b = 1.0 / dy;
        if (b >= 0) {
            ty_min = (y0 - oy) * b;
            ty_max = (y1 - oy) * b;
        } else {
            ty_min = (y1 - oy) * b;
            ty_max = (y0 - oy) * b;
        }

        final double c = 1.0 / dz;
        if (c >= 0) {
            tz_min = (z0 - oz) * c;
            tz_max = (z1 - oz) * c;
        } else {
            tz_min = (z1 - oz) * c;
            tz_max = (z0 - oz) * c;
        }

        double t0, t1;

        if (tx_min > ty_min)
            t0 = tx_min;
        else
            t0 = ty_min;

        if (tz_min > t0)
            t0 = tz_min;

        if (tx_max < ty_max)
            t1 = tx_max;
        else
            t1 = ty_max;

        if (tz_max < t1)
            t1 = tz_max;

        if (t0 > t1) {
            Counter.count("Grid.hit.t0>t1");
            return false;
        }

        // initial cell coordinates

        int ix, iy, iz;

        if (bbox.inside(ray.getO())) {              // does the ray start inside the grid?
            ix = (int) MathUtils.clamp((ox - x0) * nx / (x1 - x0), 0, nx - 1);
            iy = (int) MathUtils.clamp((oy - y0) * ny / (y1 - y0), 0, ny - 1);
            iz = (int) MathUtils.clamp((oz - z0) * nz / (z1 - z0), 0, nz - 1);
        } else {
            Point3D p = ray.linear(t0);  // initial hit point with grid's bounding box
            ix = (int) MathUtils.clamp((p.getX() - x0) * nx / (x1 - x0), 0, nx - 1);
            iy = (int) MathUtils.clamp((p.getY() - y0) * ny / (y1 - y0), 0, ny - 1);
            iz = (int) MathUtils.clamp((p.getZ() - z0) * nz / (z1 - z0), 0, nz - 1);
        }

        // ray parameter increments per cell in the x, y, and z directions

        double dtx = (tx_max - tx_min) / nx;
        double dty = (ty_max - ty_min) / ny;
        double dtz = (tz_max - tz_min) / nz;

        double tx_next, ty_next, tz_next;
        int ix_step, iy_step, iz_step;
        int ix_stop, iy_stop, iz_stop;

        if (dx > 0) {
            tx_next = tx_min + (ix + 1) * dtx;
            ix_step = +1;
            ix_stop = nx;
        } else {
            tx_next = tx_min + (nx - ix) * dtx;
            ix_step = -1;
            ix_stop = -1;
        }
        if (dx == 0.0) {
            tx_next = MathUtils.K_HUGEVALUE;
            ix_step = -1;
            ix_stop = -1;
        }
        if (dy > 0) {
            ty_next = ty_min + (iy + 1) * dty;
            iy_step = +1;
            iy_stop = ny;
        } else {
            ty_next = ty_min + (ny - iy) * dty;
            iy_step = -1;
            iy_stop = -1;
        }
        if (dy == 0.0) {
            ty_next = MathUtils.K_HUGEVALUE;
            iy_step = -1;
            iy_stop = -1;
        }
        if (dz > 0) {
            tz_next = tz_min + (iz + 1) * dtz;
            iz_step = +1;
            iz_stop = nz;
        } else {
            tz_next = tz_min + (nz - iz) * dtz;
            iz_step = -1;
            iz_stop = -1;
        }
        if (dz == 0.0) {
            tz_next = MathUtils.K_HUGEVALUE;
            iz_step = -1;
            iz_stop = -1;
        }

        // traverse the grid
        while (true) {
            Counter.count("Grid.hit.traverse");
            int idx = ix + nx * iy + nx * ny * iz;
            GeometricObject object = cells[ix + nx * iy + nx * ny * iz];
            Hit sr2 = new Hit(sr.getT());
            if (tx_next < ty_next && tx_next < tz_next) {
                if (null != object && object.hit(ray, sr2) && sr2.getT() < tx_next) {
                    sr.setT(sr2.getT());
                    sr.setNormal(sr2.getNormal());
                    if (!(object instanceof Compound)) {
                        sr.setObject(object);
                    } else {
                        sr.setObject(sr2.getObject());
                    }
                    return true;
                }

                tx_next += dtx;
                ix += ix_step;

                if (ix == ix_stop)
                    return (false);
            } else {
                if (ty_next < tz_next) {
                    if (null != object && object.hit(ray, sr2) && sr2.getT() < ty_next) {
                        sr.setT(sr2.getT());
                        sr.setNormal(sr2.getNormal());
                        if (!(object instanceof Compound)) {
                            sr.setObject(object);
                        } else {
                            sr.setObject(sr2.getObject());
                        }
                        return (true);
                    }

                    ty_next += dty;
                    iy += iy_step;

                    if (iy == iy_stop)
                        return (false);
                } else {
                    if (null != object && object.hit(ray, sr2) && sr2.getT() < tz_next) {
                        sr.setT(sr2.getT());
                        sr.setNormal(sr2.getNormal());
                        if (!(object instanceof Compound)) {
                            sr.setObject(object);
                        } else {
                            sr.setObject(sr2.getObject());
                        }
                        return (true);
                    }

                    tz_next += dtz;
                    iz += iz_step;

                    if (iz == iz_stop)
                        return (false);
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
//        double ox = ray.o.x;
//        double oy = ray.o.y;
//        double oz = ray.o.z;
//        double dx = ray.d.x;
//        double dy = ray.d.y;
//        double dz = ray.d.z;
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
//        if (bbox.inside(ray.o)) {              // does the ray start inside the grid?
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

    @Override
    public boolean shadowHit(Ray ray, ShadowHit tmin) {
        Counter.count("Grid.shadowHit");
        Hit h = new Hit();
        h.setT(tmin.getT());
        boolean b = hit(ray, h);
        tmin.setT(h.getT());
        return b;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    @Override
    public String toString() {
        return super.toString() + " (" + objects.size() + " elements)";
    }

}
