package net.dinkla.raytracer.cameras.lenses;

import net.dinkla.raytracer.math.Basis;
import net.dinkla.raytracer.math.Point2DF;
import net.dinkla.raytracer.math.Point3DF;
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

    public Ray getRaySampled(int r, int c, Point2DF sp);

    public Point3DF getEye();

    public void setEye(Point3DF eye);

    public Basis getUvw();

    public void setUvw(Basis uvw);

}
