package net.dinkla.raytracer.objects;

import net.dinkla.raytracer.hits.Hit;
import net.dinkla.raytracer.hits.ShadowHit;
import net.dinkla.raytracer.math.*;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 21.04.2010
 * Time: 20:01:41
 * To change this template use File | Settings | File Templates.
 */
public class AlignedBox extends GeometricObject {

    public final Point3DF p;
    public final Point3DF q;

    public AlignedBox(final Point3DF p, final Point3DF q) {
        this.p = p;
        this.q = q;        
    }

    @Override
    public boolean hit(final Ray ray, Hit sr) {
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
        int faceIn, faceOut;
        // find largest entering t value
        if (tx_min > ty_min) {
            t0 = tx_min;
            faceIn = (a >= 0) ? 0 : 3;
        } else {
            t0 = ty_min;
            faceIn = (b >= 0) ? 1 : 4;
        }
        if (tz_min > t0) {
            t0 = tz_min;
            faceIn = (c >= 0) ? 2 : 5;
        }

        // find smallest exiting t value
        if (tx_max < ty_max) {
            t1 = tx_max;
            faceOut = (a >= 0) ? 3 : 0;
        } else {
            t1 = ty_max;
            faceOut = (b >= 0) ? 4 : 1;
        }
        if (tz_max < t1) {
            t1 = tz_max;
            faceOut = (c >= 0) ? 5 : 2;
        }

        if (t0 < t1 && t1 > MathUtils.K_EPSILON) {
            if ( t0 > MathUtils.K_EPSILON) {
                sr.setT(t0);
                sr.setNormal(getNormal(faceIn));
            } else {
                sr.setT(t1);
                sr.setNormal(getNormal(faceOut));
            }            
            //sr.localHitPoint = ray.linear(tmin.getValue());
            return true;
        }
        return false;
    }

    @Override
    public boolean shadowHit(final Ray ray, ShadowHit tmin) {
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
        int faceIn, faceOut;
        // find largest entering t value
        if (tx_min > ty_min) {
            t0 = tx_min;
            faceIn = (a >= 0) ? 0 : 3;
        } else {
            t0 = ty_min;
            faceIn = (b >= 0) ? 1 : 4;
        }
        if (tz_min > t0) {
            t0 = tz_min;
            faceIn = (c >= 0) ? 2 : 5;
        }

        // find smallest exiting t value
        if (tx_max < ty_max) {
            t1 = tx_max;
            faceOut = (a >= 0) ? 3 : 0;
        } else {
            t1 = ty_max;
            faceOut = (b >= 0) ? 4 : 1;
        }
        if (tz_max < t1) {
            t1 = tz_max;
            faceOut = (c >= 0) ? 5 : 2;
        }

        if (t0 < t1 && t1 > MathUtils.K_EPSILON) {
            if ( t0 > MathUtils.K_EPSILON) {
                tmin.setT(t0);
            } else {
                tmin.setT(t1);
            }
            return true;
        }
        return false;
    }

    Normal getNormal(final int face) {
        switch (face) {
            case 0: return new Normal(-1,  0,  0);
            case 1: return new Normal( 0, -1,  0);
            case 2: return new Normal( 0,  0, -1);
            case 3: return new Normal( 1,  0,  0);
            case 4: return new Normal( 0,  1,  0);
            case 5: return new Normal( 0,  0,  1);
        }
        return null;
    }

    @Override
    public BBox getBoundingBox() {
        return new BBox(p, q);
    }
    
}
