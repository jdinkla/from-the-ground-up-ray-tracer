package net.dinkla.raytracer.objects;

import net.dinkla.raytracer.hits.Hit;
import net.dinkla.raytracer.hits.ShadowHit;
import net.dinkla.raytracer.materials.Material;
import net.dinkla.raytracer.math.BBox;
import net.dinkla.raytracer.math.Ray;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 10.04.2010
 * Time: 14:11:59
 * To change this template use File | Settings | File Templates.
 */
public abstract class GeometricObject {

    protected boolean shadows = true;
    
    protected Material material;

    protected boolean isInitialized;

    public GeometricObject() {
        isInitialized = false;
    }
    
    public abstract boolean hit(final Ray ray, Hit sr);

    public abstract boolean shadowHit(final Ray ray, ShadowHit tmin);

    public abstract BBox getBoundingBox();

    public void initialize() {
        isInitialized = true;
    }

    public Material getMaterial() {
        return material;
    }
    
    public void setMaterial(final Material material) {
        this.material = material;
    }

    public boolean isShadows() {
        return shadows;
    }

    public void setShadows(boolean shadows) {
        this.shadows = shadows;
    }
}
