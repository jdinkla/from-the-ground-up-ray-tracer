package net.dinkla.raytracer.lights;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.materials.Material;
import net.dinkla.raytracer.math.*;
import net.dinkla.raytracer.worlds.World;

import java.util.ArrayList;
import java.util.List;

/**
 * The original C code in the book [] is not thread safe. The samplePoint, lightNormal
 * and wi are shared by some methods. Parallel execution yields artifacts in the generated image.
 * So the state is extracted into subclass Sample.
 * 
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 13.04.2010
 * Time: 16:39:38
 */
public class AreaLight<C extends Color> extends Light<C> implements ILightSource {

    public ILightSource object;

    // Emissive Material TODO: Warum nicht Emissive?
    public Material<C> material;

    public int numSamples;

    public class Sample {
        public Point3DF samplePoint;
        public Normal lightNormal;
        public Vector3DF wi;

        public float getNDotD() {
            return lightNormal.negate().dot(wi);
        }
    }

    public AreaLight() {
        super();
        numSamples = 4;
    }

    public C L(World<C> world, Shade sr, Sample sample) {
        if (sample.getNDotD() > 0) {
            return (C) sr.getMaterial().getLe(sr);
        } else {
            return (C) C.getBlack();
        }
    }

    public boolean inShadow(World<C> world, Ray ray, Shade sr, Sample sample) {
        float d = sample.samplePoint.minus(ray.o).dot(ray.d);
        return world.inShadow(ray, sr, d);
    }

    public float G(Shade sr, Sample sample) {
        float nDotD = sample.getNDotD();
        float d2 = sample.samplePoint.distanceSquared(sr.getHitPoint());        
        return nDotD / d2;
    }

    public float pdf(Shade sr) {
        return object.pdf(sr);
    }

    public Sample getSample(Shade sr) {
        Sample sample = new Sample();
        sample.samplePoint = object.sample();
        sample.lightNormal = object.getNormal(sample.samplePoint);
        sample.wi = sample.samplePoint.minus(sr.getHitPoint()).normalize();
        return sample;
    }

    public List<Sample> getSamples(Shade sr) {
        List<Sample> result = new ArrayList<Sample>();
        for (int i = 0; i < numSamples; i++) {
            result.add(getSample(sr));
        }
        return result;
    }

    public Point3DF sample() {
        throw new RuntimeException("NLU");
    }


    public Normal getNormal(Point3DF p) {
        throw new RuntimeException("NLU");
    }

    @Override
    public C L(World<C> world, Shade sr) {
        throw new RuntimeException("NLU");
    }

    @Override
    public Vector3DF getDirection(Shade sr) {
        throw new RuntimeException("NLU");
    }

    @Override
    public boolean inShadow(World world, Ray ray, Shade sr) {
        throw new RuntimeException("NLU");
    }

}
