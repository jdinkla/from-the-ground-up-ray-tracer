package net.dinkla.raytracer.math;

import net.dinkla.raytracer.objects.GeometricObject;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 10.04.2010
 * Time: 15:28:49
 * To change this template use File | Settings | File Templates.
 */
public class BBox {

    public final Point3DF p;
    public final Point3DF q;

    public BBox() {
        p = null;
        q = null;
    }
    
    public BBox(final Point3DF p, final Point3DF q) {
        if (null != p && null != q) {
            if (p.x > q.x || p.y > q.y || p.z > q.z) {
                int a = 2;
            }
            assert p.x <= q.x && p.y <= q.y && p.z <= q.z;
        }
        this.p = p;
        this.q = q;
    }

    /**
     * Is Point r inside the bounding box?
     *
     * @param r     A point.
     * @return      True, if the point r is inside the bounding box.
     */
    public boolean inside(final Point3DF r) {
        boolean isX = r.x > p.x && r.x < q.x;
        boolean isY = r.y > p.y && r.y < q.y;
        boolean isZ = r.z > p.z && r.z < q.z;
        return (isX && isY && isZ);
    }

    static public class Hit {
        public final float t0;
        public final float t1;
        public final boolean isHit;

        public Hit(final float t0, final float t1) {
            this.t0 = t0;
            this.t1 = t1;
            isHit = t0 < t1 && t1 > MathUtils.K_EPSILON;
        }

        public Hit() {
            isHit = false;
            t0 = Float.NaN;
            t1 = Float.NaN;
        }
    }

    public Hit hitX(final Ray ray) {
        if (null == p && null == q) {
            return new Hit();
        }
        float tx_min, ty_min, tz_min;
        float tx_max, ty_max, tz_max;

        float a = 1.0f / ray.d.x;
        if (a >= 0) {
            tx_min = (p.x - ray.o.x) * a;
            tx_max = (q.x - ray.o.x) * a;
        } else {
            tx_min = (q.x - ray.o.x) * a;
            tx_max = (p.x - ray.o.x) * a;
        }

        float b = 1.0f / ray.d.y;
        if (b >= 0) {
            ty_min = (p.y - ray.o.y) * b;
            ty_max = (q.y - ray.o.y) * b;
        } else {
            ty_min = (q.y - ray.o.y) * b;
            ty_max = (p.y - ray.o.y) * b;
        }

        float c = 1.0f / ray.d.z;
        if (c >= 0) {
            tz_min = (p.z - ray.o.z) * c;
            tz_max = (q.z - ray.o.z) * c;
        } else {
            tz_min = (q.z - ray.o.z) * c;
            tz_max = (p.z - ray.o.z) * c;
        }

        float t0, t1;

        // find largest entering t value
        if (tx_min > ty_min) {
            t0 = tx_min;
        } else {
            t0 = ty_min;
        }

        if (tz_min > t0) {
            t0 = tz_min;
        }

        // find smallest exiting t value
        if (tx_max < ty_max) {
            t1 = tx_max;
        } else {
            t1 = ty_max;
        }
        if (tz_max < t1) {
            t1 = tz_max;
        }

        return new Hit(t0, t1);
    }


    public boolean hit(final Ray ray) {
        if (null == p && null == q) {
            return false;
        }

        float tx_min, ty_min, tz_min;
        float tx_max, ty_max, tz_max;

        float a = 1.0f / ray.d.x;
        if (a >= 0) {
            tx_min = (p.x - ray.o.x) * a;
            tx_max = (q.x - ray.o.x) * a;
        } else {
            tx_min = (q.x - ray.o.x) * a;
            tx_max = (p.x - ray.o.x) * a;
        }

        float b = 1.0f / ray.d.y;
        if (b >= 0) {
            ty_min = (p.y - ray.o.y) * b;
            ty_max = (q.y - ray.o.y) * b;
        } else {
            ty_min = (q.y - ray.o.y) * b;
            ty_max = (p.y - ray.o.y) * b;
        }

        float c = 1.0f / ray.d.z;
        if (c >= 0) {
            tz_min = (p.z - ray.o.z) * c;
            tz_max = (q.z - ray.o.z) * c;
        } else {
            tz_min = (q.z - ray.o.z) * c;
            tz_max = (p.z - ray.o.z) * c;
        }

        float t0, t1;

        // find largest entering t value
        if (tx_min > ty_min) {
            t0 = tx_min;
        } else {
            t0 = ty_min;
        }

        if (tz_min > t0) {
            t0 = tz_min;
        }

        // find smallest exiting t value
        if (tx_max < ty_max) {
            t1 = tx_max;
        } else {
            t1 = ty_max;
        }
        if (tz_max < t1) {
            t1 = tz_max;
        }

        return (t0 < t1 && t1 > MathUtils.K_EPSILON);
    }

    static public BBox create(final Point3DF v0, final Point3DF v1, final Point3DF v2) {
        float x0 = Float.POSITIVE_INFINITY;
        float x1 = Float.NEGATIVE_INFINITY;
        if (v0.x < x0) {
            x0 = v0.x;
        }
        if (v1.x < x0) {
            x0 = v1.x;
        }
        if (v2.x < x0) {
            x0 = v2.x;
        }
        if (v0.x > x1) {
            x1 = v0.x;
        }
        if (v1.x > x1) {
            x1 = v1.x;
        }
        if (v2.x > x1) {
            x1 = v2.x;
        }
        float y0 = Float.POSITIVE_INFINITY;
        float y1 = Float.NEGATIVE_INFINITY;
        if (v0.y < y0) {
            y0 = v0.y;
        }
        if (v1.y < y0) {
            y0 = v1.y;
        }
        if (v2.y < y0) {
            y0 = v2.y;
        }
        if (v0.y > y1) {
            y1 = v0.y;
        }
        if (v1.y > y1) {
            y1 = v1.y;
        }
        if (v2.y > y1) {
            y1 = v2.y;
        }
        float z0 = Float.POSITIVE_INFINITY;
        float z1 = Float.NEGATIVE_INFINITY;
        if (v0.z < z0) {
            z0 = v0.z;
        }
        if (v1.z < z0) {
            z0 = v1.z;
        }
        if (v2.z < z0) {
            z0 = v2.z;
        }
        if (v0.z > z1) {
            z1 = v0.z;
        }
        if (v1.z > z1) {
            z1 = v1.z;
        }
        if (v2.z > z1) {
            z1 = v2.z;
        }
        return new BBox(new Point3DF(x0 - MathUtils.K_EPSILON, y0 - MathUtils.K_EPSILON, z0 - MathUtils.K_EPSILON),
                new Point3DF(x1 + MathUtils.K_EPSILON, y1 + MathUtils.K_EPSILON, z1 + MathUtils.K_EPSILON));
    }

    public static BBox create(final List<GeometricObject> objects) {
        if (objects.size() > 0) {
            Point3DF p = PointUtilities.minCoordinates(objects);
            Point3DF q = PointUtilities.maxCoordinates(objects);
            return new BBox(p, q);
        } else {
            return new BBox();
        }
    }

    public float getVolume() {
        if (null == p) {
            return 0;
        } else {
            Vector3DF width = q.minus(p);
            return width.x * width.y * width.z;
        }
    }

    public boolean isContainedIn(final BBox bbox) {
        boolean bX = bbox.p.x <= p.x && q.x <= bbox.q.x;
        boolean bY = bbox.p.y <= p.y && q.y <= bbox.q.y;
        boolean bZ = bbox.p.z <= p.z && q.z <= bbox.q.z;
        return bX && bY && bZ;
    }    

    /**
     * Restrict to bbox.
     * @param bbox
     * @return
     */
    public BBox clipTo(final BBox bbox) {
        if (isContainedIn(bbox)) {
            return this;
        }
        float px = Math.max(p.x, bbox.p.x);
        float py = Math.max(p.y, bbox.p.y);
        float pz = Math.max(p.z, bbox.p.z);

        float qx = Math.min(q.x, bbox.q.x);
        float qy = Math.min(q.y, bbox.q.y);
        float qz = Math.min(q.z, bbox.q.z);

        return new BBox(new Point3DF(px, py, pz), new Point3DF(qx, qy, qz));
    }


    public BBox splitLeft(final Axis axis, final float split) {
        switch(axis) {
            case X:
                return new BBox(p, new Point3DF(split, q.y, q.z));
            case Y:
                return new BBox(p, new Point3DF(q.x, split, q.z));
            case Z:
                return new BBox(p, new Point3DF(q.x, q.y, split));
        }
        return null;
    }

    public BBox splitRight(final Axis axis, final float split) {
        switch(axis) {
            case X:
                return new BBox(new Point3DF(split, p.y, p.z), q);
            case Y:
                return new BBox(new Point3DF(p.x, split, p.z), q);
            case Z:
                return new BBox(new Point3DF(p.x, p.y, split), q);
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (null != obj) {
            if (obj instanceof BBox) {
                BBox o = (BBox) obj;
                return p.equals(o.p) && q.equals(o.q);
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "BBox " + p.toString() + "-" + q.toString();
    }
    
}
