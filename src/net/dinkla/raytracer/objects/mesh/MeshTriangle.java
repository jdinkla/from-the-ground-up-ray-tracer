package net.dinkla.raytracer.objects.mesh;

import net.dinkla.raytracer.hits.Hit;
import net.dinkla.raytracer.hits.ShadowHit;
import net.dinkla.raytracer.math.*;
import net.dinkla.raytracer.objects.GeometricObject;
import net.dinkla.raytracer.utilities.Counter;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 28.04.2010
 * Time: 20:12:03
 * To change this template use File | Settings | File Templates.
 */
public class MeshTriangle extends GeometricObject {

    Mesh mesh;
    int index0;
    int index1;
    int index2;
    Normal normal;
    //double area;
    BBox bbox;
    
    public MeshTriangle(final Mesh mesh) {
        this.mesh = mesh;
        index0 = 0;
        index1 = 0;
        index2 = 0;
        normal = null;
        //area = 0;
    }

    public MeshTriangle(final Mesh mesh, final int i0, final int i1, final int i2) {
        this.mesh = mesh;
        this.index0 = i0;
        this.index1 = i1;
        this.index2 = i2;
    }

    @Override
    public boolean hit(final Ray ray, Hit sr) {
        return false;
    }

    @Override
    public boolean shadowHit(final Ray ray, ShadowHit tmin) {
        Point3D p0 = mesh.vertices.get(index0);
        Point3D p1 = mesh.vertices.get(index1);
        Point3D p2 = mesh.vertices.get(index2);

        double a = p0.getX() - p1.getX();
        double b = p0.getX() - p2.getX();
        double c = ray.getD().getX();
        double d = p0.getX() - ray.getO().getX();
        double e = p0.getY() - p1.getY();
        double f = p0.getY() - p2.getY();
        double g = ray.getD().getY();
        double h = p0.getY() - ray.getO().getY();
        double i = p0.getZ() - p1.getZ();
        double j = p0.getZ() - p2.getZ();
        double k = ray.getD().getZ();
        double l = p0.getZ() - ray.getO().getZ();

        double m = f * k - g * j;
        double n = h * k - g * l;
        double p = f * l - h * j;
        double q = g * i - e * k;
        double s = e * j - f * i;

        double invDenom  = 1.0 / (a * m + b * q + c * s);

        double e1 = d * m - b * n - c * p;
        double beta = e1 * invDenom;

        if (beta < 0.0) {
             return false;
        }

        double r = e * l - h * i;
        double e2 = a * n + d * q + c * r;
        double gamma = e2 * invDenom;

        if (gamma < 0.0 ) {
            return false;
        }
        if (beta + gamma > 1.0) {
            return false;
        }
        double e3 = a * p - b * r + d * s;
        double t = e3 * invDenom;

        if (t < MathUtils.K_EPSILON) {
            return false;
        }
        tmin.setT(t);
        return true;
    }


    public void computeNormal(final boolean reverseNormal) {
        Point3D p0 = mesh.vertices.get(index0);
        Point3D p1 = mesh.vertices.get(index1);
        Point3D p2 = mesh.vertices.get(index2);
        //normal = new Normal(p0, p1, p2);
        normal = new Normal(p1.minus(p0).cross(p2.minus(p0)).normalize());
        if (reverseNormal) {
            normal = normal.negate();
        }
    }

    public Normal getNormal() {
        return normal;
    }

    public BBox getBoundingBox() {
        if (null == bbox) {
            Point3D p0 = mesh.vertices.get(index0);
            Point3D p1 = mesh.vertices.get(index1);
            Point3D p2 = mesh.vertices.get(index2);

            Point3D min = MathUtils.minMin(p0, p1, p2).minus(MathUtils.K_EPSILON);
            Point3D max = MathUtils.maxMax(p0, p1, p2).plus(MathUtils.K_EPSILON);
            bbox = new BBox(min, max);
        }
        return bbox;
    }

    /*
    double interpolateU(final double beta, final double gamma) {
        return ((1 - beta - gamma) * mesh.us.get(index0)
                + beta * mesh.us.get(index1)
                + gamma * mesh.us.get(index2));
    }

    double interpolateV(final double beta, final double gamma) {
        return ((1 - beta - gamma) * mesh.vs.get(index0)
                + beta * mesh.vs.get(index1)
                + gamma * mesh.vs.get(index2));
    }
    */

    public Mesh getMesh() {
        return mesh;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }
    
}
