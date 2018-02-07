package net.dinkla.raytracer.math;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 26.04.2010
 * Time: 09:00:51
 * To change this template use File | Settings | File Templates.
 */
public class MathUtils {

    public static final double INV_PI = (1.0 / Math.PI);

    public static final double PI_ON_180 = (Math.PI / 180);

    public static final double K_HUGEVALUE = 1.0E10;

    static public final double K_EPSILON = 0.01;

    public static double clamp(double x, double low, double high) {
        return (x < low) ? low : ((x > high) ? high : x);
    }

    public static Point3D minMin(Point3D p, Point3D q, Point3D r) {
        double x = Math.min(Math.min(p.getX(), q.getX()), r.getX());
        double y = Math.min(Math.min(p.getY(), q.getY()), r.getY());
        double z = Math.min(Math.min(p.getZ(), q.getZ()), r.getZ());
        return new Point3D(x, y, z);
    }

    public static Point3D maxMax(Point3D p, Point3D q, Point3D r) {
        double x = Math.max(Math.max(p.getX(), q.getX()), r.getX());
        double y = Math.max(Math.max(p.getY(), q.getY()), r.getY());
        double z = Math.max(Math.max(p.getZ(), q.getZ()), r.getZ());
        return new Point3D(x, y, z);
    }

    public static boolean isZero(double r) {
        return r > -K_EPSILON && r < K_EPSILON;
    }

}
