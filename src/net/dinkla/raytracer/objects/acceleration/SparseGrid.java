package net.dinkla.raytracer.objects.acceleration;

import net.dinkla.raytracer.hits.Hit;
import net.dinkla.raytracer.hits.ShadowHit;
import net.dinkla.raytracer.math.BBox;
import net.dinkla.raytracer.math.MathUtils;
import net.dinkla.raytracer.math.Point3DF;
import net.dinkla.raytracer.math.Ray;
import net.dinkla.raytracer.objects.compound.Compound;
import net.dinkla.raytracer.objects.GeometricObject;
import net.dinkla.raytracer.objects.mesh.Mesh;
import net.dinkla.raytracer.utilities.Timer;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 19.05.2010
 * Time: 23:49:22
 * To change this template use File | Settings | File Templates.
 */
public class SparseGrid extends Grid {

    static final Logger LOGGER = Logger.getLogger(SparseGrid.class);

    //protected GeometricObject[] cells;
    protected Map<Integer, GeometricObject> cellsX;

    public SparseGrid() {
        super();
    }

    public SparseGrid(final Mesh mesh) {
        super(mesh);
    }

    public void initialize() {
        if (isInitialized) {
            return;
        }
        isInitialized = true;
        // super.initialize();

        if (objects.size() == 0) {
            return;
        }

        Timer timer = new Timer();
        timer.start();
        bbox = getBoundingBox();

        float wx = bbox.q.x() - bbox.p.x();
        float wy = bbox.q.y() - bbox.p.y();
        float wz = bbox.q.z() - bbox.p.z();

        float s = (float) Math.pow(wx * wy * wz / objects.size(), (1.0 / 3));
        nx = (int) (multiplier * wx / s + 1);
        ny = (int) (multiplier * wy / s + 1);
        nz = (int) (multiplier * wz / s + 1);

        int numCells = nx * ny * nz;

        LOGGER.info("Grid: numCells=" + numCells + " = " + nx + "*" + ny + "*" + nz);

        //cells = new GeometricObject[numCells];
        cellsX = new TreeMap<Integer, GeometricObject>();
        //cellsX.ensureCapacity(numCells/10);

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

            final BBox objBbox = object.getBoundingBox();

            final int ixmin = (int) MathUtils.clamp((objBbox.p.x() - bbox.p.x()) * nx / wx, 0, nx - 1);
            final int iymin = (int) MathUtils.clamp((objBbox.p.y() - bbox.p.y()) * ny / wy, 0, ny - 1);
            final int izmin = (int) MathUtils.clamp((objBbox.p.z() - bbox.p.z()) * nz / wz, 0, nz - 1);

            final int ixmax = (int) MathUtils.clamp((objBbox.q.x() - bbox.p.x()) * nx / wx, 0, nx - 1);
            final int iymax = (int) MathUtils.clamp((objBbox.q.y() - bbox.p.y()) * ny / wy, 0, ny - 1);
            final int izmax = (int) MathUtils.clamp((objBbox.q.z() - bbox.p.z()) * nz / wz, 0, nz - 1);

            for (int iz = izmin; iz <= izmax; iz++) {
                for (int iy = iymin; iy <= iymax; iy++) {
                    for (int ix = ixmin; ix <= ixmax; ix++) {
                        int index = iz * nx * ny + iy * nx + ix;
                        GeometricObject go = cellsX.get(index);
                        if (null == go) {
                            cellsX.put(index, object);
                        } else if (go instanceof Compound) {
                            Compound c = (Compound) go;
                            c.add(object);
                        } else {
                            Compound c = new Compound();
                            c.add(go);
                            c.add(object);
                            cellsX.put(index, c);
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
                        counts[index]++;
                    }
                }
            }
        }

        timer.stop();
        LOGGER.info("Creating grid took " + timer.getDuration() + " ms");

        statistics(numCells, counts);

    }

    @Override
    public boolean hit(final Ray ray, Hit sr) {
        if (!bbox.hit(ray)) {
            return false;
        }

        float ox = ray.getO().x();
        float oy = ray.getO().y();
        float oz = ray.getO().z();
        float dx = ray.getD().x();
        float dy = ray.getD().y();
        float dz = ray.getD().z();

        float x0 = bbox.p.x();
        float y0 = bbox.p.y();
        float z0 = bbox.p.z();
        float x1 = bbox.q.x();
        float y1 = bbox.q.y();
        float z1 = bbox.q.z();

        float tx_min, ty_min, tz_min;
        float tx_max, ty_max, tz_max;

        // the following code includes modifications from Shirley and Morley (2003)

        final float a = 1.0f / dx;
        if (a >= 0) {
            tx_min = (x0 - ox) * a;
            tx_max = (x1 - ox) * a;
        } else {
            tx_min = (x1 - ox) * a;
            tx_max = (x0 - ox) * a;
        }

        final float b = 1.0f / dy;
        if (b >= 0) {
            ty_min = (y0 - oy) * b;
            ty_max = (y1 - oy) * b;
        } else {
            ty_min = (y1 - oy) * b;
            ty_max = (y0 - oy) * b;
        }

        final float c = 1.0f / dz;
        if (c >= 0) {
            tz_min = (z0 - oz) * c;
            tz_max = (z1 - oz) * c;
        } else {
            tz_min = (z1 - oz) * c;
            tz_max = (z0 - oz) * c;
        }

        float t0, t1;

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

        if (t0 > t1)
            return false;

        // initial cell coordinates

        int ix, iy, iz;

        if (bbox.inside(ray.getO())) {              // does the ray start inside the grid?
            ix = (int) MathUtils.clamp((ox - x0) * nx / (x1 - x0), 0, nx - 1);
            iy = (int) MathUtils.clamp((oy - y0) * ny / (y1 - y0), 0, ny - 1);
            iz = (int) MathUtils.clamp((oz - z0) * nz / (z1 - z0), 0, nz - 1);
        } else {
            Point3DF p = ray.linear(t0);  // initial hit point with grid's bounding box
            ix = (int) MathUtils.clamp((p.x() - x0) * nx / (x1 - x0), 0, nx - 1);
            iy = (int) MathUtils.clamp((p.y() - y0) * ny / (y1 - y0), 0, ny - 1);
            iz = (int) MathUtils.clamp((p.z() - z0) * nz / (z1 - z0), 0, nz - 1);
        }

        // ray parameter increments per cell in the x, y, and z directions

        float dtx = (tx_max - tx_min) / nx;
        float dty = (ty_max - ty_min) / ny;
        float dtz = (tz_max - tz_min) / nz;

        float tx_next, ty_next, tz_next;
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
            int idx = ix + nx * iy + nx * ny * iz;
            GeometricObject object = cellsX.get(idx);
            //GeometricObject object = cells[ix + nx * iy + nx * ny * iz];
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

    @Override
    public boolean shadowHit(Ray ray, ShadowHit tmin) {
        Hit h = new Hit();
        h.setT(tmin.getT());
        boolean b = hit(ray, h);
        tmin.setT(h.getT());
        return b;
    }

}