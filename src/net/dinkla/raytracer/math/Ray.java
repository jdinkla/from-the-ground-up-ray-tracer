package net.dinkla.raytracer.math;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 10.04.2010
 * Time: 15:28:13
 * To change this template use File | Settings | File Templates.
 */
public class Ray {

    public final Point3D o;
    public final Vector3D d;

    public Ray(final Point3D origin, final Vector3D direction) {
        this.o = origin;
        this.d = direction;
    }

    public Ray(Ray ray) {
        this.o = ray.o;
        this.d = ray.d;
    }

    public Point3D linear(final float t) {
        return o.plus(d.mult(t));        
    }

    @Override
    public String toString() {
        return "Ray(" + o.toString() + ", " + d.toString() + ")";
    }

}
