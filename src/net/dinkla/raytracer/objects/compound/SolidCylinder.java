package net.dinkla.raytracer.objects.compound;

import net.dinkla.raytracer.hits.Hit;
import net.dinkla.raytracer.math.*;
import net.dinkla.raytracer.objects.Disk;
import net.dinkla.raytracer.objects.OpenCylinder;

public class SolidCylinder extends Compound {

    BBox bbox;
    
    public SolidCylinder(double y0, double y1, double radius) {
        Disk bottom = new Disk(new Point3D(0, y0, 0), radius, new Normal(0, -1, 0));
        Disk top = new Disk(new Point3D(0, y1, 0), radius, new Normal(0, 1, 0));
        OpenCylinder oc = new OpenCylinder(y0, y1, radius);

        objects.add(bottom);
        objects.add(oc);
        objects.add(top);

        bbox = new BBox(new Point3D(-radius, y0, -radius), new Point3D(radius, y1, radius));
    }

    @Override
    public boolean hit(final Ray ray, Hit sr) {
        if (bbox.hit(ray)) {
            return super.hit(ray, sr);
        } else {
            return false;
        }
    }
}
