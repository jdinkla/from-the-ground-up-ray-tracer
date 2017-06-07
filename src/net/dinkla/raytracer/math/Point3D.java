package net.dinkla.raytracer.math;

import static java.lang.Math.sqrt;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 10.04.2010
 * Time: 15:17:43
 * To change this template use File | Settings | File Templates.
 */
public class Point3D extends Element3D {

    static public final Point3D ORIGIN = new Point3D(0, 0, 0);
    static public final Point3D MAX = new Point3D(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
    static public final Point3D MIN = new Point3D(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
    static public final Point3D DEFAULT_CAMERA = new Point3D(0, 10, 10);

    public Point3D(final float x, final float y, final float z) {
        super(x, y, z);
    }

    public Point3D(final Element3D e) {
        super(e);
    }

    public Point3D plus(final Vector3D v) {
        return new Point3D(x + v.x, y + v.y, z + v.z);
    }

    public Point3D plus(final float f) {
        return new Point3D(x + f, y + f, z + f);
    }

    public Vector3D minus(final Point3D v) {
        return new Vector3D(x - v.x, y - v.y, z - v.z);
    }

    public Point3D minus(final Vector3D v) {
        return new Point3D(x - v.x, y - v.y, z - v.z);
    }

    public Point3D minus(final float f) {
        return new Point3D(x - f, y - f, z - f);
    }

}
