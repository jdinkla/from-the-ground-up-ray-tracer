package net.dinkla.raytracer.objects;

import net.dinkla.raytracer.hits.Hit;
import net.dinkla.raytracer.math.Normal;
import net.dinkla.raytracer.math.Point3D;
import net.dinkla.raytracer.math.Ray;
import net.dinkla.raytracer.math.Vector3D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 10.04.2010
 * Time: 21:12:03
 * To change this template use File | Settings | File Templates.
 */
public class PlaneTest {

    // Plane
    Point3D p;
    Normal normal;
    Plane plane;

    // Ray
    Point3D o;
    Vector3D d;
    Ray ray;
    
    // Sample
    Hit sr;

    @BeforeEach
    public void init() {
        sr = new Hit();
    }

    @Test
    public void construct() {
        new Plane(Point3D.Companion.getORIGIN(), Normal.Companion.getDOWN());
    }

    // q=0 plane, point below, vector up, hit
    @Test
    public void hit0() {
        p = new Point3D(0, 0, 0);
        normal = new Normal(0, 1, 0);
        plane = new Plane(p, normal);

        o = new Point3D(-1,-1,-1);
        d = new Vector3D(0,1,0);
        ray = new Ray(o, d);

        boolean isHit = plane.hit(ray, sr);
        assert isHit;
        assertEquals(sr.getT(), 1.0);
        assertEquals(sr.getNormal(), normal);
    }

    // q=0 plane, point above, vector up, no hit
    @Test
    public void hit1() {
        p = new Point3D(0, 0, 0);
        normal = new Normal(0, 1, 0);
        plane = new Plane(p, normal);

        // point above, vector up
        o = new Point3D(-1,1,-1);
        d = new Vector3D(0,1,0);
        ray = new Ray(o, d);

        boolean isHit = plane.hit(ray, sr);
        assert !isHit;
        assertNull(sr.getNormal());  // TODO NULL and normals
    }

    // q=0 plane upside down, point below, vector up, hit
    @Test
    public void hit2() {
        p = new Point3D(0, 0, 0);
        normal = new Normal(0, -1, 0);
        plane = new Plane(p, normal);

        o = new Point3D(-1, -1, -1);
        d = new Vector3D(0, 1, 0);
        ray = new Ray(o, d);

        boolean isHit = plane.hit(ray, sr);
        assert isHit;
        assertEquals(sr.getT(), 1.0);
        assertEquals(sr.getNormal(), normal);
    }

    // q=0 plane, point above, vector down, hit
    @Test
    public void hit3() {
        p = new Point3D(0, 0, 0);
        normal = new Normal(0, 1, 0);
        plane = new Plane(p, normal);

        o = new Point3D(1, 2, 1);
        d = new Vector3D(0, -1, 0);
        ray = new Ray(o, d);

        boolean isHit = plane.hit(ray, sr);
        assert isHit;
        assertEquals(sr.getT(), 2.0);
        assertEquals(sr.getNormal(), normal);
    }

    @Test
    public void hit4() {
        p = new Point3D(0, 0, 0);
        normal = new Normal(1, 1, 0);
        plane = new Plane(p, normal);

        o = new Point3D(-2, -2, 0);
        d = new Vector3D(1, 1, 0);
        ray = new Ray(o, d);

        boolean isHit = plane.hit(ray, sr);
        assert isHit;
        assertEquals(sr.getT(), 2.0);
        assertEquals(sr.getNormal(), normal);
    }

    @Test
    public void hit5() {
        p = new Point3D(0.1234f, 0, 0);
        normal = new Normal(1, 0, 0);
        plane = new Plane(p, normal);

        o = new Point3D(0, 4, 3);
        d = new Vector3D(-1, 0, 0);
        ray = new Ray(o, d);

        boolean isHit = plane.hit(ray, sr);
        assert !isHit;
    }

}
