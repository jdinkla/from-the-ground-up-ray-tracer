package net.dinkla.raytracer.hits;

import net.dinkla.raytracer.math.Normal;
import net.dinkla.raytracer.objects.compound.Compound;
import net.dinkla.raytracer.objects.GeometricObject;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 16.05.2010
 * Time: 18:57:19
 * To change this template use File | Settings | File Templates.
 */
public class Hit extends ShadowHit {

    protected Normal normal;

    // Wird erst ab Compound gefüllt
    protected GeometricObject object;

    public Hit() {
        super();
        normal = null;
        object = null;
    }

    public Hit(final float t) {
        super(t);
        normal = null;
        object = null;
    }

    public Hit(final Hit hit) {
        super(hit.t);
        normal = hit.normal;
        object = hit.object;
    }

    public void set(final Hit hit) {
        t = hit.t;
        normal = hit.normal;
        object = hit.object;
    }

    public Normal getNormal() {
        return normal;
    }

    public void setNormal(final Normal normal) {
        this.normal = normal;
    }

    public void setObject(GeometricObject object) {
        assert(!(object instanceof Compound));
        this.object = object;
    }

    public GeometricObject getObject() {
        return object;
    }
    
}
