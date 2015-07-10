package net.dinkla.raytracer.objects.arealights;

import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.lights.ILightSource;
import net.dinkla.raytracer.math.*;
import net.dinkla.raytracer.objects.Disk;
import net.dinkla.raytracer.samplers.Sampler;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 22.05.2010
 * Time: 20:36:07
 * To change this template use File | Settings | File Templates.
 */
public class DiskLight extends Disk implements ILightSource {

    public Normal normal;
    
    public Sampler sampler;
    
    public DiskLight(final Point3DF center, final float radius, final Normal normal) {
        super(center, radius, normal);
    }

    public float pdf(Shade sr) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    // TODO: sample auf ner disk
    public Point3DF sample() {
        Point2DF sp = sampler.sampleUnitDisk();
        assert(null != sampler);
        Vector2DF v = new Vector2DF(sp.x() * radius, sp.y() * radius);
        return center.plus(new Vector3DF((float) v.x(), (float) v.y(), 0.0f));
    }
}
