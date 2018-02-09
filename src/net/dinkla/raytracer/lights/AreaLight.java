package net.dinkla.raytracer.lights;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.hits.ShadowHit;
import net.dinkla.raytracer.materials.Material;
import net.dinkla.raytracer.math.*;
import net.dinkla.raytracer.objects.GeometricObject;
import net.dinkla.raytracer.worlds.World;

import java.util.ArrayList;
import java.util.List;


public class AreaLight extends Light implements ILightSource {

    public ILightSource object;

    // Emissive Material TODO: Warum nicht Emissive?
    public Material material;

    public int numSamples;

    public class Sample {
        public Point3D samplePoint;
        public Normal lightNormal;
        public Vector3D wi;

        public double getNDotD() {
            return lightNormal.negate().dot(wi);
        }
    }

    public AreaLight() {
        super();
        numSamples = 4;
    }

    public Color L(World world, Shade sr, Sample sample) {
        if (sample.getNDotD() > 0) {
            return sr.getMaterial().getLe(sr);
        } else {
            return Color.BLACK;
        }
    }

    public boolean inShadow(World world, Ray ray, Shade sr, Sample sample) {
        double d = sample.samplePoint.minus(ray.getO()).dot(ray.getD());
        return world.inShadow(ray, sr, d);
    }

    public double G(Shade sr, Sample sample) {
        double nDotD = sample.getNDotD();
        double d2 = sample.samplePoint.distanceSquared(sr.getHitPoint());
        return nDotD / d2;
    }

    public double pdf(Shade sr) {
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

    public Point3D sample() {
        throw new RuntimeException("NLU");
    }


    public Normal getNormal(Point3D p) {
        throw new RuntimeException("NLU");
    }

    @Override
    public Color L(World world, Shade sr) {
        throw new RuntimeException("NLU");
    }

    @Override
    public Vector3D getDirection(Shade sr) {
        throw new RuntimeException("NLU");
    }

    @Override
    public boolean inShadow(World world, Ray ray, Shade sr) {
        throw new RuntimeException("NLU");
    }

}
