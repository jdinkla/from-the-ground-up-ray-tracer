package net.dinkla.raytracer.cameras.lenses;

import net.dinkla.raytracer.ViewPlane;
import net.dinkla.raytracer.cameras.lenses.AbstractLens;
import net.dinkla.raytracer.math.*;
import net.dinkla.raytracer.samplers.Sampler;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 23.05.2010
 * Time: 12:40:26
 * To change this template use File | Settings | File Templates.
 */
public class ThinLens extends AbstractLens {

    public Sampler sampler;           // unit disk

    public float lensRadius;
    public float f;
    public float d;
    //public float zoom;

    public ThinLens(ViewPlane viewPlane) {
        super(viewPlane);
        lensRadius = 1.0f;
        f = 1.0f;
        d = 1.0f;
    }

    public Ray getRaySingle(int r, int c) {
        float x = (float) (viewPlane.size * (c - 0.5 * viewPlane.resolution.hres));
        float y = (float) (viewPlane.size * (r - 0.5 * viewPlane.resolution.vres));
        return getRay(x, y);
    }

    public Ray getRaySampled(int r, int c, Point2D sp) {
        float x = (float) (viewPlane.size * (c - 0.5 * viewPlane.resolution.hres + sp.x));
        float y = (float) (viewPlane.size * (r - 0.5 * viewPlane.resolution.vres + sp.y));
        return getRay(x, y);
    }

    private Ray getRay(float x, float y) {
        Point2D pp = new Point2D(x, y);
        Point2D dp = sampler.sampleUnitDisk();
        Point2D lp = new Point2D(dp.x * lensRadius, dp.y * lensRadius);
//        Point3D o = eye.plus(u.mult(lp.x)).plus(v.mult(lp.y));
        Point3D o = eye.plus(uvw.pp(lp.x, lp.y, 0));
        Ray ray = new Ray(eye, getRayDirection(pp, lp));
        return ray;
    }
    
    protected Vector3D getRayDirection(final Point2D pixel, final Point2D lens) {
        final Point2D p = new Point2D(pixel.x * f/d, pixel.y * f/d);
//        final Vector3D v1 = u.mult(p.x - lens.x);
//        final Vector3D v2 = v.mult(p.y - lens.y);
//        final Vector3D v3 = w.mult(f);
        //final Vector3D dir = v1.plus(v2).minus(v3).normalize();
        final Vector3D dir = uvw.pm(1, 1, 1).normalize();
        return dir;
    }

}
