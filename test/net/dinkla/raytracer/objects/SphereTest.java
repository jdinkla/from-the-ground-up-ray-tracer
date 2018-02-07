package net.dinkla.raytracer.objects;

import net.dinkla.raytracer.hits.Hit;
import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.math.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 11.04.2010
 * Time: 17:39:19
 * To change this template use File | Settings | File Templates.
 */
public class SphereTest {

    // Sphere
    Point3D point;
    double radius;
    Sphere sphere;
    
    // Ray
    Point3D o;
    Vector3D d;
    Ray ray;

    // Sample
    Hit sr;

    @Before
    public void init() {
        sr = new Shade();
    }

    @Test
    public void hit0() {
        point = new Point3D(0, 0, 0);
        radius = 1.0;
        sphere = new Sphere(point, radius);

        o = new Point3D(0, 0, -2);
        d = new Vector3D(0, 0, 1);
        ray = new Ray(o, d);

        boolean isHit = sphere.hit(ray, sr);
        assert isHit;
        assertEquals(sr.getT(), 1.0, MathUtils.K_EPSILON);
//        assertEquals(sr.getLocalHitPoint(), new Point3D(0, 0, -1));
        assertEquals(sr.getNormal(), new Normal(0, 0, -1));

        /*
        isHit = sphere.hit(ray, tmin, sr);
        assert isHit;
        assertEquals(tmin.value, 2.0);
        assertEquals(sr.localHitPoint, new Point3D(0, 0, 1));
        */
    }

    // ray starts in the sphere
    @Test
    public void hit1() {
        point = new Point3D(0, 0, 0);
        radius = 1.0;
        sphere = new Sphere(point, radius);

        o = new Point3D(0, 0, 0);
        d = new Vector3D(0, 0, 1);
        ray = new Ray(o, d);

        boolean isHit = sphere.hit(ray, sr);
        assert isHit;
        assertEquals(sr.getT(), 1.0, MathUtils.K_EPSILON);
//        assertEquals(sr.getLocalHitPoint(), new Point3D(0, 0, 1));
        assertEquals(sr.getNormal(), new Normal(0, 0, 1));
    }

    @Test
    public void boundingBox() {
        point = new Point3D(0, 0, 0);
        radius = 1.0;
        sphere = new Sphere(point, radius);

        BBox bbox = sphere.getBoundingBox();
        assertEquals(bbox.getP(), new Point3D(-1, -1, -1));
        assertEquals(bbox.getQ(), new Point3D(1, 1, 1));
    }

}
