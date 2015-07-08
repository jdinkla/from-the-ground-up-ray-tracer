package net.dinkla.raytracer.math;

import static java.lang.Math.sqrt;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 10.04.2010
 * Time: 15:17:35
 * To change this template use File | Settings | File Templates.
 */
public class Vector3D extends Element3D {

    public static final Vector3D ZERO = new Vector3D(0, 0, 0);
    static public final Vector3D UP = new Vector3D(0, 1, 0);
    static public final Vector3D DOWN = new Vector3D(0, -1, 0);
    public static final Vector3D JITTER = new Vector3D(0.0072f, 1.0f, 0.0034f);
    public static final Vector3D BACK = new Vector3D(0, 0, -1);

    public Vector3D(final float x, final float y, final float z) {
        super(x, y, z);
    }

    public Vector3D(final Element3D e) {
        super(e);
    }

    public Vector3D plus(final Vector3D v) {
        return new Vector3D(x + v.x, y + v.y, z + v.z);
    }

    public Vector3D minus(final Vector3D v) {
        return new Vector3D(x - v.x, y - v.y, z - v.z);
    }

    public Vector3D mult(final float s) {
        return new Vector3D(s * x, s * y, s * z);
    }

    public float dot(final Vector3D v)  {
        return x * v.x + y * v.y + z * v.z;
    }

    public float dot(final Normal v)  {
        return x * v.x + y * v.y + z * v.z;
    }

    public Vector3D cross(Vector3D v)  {
        return new Vector3D(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x);
    }

    public Vector3D normalize() {
        final float l = length();
        return new Vector3D(x / l, y / l, z / l);
    }

    public Vector3D negate() {
        return new Vector3D(-x, -y, -z);
    }

}
