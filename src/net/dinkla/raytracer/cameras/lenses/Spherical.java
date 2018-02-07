package net.dinkla.raytracer.cameras.lenses;

import net.dinkla.raytracer.ViewPlane;
import net.dinkla.raytracer.cameras.lenses.AbstractLens;
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

    public double maxLambda;
    public double maxPsi;

    public Spherical(ViewPlane viewPlane) {
        super(viewPlane);
        maxLambda = 180;
        maxPsi = 180;
    }

    public Ray getRaySingle(int r, int c) {
        final double x = viewPlane.size * (c - 0.5 * viewPlane.resolution.hres);
        final double y = viewPlane.size * (r - 0.5 * viewPlane.resolution.vres);
        final Point2D pp = new Point2D(x, y);
        Vector3D direction = getRayDirection(pp, viewPlane.resolution, viewPlane.size);
        Ray ray = new Ray(eye, direction);
        return ray;
    }

    public Ray getRaySampled(int r, int c, Point2D sp) {
        final double x = viewPlane.size * (c - 0.5 * viewPlane.resolution.hres + sp.getX());
        final double y = viewPlane.size * (r - 0.5 * viewPlane.resolution.vres + sp.getY());
        final Point2D pp = new Point2D(x, y);
        Vector3D direction = getRayDirection(pp, viewPlane.resolution, viewPlane.size);
        Ray ray = new Ray(eye, direction);
        return ray;
    }

    protected Vector3D getRayDirection(Point2D pp, Resolution resolution, double s) {
        double x = 2.0 / (s * resolution.hres) * pp.getX();
        double y = 2.0 / (s * resolution.vres) * pp.getY();

        double lambda = x * maxLambda * MathUtils.PI_ON_180;
        double psi = y * maxPsi * MathUtils.PI_ON_180;

        double phi =  Math.PI - lambda;
        double theta = 0.5 *  Math.PI - psi;

        double sinPhi =  Math.sin(phi);
        double cosPhi =  Math.cos(phi);

        double sinTheta =  Math.sin(theta);
        double cosTheta =  Math.cos(theta);

//        Vector3D direction = u.mult(sinTheta * sinPhi).plus(v.mult(cosTheta)).plus(w.mult(sinTheta * cosPhi));
        Vector3D direction = uvw.pp(sinTheta * sinPhi, cosTheta, sinTheta * cosPhi);
        return direction;
    }
}
