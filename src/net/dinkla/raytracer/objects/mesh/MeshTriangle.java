package net.dinkla.raytracer.objects.mesh;

import net.dinkla.raytracer.hits.Hit;
import net.dinkla.raytracer.hits.ShadowHit;
import net.dinkla.raytracer.math.*;
import net.dinkla.raytracer.objects.GeometricObject;

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
    //float area;
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
        Point3DF p0 = mesh.vertices.get(index0);
        Point3DF p1 = mesh.vertices.get(index1);
        Point3DF p2 = mesh.vertices.get(index2);

        float a = p0.x() - p1.x();
        float b = p0.x() - p2.x();
        float c = ray.getD().x();
        float d = p0.x() - ray.getO().x();
        float e = p0.y() - p1.y();
        float f = p0.y() - p2.y();
        float g = ray.getD().y();
        float h = p0.y() - ray.getO().y();
        float i = p0.z() - p1.z();
        float j = p0.z() - p2.z();
        float k = ray.getD().z();
        float l = p0.z() - ray.getO().z();

        float m = f * k - g * j;
        float n = h * k - g * l;
        float p = f * l - h * j;
        float q = g * i - e * k;
        float s = e * j - f * i;

        float invDenom  = 1.0f / (a * m + b * q + c * s);

        float e1 = d * m - b * n - c * p;
        float beta = e1 * invDenom;

        if (beta < 0.0f) {
             return false;
        }

        float r = e * l - h * i;
        float e2 = a * n + d * q + c * r;
        float gamma = e2 * invDenom;

        if (gamma < 0.0 ) {
            return false;
        }
        if (beta + gamma > 1.0) {
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


    public void computeNormal(final boolean reverseNormal) {
        Point3DF p0 = mesh.vertices.get(index0);
        Point3DF p1 = mesh.vertices.get(index1);
        Point3DF p2 = mesh.vertices.get(index2);
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
            Point3DF p0 = mesh.vertices.get(index0);
            Point3DF p1 = mesh.vertices.get(index1);
            Point3DF p2 = mesh.vertices.get(index2);

            Point3DF min = MathUtils.minMin(p0, p1, p2).minus(MathUtils.K_EPSILON);
            Point3DF max = MathUtils.maxMax(p0, p1, p2).plus(MathUtils.K_EPSILON);
            bbox = new BBox(min, max);
        }
        return bbox;
    }

    /*
    float interpolateU(final float beta, final float gamma) {
        return ((1 - beta - gamma) * mesh.us.get(index0)
                + beta * mesh.us.get(index1)
                + gamma * mesh.us.get(index2));
    }

    float interpolateV(final float beta, final float gamma) {
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
