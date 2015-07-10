package net.dinkla.raytracer.objects;

import net.dinkla.raytracer.hits.Hit;
import net.dinkla.raytracer.hits.ShadowHit;
import net.dinkla.raytracer.math.*;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 12.04.2010
 * Time: 19:21:22
 * To change this template use File | Settings | File Templates.
 */
public class Rectangle extends GeometricObject {

    public final Point3DF p0;
    public final Vector3DF a;
    public final Vector3DF b;
    public final Normal normal;

    public Rectangle(final Point3DF p0, final Vector3DF a, final Vector3DF b) {
        this.p0 = p0;
        this.a = a;
        this.b = b;
        Vector3DF v = a.cross(b);
        normal = new Normal(v.normalize());
    }

    public Rectangle(final Point3DF p0, final Vector3DF a, final Vector3DF b, final boolean inverted) {
        this.p0 = p0;
        this.a = a;
        this.b = b;
        Vector3DF v;
        if (inverted) {
            v = b.cross(a);
        } else {
            v = a.cross(b);
        }
        normal = new Normal(v.normalize());
    }
    
    public Rectangle(final Point3DF p0, final Vector3DF a, final Vector3DF b, final Normal normal) {
        this.p0 = p0;
        this.a = a;
        this.b = b;
        this.normal = normal;
    }

    @Override
    public boolean hit(final Ray ray, Hit sr) {
        final float nom = p0.minus(ray.o).dot(normal);
        final float denom = ray.d.dot(normal);
        final float t = nom / denom;

        if (t <= MathUtils.K_EPSILON) {
            return false;
        }

        final Point3DF p = ray.linear(t);
        final Vector3DF d = p.minus(p0);

        final float ddota = d.dot(a);
        if (ddota < 0 || ddota > a.sqrLength()) {
            return false;
        }

        final float ddotb = d.dot(b);
        if (ddotb < 0 || ddotb > b.sqrLength()) {
            return false;
        }
        
        sr.setT(t);
        sr.setNormal(normal);

        return true;
    }

    @Override
    public boolean shadowHit(final Ray ray, ShadowHit tmin) {
        final float nom = p0.minus(ray.o).dot(normal);
        final float denom = ray.d.dot(normal);
        final float t = nom / denom;

        if (t <= MathUtils.K_EPSILON) {
            return false;
        }

        final Point3DF p = ray.linear(t);
        final Vector3DF d = p.minus(p0);

        final float ddota = d.dot(a);
        if (ddota < 0 || ddota > a.sqrLength()) {
            return false;
        }

        final float ddotb = d.dot(b);
        if (ddotb < 0 || ddotb > b.sqrLength()) {
            return false;
        }

        tmin.setT(t);
        return true;
    }

    public Normal getNormal(Point3DF p) {
        return normal;  
    }

    @Override
    public BBox getBoundingBox() {
        Point3DF v0 = p0;
        Point3DF v1 = p0.plus(a).plus(b);

        float x0 = Float.POSITIVE_INFINITY;
        float x1 = Float.NEGATIVE_INFINITY;
        if (v0.x < x0) {
            x0 = v0.x;
        }
        if (v1.x < x0) {
            x0 = v1.x;
        }
        if (v0.x > x1) {
            x1 = v0.x;
        }
        if (v1.x > x1) {
            x1 = v1.x;
        }
        float y0 = Float.POSITIVE_INFINITY;
        float y1 = Float.NEGATIVE_INFINITY;
        if (v0.y < y0) {
            y0 = v0.y;
        }
        if (v1.y < y0) {
            y0 = v1.y;
        }
        if (v0.y > y1) {
            y1 = v0.y;
        }
        if (v1.y > y1) {
            y1 = v1.y;
        }
        float z0 = Float.POSITIVE_INFINITY;
        float z1 = Float.NEGATIVE_INFINITY;
        if (v0.z < z0) {
            z0 = v0.z;
        }
        if (v1.z < z0) {
            z0 = v1.z;
        }
        if (v0.z > z1) {
            z1 = v0.z;
        }
        if (v1.z > z1) {
            z1 = v1.z;
        }
        return new BBox(new Point3DF(x0, y0, z0), new Point3DF(x1, y1, z1));
    }
}
