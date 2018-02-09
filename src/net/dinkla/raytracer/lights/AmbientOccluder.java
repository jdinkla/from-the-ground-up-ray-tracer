package net.dinkla.raytracer.lights;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.math.Point3D;
import net.dinkla.raytracer.math.Ray;
import net.dinkla.raytracer.math.Vector3D;
import net.dinkla.raytracer.samplers.Sampler;
import net.dinkla.raytracer.worlds.World;

/**
 *
 * TODO: Da war shared state drin. Böse bei der Parallelisierung
 */
public class AmbientOccluder extends Ambient {

    //public Vector3D u, v, w;
    public final Color minAmount;
    public final Sampler sampler;
    public final int numSamples;

    public AmbientOccluder(Sampler sampler, int numSamples) {
        this.minAmount =  Color.WHITE;
        this.sampler = sampler;
        this.numSamples = numSamples;
    }

    public AmbientOccluder(Color minAmount, Sampler sampler, int numSamples) {
        this.minAmount = minAmount;
        this.sampler = sampler;
        this.numSamples = numSamples;
    }

    @Override
    public Color L(World world, Shade sr) {
        Vector3D w = new Vector3D(sr.getNormal());
        // jitter up vector in case normal is vertical
        Vector3D v = w.cross(Vector3D.Companion.getJITTER()).normalize();
        Vector3D u = v.cross(w);

        int numHits = 0;
        for (int i = 0; i < numSamples; i++) {
            Point3D p = sampler.sampleHemisphere();
            Vector3D dir = u.mult(p.getX()).plus(v.mult(p.getY())).plus(w.mult(p.getZ()));
            Ray shadowRay = new Ray(sr.getHitPoint(), dir);
            if (inShadow(world, shadowRay, sr)) {
                numHits++;
            }
        }
        double ratio = 1.0 - (1.0 * numHits / numSamples);
        return  color.mult(ls * ratio);
    }

    @Override
    public Vector3D getDirection(Shade sr) {
        Point3D p = sampler.sampleHemisphere();
        Vector3D w = new Vector3D(sr.getNormal());
        Vector3D v = w.cross(Vector3D.Companion.getJITTER()).normalize();
        Vector3D u = v.cross(w);
        return u.mult(p.getX()).plus(v.mult(p.getY())).plus(w.mult(p.getZ()));
    }

    @Override
    public boolean inShadow(World world, Ray ray, Shade sr) {
        return world.inShadow(ray, sr, Double.MAX_VALUE);
    }
}
