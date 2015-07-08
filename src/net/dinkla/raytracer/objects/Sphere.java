package net.dinkla.raytracer.objects;

import net.dinkla.raytracer.hits.Hit;
import net.dinkla.raytracer.hits.ShadowHit;
import net.dinkla.raytracer.math.*;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 10.04.2010
 * Time: 16:05:01
 * To change this template use File | Settings | File Templates.
 */
public class Sphere extends GeometricObject {

    public Point3D center;

    public float radius;

    protected BBox bbox;

    public Sphere(float radius) {
        this.center = Point3D.ORIGIN;
        this.radius = radius;
        bbox = null;
    }

    public Sphere(Point3D center, float radius) {
        this.center = center;
        this.radius = radius;
        bbox = null;
    }

    @Override
    public boolean hit(final Ray ray, Hit sr) {
        float t;
        Vector3D temp = ray.o.minus(center);
        float a = ray.d.dot(ray.d);
        float b = temp.mult(2).dot(ray.d);
        float c = temp.dot(temp) - radius * radius;
        float disc = b * b - 4 * a * c;

        if (disc < 0) {
            return false;
        } else {
            float e = (float) Math.sqrt(disc);
            float denom = 2 * a;
            t = (-b - e) / denom;
            if (t > MathUtils.K_EPSILON) {
                sr.setT(t);
                sr.setNormal(new Normal(ray.d.mult(t).plus(temp).mult(1.0f/radius)));
                return true;
            }
            t = (-b + e) / denom;
            if (t > MathUtils.K_EPSILON) {
                sr.setT(t);
                sr.setNormal(new Normal(ray.d.mult(t).plus(temp).mult(1.0f/radius)));
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean shadowHit(Ray ray, ShadowHit tmin) {
        float t;
        Vector3D temp = ray.o.minus(center);
        float a = ray.d.dot(ray.d);
        float b = temp.mult(2).dot(ray.d);
        float c = temp.dot(temp) - radius * radius;
        float disc = b * b - 4 * a * c;

        if (disc < 0) {
            return false;
        } else {
            float e = (float) Math.sqrt(disc);
            float denom = 2 * a;
            t = (-b - e) / denom;
            if (t > MathUtils.K_EPSILON) {
                tmin.setT(t);
                return true;
            }
            t = (-b + e) / denom;
            if (t > MathUtils.K_EPSILON) {
                tmin.setT(t);
                return true;
            }
        }
        return false;
    }

    @Override
    public BBox getBoundingBox() {
        if (null == bbox) {
            bbox = new BBox(center.minus(radius), center.plus(radius));
        }
        return bbox;
        //return new BBox(center.minus(radius), center.plus(radius));
//        return new BBox(center.minus(radius + MathUtils.K_EPSILON), center.plus(radius + MathUtils.K_EPSILON));
    }

    @Override
    public String toString() {
        return "Sphere(" + center.toString() + ", " + radius + ")";
    }

}
