package net.dinkla.raytracer.objects;

import net.dinkla.raytracer.math.*;
import net.dinkla.raytracer.objects.compound.Compound;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 13.04.2010
 * Time: 14:02:58
 * To change this template use File | Settings | File Templates.
 */
public class Box extends Compound {

    BBox bbox;
    public Point3DF p0;
    public Point3DF p1;

    /**
     *
     * @param p0        bottom left front
     * @param a
     * @param b
     * @param c
     */
    public Box(Point3DF p0, Vector3DF a, Vector3DF b, Vector3DF c) {

        // point at the "top left front"
        //Rectangle rBottom = new Rectangle(p0, b, a);
        Rectangle rBottom = new Rectangle(p0, a, b, true);
        Rectangle rTop = new Rectangle(p0.plus(c), a, b);
        Rectangle rFront = new Rectangle(p0, a, c);
//        Rectangle rBehind = new Rectangle(p0.plus(b), c, a);
        Rectangle rBehind = new Rectangle(p0.plus(b), a, c, true);
//        Rectangle rLeft = new Rectangle(p0, c, b);
        Rectangle rLeft = new Rectangle(p0, b, c, true);
        Rectangle rRight = new Rectangle(p0.plus(a), b, c);

        objects.add(rBottom);
        objects.add(rTop);
        objects.add(rBehind);
        objects.add(rLeft);
        objects.add(rRight);
        objects.add(rFront);

        this.p0 = p0;
        this.p1 = p0.plus(a).plus(b).plus(c);
        
        bbox = new BBox(p0, p1);
    }

}
