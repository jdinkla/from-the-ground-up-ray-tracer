package net.dinkla.raytracer.objects.parts;

import net.dinkla.raytracer.hits.Hit;
import net.dinkla.raytracer.hits.ShadowHit;
import net.dinkla.raytracer.math.*;
import net.dinkla.raytracer.objects.GeometricObject;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 27.05.2010
 * Time: 23:30:10
 * To change this template use File | Settings | File Templates.
 */
public class ConvexPartSphere extends GeometricObject {

    public Point3DF center;
    public double 		radius;
    public double 		phi_min;
    public double 		phi_max;
    public double 		theta_min;
    public double 		theta_max;

    public double		cos_theta_min;
    public double		cos_theta_max;

    public ConvexPartSphere() {
        super();
    }

    @Override
    public boolean hit(Ray ray, Hit sr) {
        return false;
    }

        /*

        double 		t;
        Vector3DF temp 	= ray.o.minus(center);
        double 		a 		= ray.d.dot(ray.d);
        double 		b 		= ray.d.dot(temp) * 2.0f;
        double 		c 		= temp.dot(temp) - radius * radius;
        double 		disc 	= b * b - 4.0 * a * c;

        if (disc < 0.0)
            return(false);
        else {
            double e = Math.sqrt(disc);
            double denom = 2.0 * a;
            t = (-b - e) / denom;    // smaller root

            if (t > MathUtils.K_EPSILON) {
                Vector3DF hit = ray.o + t * ray.d - center;

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
                Vector3DF hit = ray.o + t * ray.d - center;

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

    @Override
    public boolean shadowHit(Ray ray, ShadowHit tmin) {
        return false;
    }

    @Override
    public BBox getBoundingBox() {
        return null;
    }
}
