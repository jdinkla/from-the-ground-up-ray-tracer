package net.dinkla.raytracer.objects.acceleration.kdtree

import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.Ray

abstract class AbstractNode {

    abstract val boundingBox: BBox

    abstract fun hit(ray: Ray, sr: Hit): Boolean

    abstract fun size(): Int

    abstract fun printBBoxes(incr: Int): String

}
