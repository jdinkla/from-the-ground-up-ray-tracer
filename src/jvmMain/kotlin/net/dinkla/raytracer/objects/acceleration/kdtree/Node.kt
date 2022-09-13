package net.dinkla.raytracer.objects.acceleration.kdtree

import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.Ray

interface Node {

    val boundingBox: BBox

    fun hit(ray: Ray, sr: Hit): Boolean

    fun size(): Int

    fun printBBoxes(incr: Int): String
}
