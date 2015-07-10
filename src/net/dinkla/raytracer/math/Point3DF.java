//package net.dinkla.raytracer.math;
//
//public class Point3DF extends Element3D {
//
//    private float x;
//    private float y;
//    private float z;
//
//    static public final Point3DF ORIGIN = new Point3DF(0, 0, 0);
//    static public final Point3DF MAX = new Point3DF(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
//    static public final Point3DF MIN = new Point3DF(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
//    static public final Point3DF DEFAULT_CAMERA = new Point3DF(0, 10, 10);
//
//    public Point3DF(final float x, final float y, final float z) {
//        super(x, y, z);
//    }
//
//    public Point3DF(final Element3D e) {
//        super(e);
//    }
//
//    public Vector3DF plus(final Point3DF v) {
//        return new Vector3DF(getX() + v.getX(), getY() + v.getY(), getZ() + v.getZ());
//    }
//
//    public Point3DF plus(final Vector3DF v) {
//        return new Point3DF(getX() + v.x, getY() + v.y, getZ() + v.z);
//    }
//
//    public Point3DF plus(final float f) {
//        return new Point3DF(getX() + f, getY() + f, getZ() + f);
//    }
//
//    public Vector3DF minus(final Point3DF v) {
//        return new Vector3DF(getX() - v.getX(), getY() - v.getY(), getZ() - v.getZ());
//    }
//
//    public Point3DF minus(final Vector3DF v) {
//        return new Point3DF(getX() - v.x, getY() - v.y, getZ() - v.z);
//    }
//
//    public Point3DF minus(final float f) {
//        return new Point3DF(getX() - f, getY() - f, getZ() - f);
//    }
//
//    public float getX() {
//        return x;
//    }
//
//    public void setX(float x) {
//        this.x = x;
//    }
//
//    public float getY() {
//        return y;
//    }
//
//    public void setY(float y) {
//        this.y = y;
//    }
//
//    public float getZ() {
//        return z;
//    }
//
//    public void setZ(float z) {
//        this.z = z;
//    }
//}
