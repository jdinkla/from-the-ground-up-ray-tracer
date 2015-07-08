package net.dinkla.raytracer.objects.acceleration.kdtree;

import net.dinkla.raytracer.hits.Hit;
import net.dinkla.raytracer.math.BBox;
import net.dinkla.raytracer.math.Ray;
import net.dinkla.raytracer.objects.GeometricObject;
import net.dinkla.raytracer.objects.acceleration.Grid;
import net.dinkla.raytracer.objects.compound.Compound;

import java.util.List;

/**
* Created by IntelliJ IDEA.
* User: jorndinkla
* Date: 11.06.2010
* Time: 09:40:41
* To change this template use File | Settings | File Templates.
*/
public class Leaf extends AbstractNode {
    protected final Compound compound;

    public Leaf(List<GeometricObject> objects) {
//        if (objects.size() > 1000) {
//            compound = new Grid();
//        } else {
            compound = new Compound();
//        }
        compound.add(objects);
        compound.initialize();
    }

    @Override
    public boolean hit(Ray ray, Hit sr) {
        return compound.hit(ray, sr);
    }

    @Override
    public BBox getBoundingBox() {
        return compound.getBoundingBox();
    }

    @Override
    public int size() {
        return compound.size();
    }

    @Override
    public String toString() {
        return "Leaf " + size() + " " + getBoundingBox(); 
    }

    @Override
    public String printBBoxes(int incr) {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<incr;i++) {
            sb.append(" ");
        }
        sb.append("-");
        return sb.toString();
    }
}
