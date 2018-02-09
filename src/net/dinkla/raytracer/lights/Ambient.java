package net.dinkla.raytracer.lights;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.math.Ray;
import net.dinkla.raytracer.math.Vector3D;
import net.dinkla.raytracer.worlds.World;

public class Ambient extends Light {

    // emissive material
    public double ls;
    public Color color;

    public Ambient() {
        ls = 1.0;
        color = Color.WHITE;
    }
    
    @Override
    public Color L(World world, Shade sr) {
        return color.mult(ls);
    }

    @Override
    public Vector3D getDirection(Shade sr) {
        return Vector3D.Companion.getZERO();
    }

    @Override
    public boolean inShadow(World world, Ray ray, Shade sr) {
        return false; 
    }

}
