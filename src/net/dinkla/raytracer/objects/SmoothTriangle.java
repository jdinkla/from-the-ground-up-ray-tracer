package net.dinkla.raytracer.objects;

import net.dinkla.raytracer.hits.Hit;
import net.dinkla.raytracer.hits.ShadowHit;
import net.dinkla.raytracer.math.*;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 28.04.2010
 * Time: 20:48:29
 * To change this template use File | Settings | File Templates.
 */
public class SmoothTriangle extends GeometricObject {

    public final Point3D v0;
    public final Point3D v1;
    public final Point3D v2;

    public Normal n0;
    public Normal n1;
    public Normal n2;
    
    public SmoothTriangle(final Point3D v0, final Point3D v1, final Point3D v2) {
        this.v0 = v0;
        this.v1 = v1;
        this.v2 = v2;
        n0 = Normal.Companion.getUP();
        n1 = Normal.Companion.getUP();
        n2 = Normal.Companion.getUP();
    }

    @Override
    public boolean hit(final Ray ray, Hit sr) {
        float a = v0.getX() - v1.getX();
        float b = v0.getX() - v2.getX();
        float c = ray.getD().getX();
        float d = v0.getX() - ray.getO().getX();

        float e = v0.getY() - v1.getY();
        float f = v0.getY() - v2.getY();
        float g = ray.getD().getY();
        float h = v0.getY() - ray.getO().getY();

        float i = v0.getZ() - v1.getZ();
        float j = v0.getZ() - v2.getZ();
        float k = ray.getD().getZ();
        float l = v0.getZ() - ray.getO().getZ();

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
        sr.setNormal(interpolateNormal(beta, gamma));
        //sr.localHitPoint = ray.linear(t);

        return true;
    }

    protected Normal interpolateNormal(final float beta, final float gamma) {
        Vector3D v1 = n0.mult(1 - beta - gamma);
        Vector3D v2 = n1.mult(beta);
        Vector3D v3 = n2.mult(gamma);
        Normal normal = new Normal(v1.plus(v2).plus(v3));
        return normal.normalize();
    }

    @Override
    public boolean shadowHit(Ray ray, ShadowHit tmin) {
        float a = v0.getX() - v1.getX();
        float b = v0.getX() - v2.getX();
        float c = ray.getD().getX();
        float d = v0.getX() - ray.getO().getX();

        float e = v0.getY() - v1.getY();
        float f = v0.getY() - v2.getY();
        float g = ray.getD().getY();
        float h = v0.getY() - ray.getO().getY();

        float i = v0.getZ() - v1.getZ();
        float j = v0.getZ() - v2.getZ();
        float k = ray.getD().getZ();
        float l = v0.getZ() - ray.getO().getZ();

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
        return BBox.Companion.create(v0, v1, v2);
    }

    public Normal getN0() {
        return n0;
    }

    public void setN0(Normal n0) {
        this.n0 = n0;
    }

    public Normal getN1() {
        return n1;
    }

    public void setN1(Normal n1) {
        this.n1 = n1;
    }

    public Normal getN2() {
        return n2;
    }

    public void setN2(Normal n2) {
        this.n2 = n2;
    }

}
