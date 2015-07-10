//package net.dinkla.raytracer.math;
//
///**
// * Created by IntelliJ IDEA.
// * User: jorndinkla
// * Date: 10.04.2010
// * Time: 15:17:35
// * To change this template use File | Settings | File Templates.
// */
//
//public class Vector3DF extends Element3D<Float> {
//
//    private float x;
//    private float y;
//    private float z;
//
//    public static final Vector3DF ZERO = new Vector3DF(0, 0, 0);
//    static public final Vector3DF UP = new Vector3DF(0, 1, 0);
//    static public final Vector3DF DOWN = new Vector3DF(0, -1, 0);
//    public static final Vector3DF JITTER = new Vector3DF(0.0072f, 1.0f, 0.0034f);
//    public static final Vector3DF BACK = new Vector3DF(0, 0, -1);
//
//    public Vector3DF(final float x, final float y, final float z) {
//        super(x, y, z);
//        //super(new Float(x), new Float(y), new Float(z));
//    }
//
//    public Vector3DF(final Element3D e) {
//        super(e);
//    }
//
//    public Vector3DF plus(final Vector3DF v) {
//        return new Vector3DF(getX() + v.getX(), getY() + v.getY(), getZ() + v.getZ());
//    }
//
//    public Vector3DF minus(final Vector3DF v) {
//        return new Vector3DF(getX() - v.getX(), getY() - v.getY(), getZ() - v.getZ());
//    }
//
//    public Vector3DF mult(final float s) {
//        return new Vector3DF(s * getX(), s * getY(), s * getZ());
//    }
//
//    public float dot(final Vector3DF v)  {
//        return getX() * v.getX() + getY() * v.getY() + getZ() * v.getZ();
//    }
//
//    public float dot(final Normal v)  {
//        return getX() * v.getX() + getY() * v.getY() + getZ() * v.getZ();
//    }
//
//    public Vector3DF cross(Vector3DF v)  {
//        return new Vector3DF(getY() * v.getZ() - getZ() * v.getY(), getZ() * v.getX() - getX() * v.getZ(), getX() * v.getY() - getY() * v.getX());
//    }
//
//    public Vector3DF normalize() {
//        final float l = length();
//        return new Vector3DF(getX() / l, getY() / l, getZ() / l);
//    }
//
//    public Vector3DF negate() {
//        return new Vector3DF(-getX(), -getY(), -getZ());
//    }
//
//    @Override
//    public Object x() {
//        return null;
//    }
//
//    @Override
//    public Object z() {
//        return null;
//    }
//
//    @Override
//    public Object length() {
//        return null;
//    }
//
//    @Override
//    public Object y() {
//        return null;
//    }
//
//    public float getX() {
//        return x;
//    }
//
//    public float getY() {
//        return y;
//    }
//
//    public float getZ() {
//        return z;
//    }
//
//    public void setX(float x) {
//        this.x = x;
//    }
//
//    public void setY(float y) {
//        this.y = y;
//    }
//
//    public void setZ(float z) {
//        this.z = z;
//    }
//
//}
