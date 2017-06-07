package net.dinkla.raytracer.math;

import static java.lang.Math.sqrt;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 10.04.2010
 * Time: 16:23:15
 */
public class Element3D {

    public final float x;
    public final float y;
    public final float z;

    public Element3D(final float x, final float y, final float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Element3D(final Element3D e) {
        this.x = e.x;
        this.y = e.y;
        this.z = e.z;
    }

    public float sqrLength() {
        return x*x + y*y + z*z;
    }

    public float length() {
        return (float) sqrt(sqrLength());
    }

    public float distanceSquared(final Element3D p) {
        final float dx = x - p.x;
        final float dy = y - p.y;
        final float dz = z - p.z;
        return (dx * dx + dy * dy + dz * dz);
    }

    public float ith(final Axis axis) {
        switch(axis) {
            case X:
                return x;
            case Y:
                return y;
            case Z:
            default:
                return z;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (null != obj) {
            if (obj instanceof Element3D) {
                Element3D e = (Element3D) obj;
                return ( x == e.x && y == e.y && z == e.z);
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + "," + z + ")";
    }

}
