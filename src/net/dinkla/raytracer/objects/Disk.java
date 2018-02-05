package net.dinkla.raytracer.objects;

import net.dinkla.raytracer.hits.Hit;
import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.hits.ShadowHit;
import net.dinkla.raytracer.lights.ILightSource;
import net.dinkla.raytracer.math.*;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 12.04.2010
 * Time: 19:06:27
 * To change this template use File | Settings | File Templates.
 */
public class Disk extends GeometricObject {

    public Point3D center;
    public float radius;
    public Normal normal;

    public Disk(final Point3D center, final float radius, final Normal normal) {
        this.center = center;
        this.radius = radius;
        this.normal = normal;
    }

    @Override
    public boolean hit(final Ray ray, Hit sr) {
        float nom = center.minus(ray.getO()).dot(normal);
        float denom = ray.getD().dot(normal);
        float t = nom / denom;
        if (t <= MathUtils.K_EPSILON) {
            return false;
        }
        Point3D p = ray.linear(t);
        if (center.distanceSquared(p) < radius * radius)  {
            sr.setT(t);
            sr.setNormal(normal);
//            sr.localHitPoint = p;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean shadowHit(final Ray ray, ShadowHit tmin) {
        float nom = center.minus(ray.getO()).dot(normal);
        float denom = ray.getD().dot(normal);
        float t = nom / denom;
        if (t <= MathUtils.K_EPSILON) {
            return false;
        }
        Point3D p = ray.linear(t);
        if (center.distanceSquared(p) < radius * radius)  {
            tmin.setT(t);
            return true;
        } else {
            return false;
        }
    }

    public Normal getNormal(Point3D p) {
        return normal;
    }

    @Override
    public BBox getBoundingBox() {
        // TODO: more exact bounding box of a disk
        Point3D p = center.minus(radius);
        Point3D q = center.plus(radius);
        return new BBox(p, q);
    }

}
