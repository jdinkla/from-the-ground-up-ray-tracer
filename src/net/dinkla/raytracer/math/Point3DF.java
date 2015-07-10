package net.dinkla.raytracer.math;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 10.04.2010
 * Time: 15:17:43
 * To change this template use File | Settings | File Templates.
 */
public class Point3DF extends Element3D {

    static public final Point3DF ORIGIN = new Point3DF(0, 0, 0);
    static public final Point3DF MAX = new Point3DF(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
    static public final Point3DF MIN = new Point3DF(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
    static public final Point3DF DEFAULT_CAMERA = new Point3DF(0, 10, 10);

    public Point3DF(final float x, final float y, final float z) {
        super(x, y, z);
    }

    public Point3DF(final Element3D e) {
        super(e);
    }

    public Vector3DF plus(final Point3DF v) {
        return new Vector3DF(x + v.x, y + v.y, z + v.z);
    }

    public Point3DF plus(final Vector3DF v) {
        return new Point3DF(x + v.x, y + v.y, z + v.z);
    }

    public Point3DF plus(final float f) {
        return new Point3DF(x + f, y + f, z + f);
    }

    public Vector3DF minus(final Point3DF v) {
        return new Vector3DF(x - v.x, y - v.y, z - v.z);
    }

    public Point3DF minus(final Vector3DF v) {
        return new Point3DF(x - v.x, y - v.y, z - v.z);
    }

    public Point3DF minus(final float f) {
        return new Point3DF(x - f, y - f, z - f);
    }

}
