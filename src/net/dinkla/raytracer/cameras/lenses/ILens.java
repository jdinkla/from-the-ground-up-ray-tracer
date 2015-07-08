package net.dinkla.raytracer.cameras.lenses;

import net.dinkla.raytracer.math.Basis;
import net.dinkla.raytracer.math.Point2D;
import net.dinkla.raytracer.math.Point3D;
import net.dinkla.raytracer.math.Ray;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 07.06.2010
 * Time: 19:37:10
 * To change this template use File | Settings | File Templates.
 */
public interface ILens {

    public Ray getRaySingle(int r, int c);

    public Ray getRaySampled(int r, int c, Point2D sp);

    public Point3D getEye();

    public void setEye(Point3D eye);

    public Basis getUvw();

    public void setUvw(Basis uvw);

}
