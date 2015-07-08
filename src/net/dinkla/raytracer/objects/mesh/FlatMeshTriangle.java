package net.dinkla.raytracer.objects.mesh;

import net.dinkla.raytracer.hits.Hit;
import net.dinkla.raytracer.math.MathUtils;
import net.dinkla.raytracer.math.Point3D;
import net.dinkla.raytracer.math.Ray;
import net.dinkla.raytracer.objects.mesh.Mesh;
import net.dinkla.raytracer.objects.mesh.MeshTriangle;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 16.05.2010
 * Time: 10:03:16
 * To change this template use File | Settings | File Templates.
 */
public class FlatMeshTriangle extends MeshTriangle {

    public FlatMeshTriangle(final Mesh mesh) {
        super(mesh);
    }

    public FlatMeshTriangle(final Mesh mesh, final int i0, final int i1, final int i2) {
        super(mesh, i0, i1, i2);
    }

    @Override
    public boolean hit(Ray ray, Hit sr) {
        Point3D v0 = mesh.vertices.get(index0);
        Point3D v1 = mesh.vertices.get(index1);
        Point3D v2 = mesh.vertices.get(index2);

        float a = v0.x - v1.x, b = v0.x - v2.x, c = ray.d.x, d = v0.x - ray.o.x;
        float e = v0.y - v1.y, f = v0.y - v2.y, g = ray.d.y, h = v0.y - ray.o.y;
        float i = v0.z - v1.z, j = v0.z - v2.z, k = ray.d.z, l = v0.z - ray.o.z;

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
        sr.setNormal(normal);

        return true;
    }
    
}
