package net.dinkla.raytracer.math;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 10.04.2010
 * Time: 15:17:52
 * To change this template use File | Settings | File Templates.
 */
public class Normal extends Element3D {

    public static final Normal RIGHT = new Normal(1, 0, 0);
    public static final Normal LEFT = new Normal(-1, 0, 0);
    public static final Normal UP = new Normal(0, 1, 0);
    public static final Normal DOWN = new Normal(0, -1, 0);
    public static final Normal FRONT = new Normal(0, 0, 1);
    public static final Normal BACK = new Normal(0, 0, -1);
    public static final Normal ZERO = new Normal(0, 0, 0);

    public Normal(float x, float y, float z) {
        super(x, y, z);
    }

    public Normal(final Vector3D v) {
        super(v.normalize());
    }

    public Normal(final Point3D p0, final Point3D p1, final Point3D p2) {
        super(p1.minus(p0).cross(p2.minus(p0)).normalize());
    }

    public Normal normalize() {
        float len = length();
        return new Normal(x / len, y / len, z / len);
    }

    public Vector3D mult(final float s) {
        return new Vector3D(s * x, s * y, s * z);
    }

    public Vector3D plus(final Normal normal) {
        return new Vector3D(x + normal.x, y + normal.y, z + normal.z);
    }

    public float dot(final Vector3D v)  {
        return x * v.x + y * v.y + z * v.z;
    }

    public Normal negate() {
        return new Normal(-x, -y, -z);
    }
}
