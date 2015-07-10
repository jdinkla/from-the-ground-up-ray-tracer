///*
//package net.dinkla.raytracer.math;
//
//import static java.lang.Math.sqrt;
//
//*/
///**
// * Created by IntelliJ IDEA.
// * User: jorndinkla
// * Date: 10.04.2010
// * Time: 15:17:52
// * To change this template use File | Settings | File Templates.
// */
//
//public class Normal extends Element3D {
//
//    public static final Normal RIGHT = new Normal(1, 0, 0);
//    public static final Normal LEFT = new Normal(-1, 0, 0);
//    public static final Normal UP = new Normal(0, 1, 0);
//    public static final Normal DOWN = new Normal(0, -1, 0);
//    public static final Normal FRONT = new Normal(0, 0, 1);
//    public static final Normal BACK = new Normal(0, 0, -1);
//    public static final Normal ZERO = new Normal(0, 0, 0);
//
//    public Normal(float x, float y, float z) {
//        super(x, y, z);
//    }
//
//    public Normal(final Vector3DF v) {
//        super(v.normalize());
//    }
//
//    public Normal(final Point3DF p0, final Point3DF p1, final Point3DF p2) {
//        super(p1.minus(p0).cross(p2.minus(p0)).normalize());
//    }
//
//    public Normal normalize() {
//        float len = length();
//        return new Normal(x / len, y / len, z / len);
//    }
//
//    public Vector3DF mult(final float s) {
//        return new Vector3DF(s * x, s * y, s * z);
//    }
//
//    public Vector3DF plus(final Normal normal) {
//        return new Vector3DF(x + normal.x, y + normal.y, z + normal.z);
//    }
//
//    public float dot(final Vector3DF v)  {
//        return x * v.x + y * v.y + z * v.z;
//    }
//
//    public Normal negate() {
//        return new Normal(-x, -y, -z);
//    }
//
//    @Override
//    public Object x() {
//        return x;
//    }
//
//    @Override
//    public Object z() {
//        return z;
//    }
//
//    @Override
//    public Object length() {
//        return sqrt(sqrLength());
//    }
//
//    @Override
//    public Object y() {
//        return y;
//    }
//}
