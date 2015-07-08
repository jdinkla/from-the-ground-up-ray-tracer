package net.dinkla.raytracer.math;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 26.04.2010
 * Time: 09:00:51
 * To change this template use File | Settings | File Templates.
 */
public class MathUtils {
    
    public static final float INV_PI = (float) (1.0f / Math.PI);

    public static final float PI_ON_180 = (float) (Math.PI / 180);

    public static final float K_HUGEVALUE = 1.0E10f;

    static public final float K_EPSILON = 0.01f;

    public static float clamp(float x, float low, float high) {
        return (x < low) ? low : ((x > high) ? high : x);
    }

    public static Point3D minMin(Point3D p, Point3D q, Point3D r) {
        float x = Math.min(Math.min(p.x, q.x), r.x);
        float y = Math.min(Math.min(p.y, q.y), r.y);
        float z = Math.min(Math.min(p.z, q.z), r.z);
        return new Point3D(x, y, z);
    }

    public static Point3D maxMax(Point3D p, Point3D q, Point3D r) {
        float x = Math.max(Math.max(p.x, q.x), r.x);
        float y = Math.max(Math.max(p.y, q.y), r.y);
        float z = Math.max(Math.max(p.z, q.z), r.z);
        return new Point3D(x, y, z);
    }

    public static boolean isZero(float r) {
        return r > -K_EPSILON && r < K_EPSILON;
    }

    public static boolean isZero(double r) {
        return r > -K_EPSILON && r < K_EPSILON;
    }

}
