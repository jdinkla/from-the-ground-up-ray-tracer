package net.dinkla.raytracer.lights;

import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.math.Normal;
import net.dinkla.raytracer.math.Point3D;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 14.04.2010
 * Time: 21:24:25
 * To change this template use File | Settings | File Templates.
 */
public interface ILightSource {

    public Point3D sample();

    public float pdf(Shade sr);

    public Normal getNormal(final Point3D p);
    
}
