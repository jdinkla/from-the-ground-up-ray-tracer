package net.dinkla.raytracer.objects.acceleration.kdtree

import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.Ray

/**
 * A node of a [KDTree]: either an [InnerNode] (a split plane with two children) or a [Leaf] (a bucket
 * of objects). The [KDTree] descends this hierarchy to find ray intersections.
 */
interface Node {
    /** The axis-aligned region of space this node covers. */
    val boundingBox: BBox

    /**
     * Intersects [ray] with the subtree rooted at this node, recording the closest hit into [sr] and
     * returning true on success.
     */
    fun hit(
        ray: Ray,
        sr: Hit,
    ): Boolean

    /** The number of objects contained in the subtree rooted at this node. */
    fun size(): Int

    /** Renders the subtree's bounding boxes as an indented (by [incr] spaces) multi-line string, for debugging. */
    fun printBBoxes(incr: Int): String
}
