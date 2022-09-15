package net.dinkla.raytracer.objects.parts

import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.hits.ShadowHit
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.objects.GeometricObject

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

    override fun hit(ray: Ray, sr: IHit): Boolean {
        return false
    }

    /*

        double 		t;
        Vector3D temp 	= ray.origin.minus(center);
        double 		a 		= ray.direction.dot(ray.direction);
        double 		b 		= ray.direction.dot(temp) * 2.0;
        double 		c 		= temp.dot(temp) - radius * radius;
        double 		disc 	= b * b - 4.0 * a * c;

        if (disc < 0.0)
            return(false);
        else {
            double e = Math.sqrt(disc);
            double denom = 2.0 * a;
            t = (-b - e) / denom;    // smaller root

            if (t > MathUtils.K_EPSILON) {
                Vector3D hit = ray.origin + t * ray.direction - center;

                double phi = Math.atan2(hit.x, hit.z);
                if (phi < 0.0)
                    phi += 2 * Math.PI;

                if (hit.y <= radius * cos_theta_min &&
                    hit.y >= radius * cos_theta_max &&
                    phi >= phi_min && phi <= phi_max) {

                    sr.setT(t);
                    sr.setNormal(temp.plus(ray.direction.minus(t)) + t * ray.direction) / radius;
//                    sr.local_hit_point = ray.origin + tmin * ray.direction;
                    return (true);
                }
            }

            t = (-b + e) / denom;    // larger root

            if (t > kEpsilon) {
                Vector3D hit = ray.origin + t * ray.direction - center;

                double phi = atan2(hit.x, hit.z);
                if (phi < 0.0)
                    phi += TWO_PI;

                if (hit.y <= radius * cos_theta_min &&
                    hit.y >= radius * cos_theta_max &&
                    phi >= phi_min && phi <= phi_max) {

                    tmin = t;
                    sr.normal = (temp + t * ray.direction) / radius;   // points outwards
                    sr.local_hit_point = ray.origin + tmin * ray.direction;
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
