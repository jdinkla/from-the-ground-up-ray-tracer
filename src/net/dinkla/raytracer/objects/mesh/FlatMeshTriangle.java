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

        double a = v0.getX() - v1.getX(), b = v0.getX() - v2.getX(), c = ray.getD().getX(), d = v0.getX() - ray.getO().getX();
        double e = v0.getY() - v1.getY(), f = v0.getY() - v2.getY(), g = ray.getD().getY(), h = v0.getY() - ray.getO().getY();
        double i = v0.getZ() - v1.getZ(), j = v0.getZ() - v2.getZ(), k = ray.getD().getZ(), l = v0.getZ() - ray.getO().getZ();

        double m = f * k - g * j, n = h * k - g * l, p = f * l - h * j;
        double q = g * i - e * k, s = e * j - f * i;

        double invDenom = 1.0 / (a * m + b * q + c * s);

        double e1 = d * m - b * n - c * p;
        double beta = e1 * invDenom;

        if (beta < 0.0)
            return (false);

        double r = e * l - h * i;
        double e2 = a * n + d * q + c * r;
        double gamma = e2 * invDenom;

        if (gamma < 0.0)
            return (false);

        if (beta + gamma > 1.0)
            return (false);

        double e3 = a * p - b * r + d * s;
        double t = e3 * invDenom;

        if (t < MathUtils.INSTANCE.getK_EPSILON())
            return (false);

        sr.setT(t);
        sr.setNormal(normal);

        return true;
    }
    
}
