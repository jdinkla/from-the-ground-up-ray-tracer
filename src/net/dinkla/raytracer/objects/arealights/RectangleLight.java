package net.dinkla.raytracer.objects.arealights;

import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.lights.ILightSource;
import net.dinkla.raytracer.math.Point2DF;
import net.dinkla.raytracer.math.Point3D;
import net.dinkla.raytracer.math.Vector3D;
import net.dinkla.raytracer.objects.Rectangle;
import net.dinkla.raytracer.samplers.Sampler;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 22.05.2010
 * Time: 20:39:09
 * To change this template use File | Settings | File Templates.
 */
public class RectangleLight extends Rectangle implements ILightSource {

    public Sampler sampler;

    protected float pdf;

    public RectangleLight(Point3D p0, Vector3D a, Vector3D b) {
        super(p0, a, b);
        pdf = 1.0f / (a.length() * b.length());
    }

    public float pdf(Shade sr) {
		return pdf;
    }

    public Point3D sample() {
        Point2DF sp = sampler.sampleUnitSquare();
        return p0.plus(a.mult(sp.x())).plus(b.mult(sp.y()));
    }
    
}


