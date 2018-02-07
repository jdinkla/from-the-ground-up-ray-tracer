package net.dinkla.raytracer.lights;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.math.Point3D;
import net.dinkla.raytracer.math.Ray;
import net.dinkla.raytracer.math.Vector3D;
import net.dinkla.raytracer.samplers.Sampler;
import net.dinkla.raytracer.worlds.World;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 13.04.2010
 * Time: 10:28:11
 * To change this template use File | Settings | File Templates.
 *
 * TODO: Da war shared state drin. Böse bei der Parallelisierung
 */
public class AmbientOccluder<C extends Color> extends Ambient<C> {

    //public Vector3D u, v, w;
    public final C minAmount;
    public final Sampler sampler;
    public final int numSamples;

    public AmbientOccluder(Sampler sampler, int numSamples) {
        this.minAmount = (C) C.WHITE;
        this.sampler = sampler;
        this.numSamples = numSamples;
    }

    public AmbientOccluder(C minAmount, Sampler sampler, int numSamples) {
        this.minAmount = minAmount;
        this.sampler = sampler;
        this.numSamples = numSamples;
    }

    @Override
    public C L(World<C> world, Shade sr) {
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
        return (C) color.mult(ls * ratio);
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
    public boolean inShadow(World<C> world, Ray ray, Shade sr) {
        return world.inShadow(ray, sr, Float.MAX_VALUE);
    }
}
