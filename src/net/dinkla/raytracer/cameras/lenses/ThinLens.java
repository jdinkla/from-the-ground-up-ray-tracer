package net.dinkla.raytracer.cameras.lenses;

import net.dinkla.raytracer.ViewPlane;
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
        float x = (float) (viewPlane.size * (c - 0.5 * viewPlane.resolution.hres()));
        float y = (float) (viewPlane.size * (r - 0.5 * viewPlane.resolution.vres()));
        return getRay(x, y);
    }

    public Ray getRaySampled(int r, int c, Point2DF sp) {
        float x = (float) (viewPlane.size * (c - 0.5 * viewPlane.resolution.hres() + sp.x()));
        float y = (float) (viewPlane.size * (r - 0.5 * viewPlane.resolution.vres() + sp.y()));
        return getRay(x, y);
    }

    private Ray getRay(float x, float y) {
        Point2DF pp = new Point2DF(x, y);
        Point2DF dp = sampler.sampleUnitDisk();
        Point2DF lp = new Point2DF(dp.x() * lensRadius, dp.y() * lensRadius);
//        Point3DF o = eye.plus(u.mult(lp.x)).plus(v.mult(lp.y));
        Point3DF o = eye.plus(uvw.pp(lp.x(), lp.y(), 0));
        Ray ray = new Ray(eye, getRayDirection(pp, lp));
        return ray;
    }
    
    protected Vector3DF getRayDirection(final Point2DF pixel, final Point2DF lens) {
        final Point2DF p = new Point2DF(pixel.x() * f/d, pixel.y() * f/d);
//        final Vector3DF v1 = u.mult(p.x - lens.x);
//        final Vector3DF v2 = v.mult(p.y - lens.y);
//        final Vector3DF v3 = w.mult(f);
        //final Vector3DF dir = v1.plus(v2).minus(v3).normalize();
        final Vector3DF dir = uvw.pm(1, 1, 1).normalize();
        return dir;
    }

}
