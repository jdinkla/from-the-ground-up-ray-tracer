package net.dinkla.raytracer.cameras.lenses;

import net.dinkla.raytracer.cameras.lenses.AbstractLens;
import net.dinkla.raytracer.ViewPlane;
import net.dinkla.raytracer.math.*;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 12.04.2010
 * Time: 12:20:46
 * To change this template use File | Settings | File Templates.
 */
public class Orthographic extends AbstractLens {

    public float zw;

    public Orthographic(ViewPlane viewPlane) {
        super(viewPlane);
        this.zw = 1111.0f;
    }

    public Ray getRaySampled(int r, int c, Point2D sp) {
        int x = (int) (viewPlane.size * (c - 0.5 * (viewPlane.resolution.hres() - 1) + sp.x));
        int y = (int) (viewPlane.size * (r - 0.5 * (viewPlane.resolution.vres() - 1) + sp.y));
        Ray ray = new Ray(new Point3D(x, y, zw), Vector3D.BACK);
        return ray;
    }

    public Ray getRaySingle(int r, int c) {
        int x = (int) (viewPlane.size * (c - 0.5 * (viewPlane.resolution.hres() - 1)));
        int y = (int) (viewPlane.size * (r - 0.5 * (viewPlane.resolution.vres() - 1)));
        Ray ray = new Ray(new Point3D(x, y, zw), Vector3D.BACK);
        return ray;
    }
}