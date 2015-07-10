package net.dinkla.raytracer.objects;

import net.dinkla.raytracer.hits.Hit;
import net.dinkla.raytracer.hits.ShadowHit;
import net.dinkla.raytracer.math.*;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 13.04.2010
 * Time: 13:23:24
 * To change this template use File | Settings | File Templates.
 */
public class OpenCylinder extends GeometricObject {

    float y0;
    float y1;
    float radius;
    float invRadius;

    public OpenCylinder(float y0, float y1, float radius) {
        this.y0 = Math.min(y0, y1);
        this.y1 = Math.max(y0, y1);
        this.radius = radius;
        this.invRadius = 1.0f / radius;
    }

    @Override
    public boolean hit(final Ray ray, Hit sr) {

        float t;
        float ox = ray.o.x;
        float oy = ray.o.y;
        float oz = ray.o.z;
        float dx = ray.d.x;
        float dy = ray.d.y;
        float dz = ray.d.z;

        float a = dx * dx + dz * dz;
        float b = 2.0f * (ox * dx + oz * dz);
        float c = ox * ox + oz * oz - radius * radius;
        float disc = b * b - 4.0f * a * c;

        if (disc < 0.0f) {
            return false;
        } else {
            float e = (float) Math.sqrt(disc);
            float denom = 2.0f * a;

            t = (-b - e) / denom;    // smaller root
            if (t > MathUtils.K_EPSILON) {
                float yhit = oy + t * dy;
                if (yhit > y0 && yhit < y1) {
                    sr.setT(t);
                    sr.setNormal(new Normal((ox + t * dx) * invRadius, 0.0f, (oz + t * dz) * invRadius));
                    // test for hitting from inside
                    if (ray.d.mult(-1).dot(sr.getNormal()) < 0.0) {
                        sr.setNormal(new Normal(sr.getNormal().mult(-1)));
                    }
                    //sr.localHitPoint = ray.linear(tmin.getValue());
                    return true;
                }
            }

            t = (-b + e) / denom;    // larger root
            if (t > MathUtils.K_EPSILON) {
                float yhit = oy + t * dy;
                if (yhit > y0 && yhit < y1) {
                    sr.setT(t);
                    sr.setNormal(new Normal((ox + t * dx) * invRadius, 0.0f, (oz + t * dz) * invRadius));
                    // test for hitting inside surface
                    if (ray.d.mult(-1).dot(sr.getNormal()) < 0.0) {
                        sr.setNormal(new Normal(sr.getNormal().mult(-1)));
                    }
                    //sr.localHitPoint = ray.linear(tmin.getValue());
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean shadowHit(final Ray ray, ShadowHit tmin) {
        float t;
        float ox = ray.o.x;
        float oy = ray.o.y;
        float oz = ray.o.z;
        float dx = ray.d.x;
        float dy = ray.d.y;
        float dz = ray.d.z;

        float a = dx * dx + dz * dz;
        float b = 2.0f * (ox * dx + oz * dz);
        float c = ox * ox + oz * oz - radius * radius;
        float disc = b * b - 4.0f * a * c;

        if (disc < 0.0f) {
            return false;
        } else {
            float e = (float) Math.sqrt(disc);
            float denom = 2.0f * a;

            t = (-b - e) / denom;    // smaller root
            if (t > MathUtils.K_EPSILON) {
                float yhit = oy + t * dy;
                if (yhit > y0 && yhit < y1) {
                    tmin.setT(t);
                    return true;
                }
            }

            t = (-b + e) / denom;    // larger root
            if (t > MathUtils.K_EPSILON) {
                float yhit = oy + t * dy;
                if (yhit > y0 && yhit < y1) {
                    tmin.setT(t);
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public BBox getBoundingBox() {
        // TODO: better bbox of open cylinder // throw new RuntimeException("OpenCylinder.getBoundingBox");
        Point3DF p = new Point3DF(-radius - MathUtils.K_EPSILON, y0, -radius - MathUtils.K_EPSILON);
        Point3DF q = new Point3DF(radius + MathUtils.K_EPSILON, y1, radius + MathUtils.K_EPSILON);
        return new BBox(p, q);
    }

}
