//package net.dinkla.raytracer.math;
//
///**
// * Created by IntelliJ IDEA.
// * User: jorndinkla
// * Date: 07.06.2010
// * Time: 19:44:08
// * To change this template use File | Settings | File Templates.
// */
//public class Basis {
//
//    public Vector3DF u, v, w;
//
//    public Basis(final Point3DF eye, final Point3DF lookAt, final Vector3DF up) {
//        w = eye.minus(lookAt).normalize();
//        u = up.cross(w).normalize();
//        v = w.cross(u);
//    }
//
//    public Vector3DF pm(final float x, final float y, final float z) {
//        return u.mult(x).plus(v.mult(y)).minus(w.mult(z));
//    }
//
//    public Vector3DF pp(final float x, final float y, final float z) {
//        return u.mult(x).plus(v.mult(y)).plus(w.mult(z));
//    }
//
//}
