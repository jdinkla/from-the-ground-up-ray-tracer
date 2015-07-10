package net.dinkla.raytracer.cameras.lenses;

import net.dinkla.raytracer.ViewPlane;
import net.dinkla.raytracer.math.*;
import net.dinkla.raytracer.utilities.Resolution;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 23.05.2010
 * Time: 20:33:34
 * To change this template use File | Settings | File Templates.
 */
public class Spherical extends AbstractLens {

    public float maxLambda;
    public float maxPsi;

    public Spherical(ViewPlane viewPlane) {
        super(viewPlane);
        maxLambda = 180;
        maxPsi = 180;
    }

    public Ray getRaySingle(int r, int c) {
        final float x = viewPlane.size * (c - 0.5f * viewPlane.resolution.hres());
        final float y = viewPlane.size * (r - 0.5f * viewPlane.resolution.vres());
        final Point2DF pp = new Point2DF(x, y);
        Vector3DF direction = getRayDirection(pp, viewPlane.resolution, viewPlane.size);
        Ray ray = new Ray(eye, direction);
        return ray;
    }

    public Ray getRaySampled(int r, int c, Point2DF sp) {
        final float x = viewPlane.size * (c - 0.5f * viewPlane.resolution.hres() + sp.x());
        final float y = viewPlane.size * (r - 0.5f * viewPlane.resolution.vres() + sp.y());
        final Point2DF pp = new Point2DF(x, y);
        Vector3DF direction = getRayDirection(pp, viewPlane.resolution, viewPlane.size);
        Ray ray = new Ray(eye, direction);
        return ray;
    }

    protected Vector3DF getRayDirection(Point2DF pp, Resolution resolution, float s) {
        float x = 2.0f / (s * resolution.hres()) * pp.x();
        float y = 2.0f / (s * resolution.vres()) * pp.y();

        float lambda = x * maxLambda * MathUtils.PI_ON_180;
        float psi = y * maxPsi * MathUtils.PI_ON_180;

        float phi = (float) Math.PI - lambda;
        float theta = 0.5f * (float) Math.PI - psi;

        float sinPhi = (float) Math.sin(phi);
        float cosPhi = (float) Math.cos(phi);

        float sinTheta = (float) Math.sin(theta);
        float cosTheta = (float) Math.cos(theta);

//        Vector3DF direction = u.mult(sinTheta * sinPhi).plus(v.mult(cosTheta)).plus(w.mult(sinTheta * cosPhi));
        Vector3DF direction = uvw.pp(sinTheta * sinPhi, cosTheta, sinTheta * cosPhi);
        return direction;
    }
}
