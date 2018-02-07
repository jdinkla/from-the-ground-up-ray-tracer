package net.dinkla.raytracer.cameras.lenses;

import net.dinkla.raytracer.ViewPlane;
import net.dinkla.raytracer.math.*;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 12.04.2010
 * Time: 12:20:46
 * To change this template use File | Settings | File Templates.
 */
public class Pinhole extends AbstractLens {

    static final Logger LOGGER = Logger.getLogger(Pinhole.class);

    public double d;
    // TODO zoom camera
//    public double zoom;

    public Pinhole(ViewPlane viewPlane) {
        super(viewPlane);            
        this.d = 1.0;
//        this.zoom = zoom;
        //viewPlane.size /= zoom;
    }

    public Ray getRaySingle(int r, int c) {
        double x =  (viewPlane.size * (c - 0.5 * viewPlane.resolution.hres));
        double y =  (viewPlane.size * (r - 0.5 * viewPlane.resolution.vres));
        Ray ray = new Ray(eye, getRayDirection(x, y));
        return ray;
    }
    
    public Ray getRaySampled(int r, int c, Point2D sp) {
        double x =  (viewPlane.size * (c - 0.5 * viewPlane.resolution.hres + sp.getX()));
        double y =  (viewPlane.size * (r - 0.5 * viewPlane.resolution.vres + sp.getY()));
        Ray ray = new Ray(eye, getRayDirection(x, y));
        return ray;
    }

    protected Vector3D getRayDirection(double x, double y) {
        // xu + yv - dw
//        Vector3D dir = u.mult(x).plus(v.mult(y)).minus(w.mult(d));
        Vector3D dir = uvw.pm(x, y, d);
        return dir.normalize();
    }

}
