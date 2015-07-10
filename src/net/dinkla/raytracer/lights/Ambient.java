package net.dinkla.raytracer.lights;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.math.Ray;
import net.dinkla.raytracer.math.Vector3D;
import net.dinkla.raytracer.math.Vector3D$;
import net.dinkla.raytracer.math.Vector3DF;
import net.dinkla.raytracer.worlds.World;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 12.04.2010
 * Time: 14:27:41
 * To change this template use File | Settings | File Templates.
 */
public class Ambient<C extends Color> extends Light<C> {

    // emissive material
    public float ls;
    public C color;

    public Ambient() {
        ls = 1.0f;
        color = (C) C.getWhite();
    }
    
    @Override
    public C L(World<C> world, Shade sr) {
        return (C) color.mult(ls);
    }

    @Override
    public Vector3DF getDirection(Shade sr) {
        return Vector3D$.MODULE$.ZERO();
    }

    @Override
    public boolean inShadow(World<C> world, Ray ray, Shade sr) {
        return false; 
    }

}
