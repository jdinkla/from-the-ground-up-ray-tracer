package net.dinkla.raytracer.math;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 10.04.2010
 * Time: 15:17:35
 * To change this template use File | Settings | File Templates.
 */
public class Vector3DF extends Element3D {

    public static final Vector3DF ZERO = new Vector3DF(0, 0, 0);
    static public final Vector3DF UP = new Vector3DF(0, 1, 0);
    static public final Vector3DF DOWN = new Vector3DF(0, -1, 0);
    public static final Vector3DF JITTER = new Vector3DF(0.0072f, 1.0f, 0.0034f);
    public static final Vector3DF BACK = new Vector3DF(0, 0, -1);

    public Vector3DF(final float x, final float y, final float z) {
        super(x, y, z);
    }

    public Vector3DF(final Element3D e) {
        super(e);
    }

    public Vector3DF plus(final Vector3DF v) {
        return new Vector3DF(x + v.x, y + v.y, z + v.z);
    }

    public Vector3DF minus(final Vector3DF v) {
        return new Vector3DF(x - v.x, y - v.y, z - v.z);
    }

    public Vector3DF mult(final float s) {
        return new Vector3DF(s * x, s * y, s * z);
    }

    public float dot(final Vector3DF v)  {
        return x * v.x + y * v.y + z * v.z;
    }

    public float dot(final Normal v)  {
        return x * v.x + y * v.y + z * v.z;
    }

    public Vector3DF cross(Vector3DF v)  {
        return new Vector3DF(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x);
    }

    public Vector3DF normalize() {
        final float l = length();
        return new Vector3DF(x / l, y / l, z / l);
    }

    public Vector3DF negate() {
        return new Vector3DF(-x, -y, -z);
    }

}
