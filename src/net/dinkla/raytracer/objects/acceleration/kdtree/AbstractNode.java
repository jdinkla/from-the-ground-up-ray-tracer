package net.dinkla.raytracer.objects.acceleration.kdtree;

import net.dinkla.raytracer.hits.Hit;
import net.dinkla.raytracer.math.BBox;
import net.dinkla.raytracer.math.Ray;

/**
* Created by IntelliJ IDEA.
* User: jorndinkla
* Date: 11.06.2010
* Time: 09:40:20
* To change this template use File | Settings | File Templates.
*/
public abstract class AbstractNode {

    abstract public boolean hit(Ray ray, Hit sr);

    abstract public BBox getBoundingBox();

    abstract public int size();

    abstract public String printBBoxes(int incr);

}
