package net.dinkla.raytracer.math;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 11.06.2010
 * Time: 19:25:07
 * To change this template use File | Settings | File Templates.
 */
public enum Axis {

    X(0), Y(1), Z(2);

    final int asInt;

    Axis(final int i) {
        asInt = i;
    }

    public static Axis fromInt(final int i) {
        switch (i) {
            case 0:
                return X;
            case 1:
                return Y;
            case 2:
                return Z;
        }
        return null;
    }

    public Axis next() {
        switch (this) {
            case X:
                return Y;
            case Y:
                return Z;
            case Z:
            default:
                return X;
        }
    }

}
