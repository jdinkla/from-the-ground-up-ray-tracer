package net.dinkla.raytracer.lights;

import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.math.Ray;
import net.dinkla.raytracer.math.Vector3DF;
import net.dinkla.raytracer.worlds.World;

/**
 * TODO: DirectionalLight Light implementieren
 * 
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 16.05.2010
 * Time: 09:48:10
 * To change this template use File | Settings | File Templates.
 */
public class DirectionalLight<C extends Color> extends Light<C> {

    public float ls;
    public C color;
    public Vector3DF negatedDirection;

    public DirectionalLight() {
        ls = 1.0f;
        color = (C) C.getWhite();
        negatedDirection = Vector3DF.DOWN.negate();
    }

    @Override
    public C L(World world, Shade sr) {
        return (C) color.mult(ls);
    }
    
    @Override
    public Vector3DF getDirection(Shade sr) {
        return negatedDirection;
    }

    @Override
    public boolean inShadow(World world, Ray ray, Shade sr) {
        return world.inShadow(ray, sr, Float.MAX_VALUE);
    }

    public void setDirection(Vector3DF direction) {
        this.negatedDirection = direction.negate();
    }
    
}
