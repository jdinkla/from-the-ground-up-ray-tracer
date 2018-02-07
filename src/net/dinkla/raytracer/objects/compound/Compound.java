package net.dinkla.raytracer.objects.compound;

import net.dinkla.raytracer.hits.Hit;
import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.hits.ShadowHit;
import net.dinkla.raytracer.materials.Material;
import net.dinkla.raytracer.math.*;
import net.dinkla.raytracer.objects.GeometricObject;
import net.dinkla.raytracer.utilities.Counter;
import net.dinkla.raytracer.worlds.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 13.04.2010
 * Time: 13:12:07
 * To change this template use File | Settings | File Templates.
 */
public class Compound extends GeometricObject {

    /**
     * The objects contained in this compound.
     */
    protected List<GeometricObject> objects;

    /**
     * The bounding box set up in <code>initialize()</code>.
     */
    protected BBox bbox;

    public boolean isUnit;

    /**
     * Constructor.
     */                        
    public Compound() {
        super();
        objects = new ArrayList<GeometricObject>();
        isUnit = false;
    }

    //@Override
    public boolean hit(final Ray ray, Hit sr) {
        if (!getBoundingBox().hit(ray)) {
            Counter.count("Compound.hit.bbox");
            return false;
        }
        Counter.count("Compound.hit");

        boolean hit = false;
        for (GeometricObject geoObj : objects) {
            Counter.count("Compound.hit.object");
            Hit sr2 = new Hit(sr.getT());
            boolean b = geoObj.hit(ray, sr2);
            if (b && sr2.getT() < sr.getT()) {
                hit = true;
                sr.setT(sr2.getT());
                sr.setNormal(sr2.getNormal());
                if (!(geoObj instanceof Compound)) {
                    sr.setObject(geoObj);
                } else {
                    sr.setObject(sr2.getObject());
                }
            }
        }
        return hit;
    }

    public Shade hitObjects(World world, Ray ray) {
        Counter.count("Compound.hitObjects");
        WrappedFloat tmin = WrappedFloat.createMax();
        Shade sr = new Shade();
        boolean b = hit(ray, sr);
        return sr;
    }

    @Override
    public boolean shadowHit(final Ray ray, ShadowHit tmin) {
        Counter.count("Compound.shadowHit");
        //WrappedFloat t = WrappedFloat.createMax();
        for (GeometricObject geoObj : objects) {
            Counter.count("Compound.shadowHit.object");
            if (geoObj.shadowHit(ray, tmin)) {
//                tmin.setT(t.getValue());
                return true;
            }
        }
        return false;
    }

    public boolean inShadow(Ray ray, Shade sr, double d) {
        Counter.count("Compound.inShadow");
        //TODO: Wieso hier createMax ? ShadowHit t = ShadowHit.createMax();
        ShadowHit t = new ShadowHit(d);
        for (GeometricObject geoObj : objects) {
            boolean b = geoObj.shadowHit(ray, t);
            if (b && t.getT() < d) {
                return true;
            }
        }
        return false;
    }

    public void add(GeometricObject object) {
        isInitialized = false;
        objects.add(object);
    }

    public void add(List<GeometricObject> objects) {
        isInitialized = false;
        this.objects.addAll(objects);
    }

    @Override
    public void initialize() {
        super.initialize();
        for (GeometricObject object : objects) {
            object.initialize();
        }
        // TODO Warum wird das vorberechnet? Warum nicht lazy?
        getBoundingBox();
    }
    
    public void setMaterial(Material material) {
        this.material = material;
        for (GeometricObject geoObj : objects) {
            geoObj.setMaterial(material);
        }
    }

    public void setShadows(boolean shadows) {
        this.shadows = shadows;
        for (GeometricObject geoObj : objects) {
            geoObj.setShadows(shadows);
        }
    }

    public int size() {
        if (isUnit) {
            return 1;
        } else {
            int size = 0;
            for (GeometricObject geoObj : objects) {
                if (geoObj instanceof Compound) {
                    size += ((Compound) geoObj).size();
                } else {
                    size += 1;
                }
            }
            return size;
        }
    }

    @Override
    public BBox getBoundingBox() {
        if (null == bbox) {
            if (objects.size() > 0) {
                Point3D p0 = PointUtilities.INSTANCE.minCoordinates(objects);
                Point3D p1 = PointUtilities.INSTANCE.maxCoordinates(objects);
                bbox = new BBox(p0, p1);
            } else {
                bbox = new BBox(null, null);
            }
        }
        return bbox;
    }

    public List<GeometricObject> getObjects() {
        return objects;
    }
}
