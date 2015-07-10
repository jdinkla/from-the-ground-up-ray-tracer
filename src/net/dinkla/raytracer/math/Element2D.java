//package net.dinkla.raytracer.math;
//
//import static java.lang.Math.sqrt;
//
///**
// * Created by IntelliJ IDEA.
// * User: jorndinkla
// * Date: 10.04.2010
// * Time: 16:23:15
// * To change this template use File | Settings | File Templates.
// */
//public class Element2D {
//
//    public final float x;
//    public final float y;
//
//    public Element2D(final float x, final float y) {
//        this.x = x;
//        this.y = y;
//    }
//
//    public Element2D(final Element2D e) {
//        this.x = e.x;
//        this.y = e.y;
//    }
//
//    public float sqrLength() {
//        return x*x + y*y;
//    }
//
//    public float length() {
//        return (float) sqrt(sqrLength());
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if (!(obj instanceof Element2D)) {
//            return false;
//        } else {
//            Element2D e = (Element2D) obj;
//            return ( x == e.x && y == e.y);
//        }
//    }
//
//    @Override
//    public String toString() {
//        return "(" + x + "," + y + ")";
//    }
//
//
//}