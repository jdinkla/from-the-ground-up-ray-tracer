package net.dinkla.raytracer.math;

import net.dinkla.raytracer.objects.GeometricObject;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 16.05.2010
 * Time: 14:29:33
 * To change this template use File | Settings | File Templates.
 */
public class PointUtilities {

    public static Point3DF minPoints(List<Point3DF> points) {
        float x = Float.POSITIVE_INFINITY;
        float y = Float.POSITIVE_INFINITY;
        float z = Float.POSITIVE_INFINITY;
        for (Point3DF p : points) {
            if (p.x() < x) {
                x = p.x();
            }
            if (p.y() < y) {
                y = p.y();
            }
            if (p.z() < z) {
                z = p.z();
            }
        }
        final Point3DF p = new Point3DF(x, y, z);
        return p;
    }

    public static Point3DF maxPoints(List<Point3DF> points) {
        float x = Float.NEGATIVE_INFINITY;
        float y = Float.NEGATIVE_INFINITY;
        float z = Float.NEGATIVE_INFINITY;
        for (Point3DF p : points) {
            if (p.x() > x) {
                x = p.x();
            }
            if (p.y() > y) {
                y = p.y();
            }
            if (p.z() > z) {
                z = p.z();
            }
        }
        final Point3DF p = new Point3DF(x, y, z);
        return p;
    }

    public static Point3DF minCoordinates(List<GeometricObject> objects) {
        float x = Float.POSITIVE_INFINITY;
        float y = Float.POSITIVE_INFINITY;
        float z = Float.POSITIVE_INFINITY;
        for (GeometricObject object : objects) {
            BBox bbox = object.getBoundingBox();
            if (bbox.p.x() < x) {
                x = bbox.p.x();
            }
            if (bbox.p.y() < y) {
                y = bbox.p.y();
            }
            if (bbox.p.z() < z) {
                z = bbox.p.z();
            }
        }
        final Point3DF p = new Point3DF(x - MathUtils.K_EPSILON, y - MathUtils.K_EPSILON, z - MathUtils.K_EPSILON);
        return p;
    }

    public static Point3DF maxCoordinates(List<GeometricObject> objects) {
        float x = Float.NEGATIVE_INFINITY;
        float y = Float.NEGATIVE_INFINITY;
        float z = Float.NEGATIVE_INFINITY;
        for (GeometricObject object : objects) {
            BBox bbox = object.getBoundingBox();
            if (bbox.q.x() > x) {
                x = bbox.q.x();
            }
            if (bbox.q.y() > y) {
                y = bbox.q.y();
            }
            if (bbox.q.z() > z) {
                z = bbox.q.z();
            }
        }
        final Point3DF p = new Point3DF(x + MathUtils.K_EPSILON, y + MathUtils.K_EPSILON, z + MathUtils.K_EPSILON);
        return p;
    }
    
}
