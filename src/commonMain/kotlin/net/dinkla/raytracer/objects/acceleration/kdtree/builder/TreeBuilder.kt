package net.dinkla.raytracer.objects.acceleration.kdtree.builder

import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.objects.acceleration.kdtree.KDTree
import net.dinkla.raytracer.objects.acceleration.kdtree.Node

/**
 * Strategy for constructing a [KDTree]'s node hierarchy from its objects, selected via the
 * `kdtree(builder = ...) { }` DSL block. Two implementations are kept:
 *
 *  - [SpatialMedianBuilder] — **the canonical, production default**: it splits each voxel at the
 *    spatial median of its bounding box, cycling x/y/z with depth. Wired in as the [KDTree] default
 *    and the DSL default.
 *  - [Simple2Builder] — an alternative that scores the x/y/z mid-plane splits and keeps the
 *    least-overlapping one, falling back to a leaf when a split would duplicate too many objects.
 *    Kept because the `SphereLatticeInKdTree` example selects it to exercise a different split policy.
 *
 * Four further experimental split strategies (object-median variants and two buggy surface-area-cost
 * prototypes) were removed in TASK-62: none was used by production rendering or any example, and each
 * added maintenance weight to every change of this interface, [Node], or `BBox`.
 */
interface TreeBuilder {
    /** Maximum recursion depth; subdivision stops here and the remaining objects become a leaf. */
    var maxDepth: Int

    /**
     * Builds and returns the root [Node] for [tree] over the region [voxel] (the tree's bounding box),
     * recursively splitting until a leaf criterion (depth, object count) is met.
     */
    fun build(
        tree: KDTree,
        voxel: BBox,
    ): Node
}
