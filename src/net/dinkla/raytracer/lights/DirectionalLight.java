package net.dinkla.raytracer.lights;

import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.math.Ray;
import net.dinkla.raytracer.math.Vector3D;
import net.dinkla.raytracer.worlds.World;

/**
 * TODO: DirectionalLight Light implementieren
 */
public class DirectionalLight extends Light {

    public double ls;
    public Color color;
    public Vector3D negatedDirection;

    public DirectionalLight() {
        ls = 1.0;
        color =  Color.WHITE;
        negatedDirection = Vector3D.Companion.getDOWN().negate();
    }

    @Override
    public Color L(World world, Shade sr) {
        return  color.mult(ls);
    }
    
    @Override
    public Vector3D getDirection(Shade sr) {
        return negatedDirection;
    }

    @Override
    public boolean inShadow(World world, Ray ray, Shade sr) {
        return world.inShadow(ray, sr, Double.MAX_VALUE);
    }

    public void setDirection(Vector3D direction) {
        this.negatedDirection = direction.negate();
    }
    
}
