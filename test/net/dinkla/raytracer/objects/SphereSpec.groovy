package net.dinkla.raytracer.objects

import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.MathUtils
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import spock.lang.Specification

class SphereSpec extends Specification {

    def "boundingBox"() {
        final def point = new Point3D(0, 0, 0)
        final def radius = 1.0
        final def sphere = new Sphere(point, radius)
        final BBox bbox = sphere.getBoundingBox()

        expect: bbox.getP() ==  new Point3D(-1, -1, -1)
        and: bbox.getQ() == new Point3D(1, 1, 1)
    }

    def "hit"() {
        final def sr = new Shade()
        final def point = new Point3D(0, 0, 0)
        final def radius = 1.0
        final def sphere = new Sphere(point, radius)
        final def o = new Point3D(0, 0, -2)
        final def d = new Vector3D(0, 0, 1)
        final def ray = new Ray(o, d)
        final boolean isHit = sphere.hit(ray, sr);

        expect: isHit
        and: Math.abs(sr.getT() - 1.0) < MathUtils.INSTANCE.getK_EPSILON()
        and: sr.getNormal() == new Normal(0, 0, -1)
    }

}
