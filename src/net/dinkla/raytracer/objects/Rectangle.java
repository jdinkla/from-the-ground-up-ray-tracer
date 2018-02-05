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
 * Time: 19:21:22
 * To change this template use File | Settings | File Templates.
 */
public class Rectangle extends GeometricObject {

    public final Point3D p0;
    public final Vector3D a;
    public final Vector3D b;
    public final Normal normal;

    public Rectangle(final Point3D p0, final Vector3D a, final Vector3D b) {
        this.p0 = p0;
        this.a = a;
        this.b = b;
        Vector3D v = a.cross(b);        
        normal = new Normal(v.normalize());
    }

    public Rectangle(final Point3D p0, final Vector3D a, final Vector3D b, final boolean inverted) {
        this.p0 = p0;
        this.a = a;
        this.b = b;
        Vector3D v;
        if (inverted) {
            v = b.cross(a);
        } else {
            v = a.cross(b);
        }
        normal = new Normal(v.normalize());
    }
    
    public Rectangle(final Point3D p0, final Vector3D a, final Vector3D b, final Normal normal) {
        this.p0 = p0;
        this.a = a;
        this.b = b;
        this.normal = normal;
    }

    @Override
    public boolean hit(final Ray ray, Hit sr) {
        final float nom = p0.minus(ray.getO()).dot(normal);
        final float denom = ray.getD().dot(normal);
        final float t = nom / denom;

        if (t <= MathUtils.K_EPSILON) {
            return false;
        }

        final Point3D p = ray.linear(t);
        final Vector3D d = p.minus(p0);

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
        final float nom = p0.minus(ray.getO()).dot(normal);
        final float denom = ray.getD().dot(normal);
        final float t = nom / denom;

        if (t <= MathUtils.K_EPSILON) {
            return false;
        }

        final Point3D p = ray.linear(t);
        final Vector3D d = p.minus(p0);

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

    public Normal getNormal(Point3D p) {
        return normal;  
    }

    @Override
    public BBox getBoundingBox() {
        Point3D v0 = p0;
        Point3D v1 = p0.plus(a).plus(b);

        float x0 = Float.POSITIVE_INFINITY;
        float x1 = Float.NEGATIVE_INFINITY;
        if (v0.getX() < x0) {
            x0 = v0.getX();
        }
        if (v1.getX() < x0) {
            x0 = v1.getX();
        }
        if (v0.getX() > x1) {
            x1 = v0.getX();
        }
        if (v1.getX() > x1) {
            x1 = v1.getX();
        }
        float y0 = Float.POSITIVE_INFINITY;
        float y1 = Float.NEGATIVE_INFINITY;
        if (v0.getY() < y0) {
            y0 = v0.getY();
        }
        if (v1.getY() < y0) {
            y0 = v1.getY();
        }
        if (v0.getY() > y1) {
            y1 = v0.getY();
        }
        if (v1.getY() > y1) {
            y1 = v1.getY();
        }
        float z0 = Float.POSITIVE_INFINITY;
        float z1 = Float.NEGATIVE_INFINITY;
        if (v0.getZ() < z0) {
            z0 = v0.getZ();
        }
        if (v1.getZ() < z0) {
            z0 = v1.getZ();
        }
        if (v0.getZ() > z1) {
            z1 = v0.getZ();
        }
        if (v1.getZ() > z1) {
            z1 = v1.getZ();
        }
        return new BBox(new Point3D(x0, y0, z0), new Point3D(x1, y1, z1));
    }
}
