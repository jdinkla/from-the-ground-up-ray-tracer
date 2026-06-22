package net.dinkla.raytracer.objects.acceleration.kdtree.builder

import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.objects.acceleration.kdtree.KDTree
import net.dinkla.raytracer.objects.acceleration.kdtree.Node

/**
 * Strategy for constructing a [KDTree]'s node hierarchy from its objects. Implementations differ in
 * how they choose split planes — spatial median ([SpatialMedianBuilder]), object median
 * ([ObjectMedianBuilder]/[ObjectMedian2Builder]), or a surface-area-heuristic cost
 * ([TestBuilder]/[Test2Builder]). Selected via the `kdtree(builder = ...) { }` DSL block.
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
