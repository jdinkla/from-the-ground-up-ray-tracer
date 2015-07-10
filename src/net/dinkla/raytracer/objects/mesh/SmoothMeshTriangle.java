package net.dinkla.raytracer.objects.mesh;

import net.dinkla.raytracer.hits.Hit;
import net.dinkla.raytracer.math.*;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 16.05.2010
 * Time: 10:04:22
 * To change this template use File | Settings | File Templates.
 */
public class SmoothMeshTriangle extends MeshTriangle {

    public SmoothMeshTriangle(final Mesh mesh) {
        super(mesh);
    }

    public SmoothMeshTriangle(final Mesh mesh, final int i0, final int i1, final int i2) {
        super(mesh, i0, i1, i2);
    }

    @Override
    public boolean hit(Ray ray, Hit sr) {
        Point3DF v0 = mesh.vertices.get(index0);
        Point3DF v1 = mesh.vertices.get(index1);
        Point3DF v2 = mesh.vertices.get(index2);

        float a = v0.x() - v1.x(), b = v0.x() - v2.x(), c = ray.getD().x(), d = v0.x() - ray.getO().x();
        float e = v0.y() - v1.y(), f = v0.y() - v2.y(), g = ray.getD().y(), h = v0.y() - ray.getO().y();
        float i = v0.z() - v1.z(), j = v0.z() - v2.z(), k = ray.getD().z(), l = v0.z() - ray.getO().z();

        float m = f * k - g * j, n = h * k - g * l, p = f * l - h * j;
        float q = g * i - e * k, s = e * j - f * i;

        float invDenom = 1.0f / (a * m + b * q + c * s);

        float e1 = d * m - b * n - c * p;
        float beta = e1 * invDenom;

        if (beta < 0.0)
            return (false);

        float r = e * l - h * i;
        float e2 = a * n + d * q + c * r;
        float gamma = e2 * invDenom;

        if (gamma < 0.0)
            return (false);

        if (beta + gamma > 1.0)
            return (false);

        float e3 = a * p - b * r + d * s;
        float t = e3 * invDenom;

        if (t < MathUtils.K_EPSILON)
            return (false);

        sr.setT(t);
        sr.setNormal(interpolateNormal(beta, gamma)); // for smooth shading
        //sr.localHitPoint = ray.linear(t);

        return (true);
    }

    protected Normal interpolateNormal(final float beta, final float gamma) {
        Vector3DF v1 = mesh.normals.get(index0).mult(1 - beta - gamma);
        Vector3DF v2 = mesh.normals.get(index1).mult(beta);
        Vector3DF v3 = mesh.normals.get(index2).mult(gamma);
        Normal normal = new Normal(v1.plus(v2).plus(v3));
        return normal.normalize();
    }

}
