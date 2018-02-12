package net.dinkla.raytracer.objects

import net.dinkla.raytracer.math.*
import net.dinkla.raytracer.objects.compound.Compound

class Box(var p0: Point3D, a: Vector3D, b: Vector3D, c: Vector3D) : Compound() {
    var p1: Point3D

    init {

        // point at the "top left front"
        //Rectangle rBottom = new Rectangle(p0, b, a);
        val rBottom = Rectangle(p0, a, b, true)
        val rTop = Rectangle(p0.plus(c), a, b)
        val rFront = Rectangle(p0, a, c)
        //        Rectangle rBehind = new Rectangle(p0.plus(b), c, a);
        val rBehind = Rectangle(p0.plus(b), a, c, true)
        //        Rectangle rLeft = new Rectangle(p0, c, b);
        val rLeft = Rectangle(p0, b, c, true)
        val rRight = Rectangle(p0.plus(a), b, c)

        objects.add(rBottom)
        objects.add(rTop)
        objects.add(rBehind)
        objects.add(rLeft)
        objects.add(rRight)
        objects.add(rFront)
        this.p1 = p0.plus(a).plus(b).plus(c)

        boundingBox = BBox(p0, p1)
    }

}
