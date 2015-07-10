package net.dinkla.raytracer.cameras.lenses;

import net.dinkla.raytracer.ViewPlane;
import net.dinkla.raytracer.math.*;
import net.dinkla.raytracer.utilities.Resolution;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 23.05.2010
 * Time: 13:57:23
 * To change this template use File | Settings | File Templates.
 */
public class FishEye extends AbstractLens {

    public float maxPsi;
    
    public class RayDirection {
        public Vector3D direction = null;
        public float rSquared = 0;
    }

    public FishEye(ViewPlane viewPlane) {
        super(viewPlane);
        maxPsi = 1.0f;
    }

    public Ray getRaySampled(int r, int c, Point2DF sp) {
        Ray ray = null;
        final float x = viewPlane.size * (c - 0.5f * viewPlane.resolution.hres() + sp.x);
        final float y = viewPlane.size * (r - 0.5f * viewPlane.resolution.vres() + sp.y);
        final Point2DF pp = new Point2DF(x, y);
        RayDirection rd = getRayDirection(pp, viewPlane.resolution, viewPlane.size);
        if (rd.rSquared <= 1) {
            ray = new Ray(eye, rd.direction);
        }
        return ray;
    }

    public Ray getRaySingle(int r, int c) {
        Ray ray = null;
        final float x = viewPlane.size * (c - 0.5f * viewPlane.resolution.hres());
        final float y = viewPlane.size * (r - 0.5f * viewPlane.resolution.vres());
        final Point2DF pp = new Point2DF(x, y);
        RayDirection rd = getRayDirection(pp, viewPlane.resolution, viewPlane.size);
        if (rd.rSquared <= 1) {
            ray = new Ray(eye, rd.direction);
        }
        return ray;
    }

    protected RayDirection getRayDirection(Point2DF pp, Resolution resolution, float s) {
        RayDirection rd = new RayDirection();
        float x = 2.0f / (s * resolution.hres()) * pp.x;
        float y = 2.0f / (s * resolution.vres()) * pp.y;
        float rSquared = x * x + y * y;
        if (rSquared <= 1) {
            float r = (float) Math.sqrt(rSquared);
            float psi = r * maxPsi * MathUtils.PI_ON_180;
            float sinPsi = (float) Math.sin(psi);
            float cosPsi = (float) Math.cos(psi);
            float sinAlpha = (float) y / r;
            float cosAlpha = (float) x / r;
//            rd.direction = uvw.u.mult(sinPsi * cosAlpha).plus(uvw.v.mult(sinPsi * sinAlpha)).minus(uvw.w.mult(cosPsi));
            rd.direction = uvw.pm(sinPsi * cosAlpha, sinPsi * sinAlpha, cosPsi);
            rd.rSquared = rSquared;
        } else {
           rd.direction = Vector3D.ZERO;
        }
        return rd;

    }
}
