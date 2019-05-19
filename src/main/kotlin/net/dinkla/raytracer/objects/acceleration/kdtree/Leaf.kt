package net.dinkla.raytracer.objects.acceleration.kdtree

import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.objects.GeometricObject
import net.dinkla.raytracer.objects.acceleration.Grid
import net.dinkla.raytracer.objects.compound.Compound

class Leaf(objects: List<GeometricObject>) : AbstractNode() {
    protected val compound: Compound

    override val boundingBox: BBox
        get() = compound.boundingBox

    init {
        //        if (objects.size() > 1000) {
        //            compound = new Grid();
        //        } else {
        compound = Compound()
        //        }
        compound.add(objects)
        compound.initialize()
    }

    override fun hit(ray: Ray, sr: Hit): Boolean = compound.hit(ray, sr)

    override fun size(): Int = compound.size()

    override fun printBBoxes(incr: Int): String {
        val sb = StringBuilder()
        for (i in 0 until incr) {
            sb.append(" ")
        }
        sb.append("-")
        return sb.toString()
    }

    override fun toString(): String = "Leaf(${size()}, ${boundingBox}"
}
