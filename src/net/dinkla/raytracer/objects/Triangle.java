package net.dinkla.raytracer.objects;

import net.dinkla.raytracer.hits.Hit;
import net.dinkla.raytracer.hits.ShadowHit;
import net.dinkla.raytracer.math.*;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 10.04.2010
 * Time: 16:06:24
 * To change this template use File | Settings | File Templates.
 */
public class Triangle extends GeometricObject {

    public final Point3DF v0;
    public final Point3DF v1;
    public final Point3DF v2;
    public Normal normal;

    public Triangle(final Point3DF v0, final Point3DF v1, final Point3DF v2) {
        this.v0 = v0;
        this.v1 = v1;
        this.v2 = v2;
        normal = new Normal(v0, v1, v2);
    }
    
    @Override
    public boolean hit(Ray ray, Hit sr) {
        
        float a = v0.x - v1.x;
        float b = v0.x - v2.x;
        float c = ray.d.x;
        float d = v0.x - ray.o.x;

        float e = v0.y - v1.y;
        float f = v0.y - v2.y;
        float g = ray.d.y;
        float h = v0.y - ray.o.y;

        float i = v0.z - v1.z;
        float j = v0.z - v2.z;
        float k = ray.d.z;
        float l = v0.z - ray.o.z;

        float m = f * k - g * j;
        float n = h * k - g * l;
        float p = f * l - h * j;
        float q = g * i - e * k;
        float s = e * j - f * i;

        float invDenom = 1.0f / (a * m + b * q + c * s);

        float e1 = d * m - b * n - c * p;
        float beta = e1 * invDenom;

        if (beta < 0) {
            return false;
        }

        float r = e * l - h * i;
        float e2 = a * n + d * q + c * r;
        float gamma = e2 * invDenom;

        if (gamma < 0) {
            return false;
        }

        if (beta + gamma > 1) {
            return false;
        }

        float e3 = a * p - b * r + d * s;
        float t = e3 * invDenom;

        if (t < MathUtils.K_EPSILON) {
            return false;
        }
        sr.setT(t);
        sr.setNormal(normal);

        return true;
    }

    @Override
    public boolean shadowHit(Ray ray, ShadowHit tmin) {

        float a = v0.x - v1.x;
        float b = v0.x - v2.x;
        float c = ray.d.x;
        float d = v0.x - ray.o.x;

        float e = v0.y - v1.y;
        float f = v0.y - v2.y;
        float g = ray.d.y;
        float h = v0.y - ray.o.y;

        float i = v0.z - v1.z;
        float j = v0.z - v2.z;
        float k = ray.d.z;
        float l = v0.z - ray.o.z;

        float m = f * k - g * j;
        float n = h * k - g * l;
        float p = f * l - h * j;
        float q = g * i - e * k;
        float s = e * j - f * i;

        float invDenom = 1.0f / (a * m + b * q + c * s);

        float e1 = d * m - b * n - c * p;
        float beta = e1 * invDenom;

        if (beta < 0) {
            return false;
        }

        float r = e * l - h * i;
        float e2 = a * n + d * q + c * r;
        float gamma = e2 * invDenom;

        if (gamma < 0) {
            return false;
        }

        if (beta + gamma > 1) {
            return false;
        }

        float e3 = a * p - b * r + d * s;
        float t = e3 * invDenom;

        if (t < MathUtils.K_EPSILON) {
            return false;
        }
        tmin.setT(t);

        return true;
    }

    @Override
    public BBox getBoundingBox() {
        return BBox.create(v0, v1, v2);
    }
}
