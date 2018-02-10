package net.dinkla.raytracer.objects.parts

import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.ShadowHit
import net.dinkla.raytracer.math.*
import net.dinkla.raytracer.objects.GeometricObject

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 27.05.2010
 * Time: 23:30:10
 * To change this template use File | Settings | File Templates.
 */
class ConvexPartSphere : GeometricObject() {

    var center: Point3D? = null
    var radius: Double = 0.toDouble()
    var phi_min: Double = 0.toDouble()
    var phi_max: Double = 0.toDouble()
    var theta_min: Double = 0.toDouble()
    var theta_max: Double = 0.toDouble()

    var cos_theta_min: Double = 0.toDouble()
    var cos_theta_max: Double = 0.toDouble()

    override var boundingBox: BBox
        get() = BBox()
        set(value: BBox) {
            super.boundingBox = value
        }

    override fun hit(ray: Ray, sr: Hit): Boolean {
        return false
    }

    /*

        double 		t;
        Vector3D temp 	= ray.o.minus(center);
        double 		a 		= ray.d.dot(ray.d);
        double 		b 		= ray.d.dot(temp) * 2.0;
        double 		c 		= temp.dot(temp) - radius * radius;
        double 		disc 	= b * b - 4.0 * a * c;

        if (disc < 0.0)
            return(false);
        else {
            double e = Math.sqrt(disc);
            double denom = 2.0 * a;
            t = (-b - e) / denom;    // smaller root

            if (t > MathUtils.K_EPSILON) {
                Vector3D hit = ray.o + t * ray.d - center;

                double phi = Math.atan2(hit.x, hit.z);
                if (phi < 0.0)
                    phi += 2 * Math.PI;

                if (hit.y <= radius * cos_theta_min &&
                    hit.y >= radius * cos_theta_max &&
                    phi >= phi_min && phi <= phi_max) {

                    sr.setT(t);
                    sr.setNormal(temp.plus(ray.d.mult(t)) + t * ray.d) / radius;
//                    sr.local_hit_point = ray.o + tmin * ray.d;
                    return (true);
                }
            }

            t = (-b + e) / denom;    // larger root

            if (t > kEpsilon) {
                Vector3D hit = ray.o + t * ray.d - center;

                double phi = atan2(hit.x, hit.z);
                if (phi < 0.0)
                    phi += TWO_PI;

                if (hit.y <= radius * cos_theta_min &&
                    hit.y >= radius * cos_theta_max &&
                    phi >= phi_min && phi <= phi_max) {

                    tmin = t;
                    sr.normal = (temp + t * ray.d) / radius;   // points outwards
                    sr.local_hit_point = ray.o + tmin * ray.d;
                    return (true);
                }
            }
        }

        return (false);
    }
    */

    override fun shadowHit(ray: Ray, tmin: ShadowHit): Boolean {
        return false
    }
}
