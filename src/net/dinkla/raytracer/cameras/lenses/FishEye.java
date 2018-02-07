package net.dinkla.raytracer.cameras.lenses;

import net.dinkla.raytracer.ViewPlane;
import net.dinkla.raytracer.cameras.lenses.AbstractLens;
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

    public double maxPsi;
    
    public class RayDirection {
        public Vector3D direction = null;
        public double rSquared = 0;
    }

    public FishEye(ViewPlane viewPlane) {
        super(viewPlane);
        maxPsi = 1.0;
    }

    public Ray getRaySampled(int r, int c, Point2D sp) {
        Ray ray = null;
        final double x = viewPlane.size * (c - 0.5 * viewPlane.resolution.hres + sp.getX());
        final double y = viewPlane.size * (r - 0.5 * viewPlane.resolution.vres + sp.getY());
        final Point2D pp = new Point2D(x, y);
        RayDirection rd = getRayDirection(pp, viewPlane.resolution, viewPlane.size);
        if (rd.rSquared <= 1) {
            ray = new Ray(eye, rd.direction);
        }
        return ray;
    }

    public Ray getRaySingle(int r, int c) {
        Ray ray = null;
        final double x = viewPlane.size * (c - 0.5 * viewPlane.resolution.hres);
        final double y = viewPlane.size * (r - 0.5 * viewPlane.resolution.vres);
        final Point2D pp = new Point2D(x, y);
        RayDirection rd = getRayDirection(pp, viewPlane.resolution, viewPlane.size);
        if (rd.rSquared <= 1) {
            ray = new Ray(eye, rd.direction);
        }
        return ray;
    }

    protected RayDirection getRayDirection(Point2D pp, Resolution resolution, double s) {
        RayDirection rd = new RayDirection();
        double x = 2.0f / (s * resolution.hres) * pp.getX();
        double y = 2.0f / (s * resolution.vres) * pp.getY();
        double rSquared = x * x + y * y;
        if (rSquared <= 1) {
            float r = (float) Math.sqrt(rSquared);
            double psi = r * maxPsi * MathUtils.PI_ON_180;
            float sinPsi = (float) Math.sin(psi);
            float cosPsi = (float) Math.cos(psi);
            float sinAlpha = (float) y / r;
            float cosAlpha = (float) x / r;
//            rd.direction = uvw.u.mult(sinPsi * cosAlpha).plus(uvw.v.mult(sinPsi * sinAlpha)).minus(uvw.w.mult(cosPsi));
            rd.direction = uvw.pm(sinPsi * cosAlpha, sinPsi * sinAlpha, cosPsi);
            rd.rSquared = rSquared;
        } else {
           rd.direction = Vector3D.Companion.getZERO();
        }
        return rd;

    }
}
