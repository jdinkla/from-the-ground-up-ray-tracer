package net.dinkla.raytracer.math;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 10.04.2010
 * Time: 15:28:13
 * To change this template use File | Settings | File Templates.
 */
public class Ray {

    public final Point3DF o;
    public final Vector3DF d;

    public Ray(final Point3DF origin, final Vector3DF direction) {
        this.o = origin;
        this.d = direction;
    }

    public Ray(Ray ray) {
        this.o = ray.o;
        this.d = ray.d;
    }

    public Point3DF linear(final float t) {
        return o.plus(d.mult(t));
    }

    @Override
    public String toString() {
        return "Ray(" + o.toString() + ", " + d.toString() + ")";
    }

}
