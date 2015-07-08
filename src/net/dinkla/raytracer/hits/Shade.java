package net.dinkla.raytracer.hits;

import net.dinkla.raytracer.materials.Material;
import net.dinkla.raytracer.math.*;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 10.04.2010
 * Time: 15:52:14
 * To change this template use File | Settings | File Templates.
 *
 * Typical lifecycle
 *
 * <pre>
 * Shade sr = world.getAccelerator().hitObjects(world, ray);
 * if (sr.hitsAnObject) {
 *   RGBColor color = sr.material.shade(sr); ...
 * </pre> 
 *
 */
public class Shade extends Hit {

    // for specular highlights, set by Tracer
    public Ray ray;

    // Recursion depth, set by tracer
    public int depth;

    public Shade() {
        super();
        this.depth = 0;
        this.ray = null;
        this.object = null;
    }

    public int getDepth() {
        return depth;
    }

    public Point3D getHitPoint() {
        return ray.linear(t);
    }

    public Point3D getLocalHitPoint() {
        return ray.linear(t);
    }

    public Material getMaterial() {
        return object.getMaterial();
    }

    public Ray getRay() {
        return ray;
    }

    public void setRay(Ray ray) {
        this.ray = ray;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

}
