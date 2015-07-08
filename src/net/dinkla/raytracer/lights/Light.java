package net.dinkla.raytracer.lights;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.math.Ray;
import net.dinkla.raytracer.math.Vector3D;
import net.dinkla.raytracer.worlds.World;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 12.04.2010
 * Time: 14:26:00
 * To change this template use File | Settings | File Templates.
 */
abstract public class Light<C extends Color> {

    public boolean shadows = true;
    abstract public C L(World<C> world, Shade sr);
    abstract public Vector3D getDirection(Shade sr);
    abstract public boolean inShadow(World<C> world, Ray ray, Shade sr);

}
