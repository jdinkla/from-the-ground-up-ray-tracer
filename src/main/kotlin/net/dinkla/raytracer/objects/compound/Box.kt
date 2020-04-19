package net.dinkla.raytracer.objects.compound

import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.objects.Rectangle
import net.dinkla.raytracer.utilities.equals
import net.dinkla.raytracer.interfaces.hash

class Box(val p0: Point3D, val a: Vector3D, val b: Vector3D, val c: Vector3D) : Compound() {

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

        boundingBox = BBox(p0, p0 + a + b + c)
    }

    override fun equals(other: Any?): Boolean = this.equals<Box>(other) { a, b ->
        a.p0 == b.p0 && a.a == b.a && a.b == b.b && a.c == b.c
    }

    override fun hashCode(): Int = this.hash(p0, a, b, c)

    override fun toString(): String = "AlignedBox($p0,$a,$b,$c)"

}
