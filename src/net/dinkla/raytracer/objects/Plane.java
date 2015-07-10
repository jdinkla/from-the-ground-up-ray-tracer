package net.dinkla.raytracer.objects;

import net.dinkla.raytracer.hits.Hit;
import net.dinkla.raytracer.hits.ShadowHit;
import net.dinkla.raytracer.math.*;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 10.04.2010
 * Time: 16:06:34
 * To change this template use File | Settings | File Templates.
 */
public class Plane extends GeometricObject {

    public Point3DF point;
    public Normal normal;

    public Plane() {
        this.point = Point3D$.MODULE$.ORIGIN();
        this.normal = Normal$.MODULE$.UP();
    }
    
    public Plane(Point3DF point, Normal normal) {
        this.point = point;
        this.normal = normal;
    }

    @Override
    public boolean hit(final Ray ray, Hit sr) {
        // (point - ray.o) * normal / (ray.d * normal)
        Vector3DF v = point.minus(ray.getO());
        float nom = v.dot(normal);
        float denom = ray.getD().dot(normal);

        float t = nom / denom;
        if (t  > MathUtils.K_EPSILON) {
            sr.setT(t);
            sr.setNormal(this.normal);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean shadowHit(final Ray ray, ShadowHit tmin) {
        Vector3DF v = point.minus(ray.getO());
        float nom = v.dot(normal);
        float denom = ray.getD().dot(normal);
        float t = nom / denom;
        if (t  > MathUtils.K_EPSILON) {
            tmin.setT(t);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public BBox getBoundingBox() {
        return new BBox(Point3D$.MODULE$.MIN(), Point3D$.MODULE$.MAX());
    }

    @Override
    public String toString() {
        return "Plane: " + super.toString() + " " + point.toString() + " " + normal.toString();
    }
}
