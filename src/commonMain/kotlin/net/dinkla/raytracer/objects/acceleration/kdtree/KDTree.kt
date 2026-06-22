package net.dinkla.raytracer.objects.acceleration.kdtree

import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.hits.ShadowHit
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.objects.acceleration.CompoundWithMesh
import net.dinkla.raytracer.objects.acceleration.kdtree.builder.SpatialMedianBuilder
import net.dinkla.raytracer.objects.acceleration.kdtree.builder.TreeBuilder
import net.dinkla.raytracer.utilities.Counter
import net.dinkla.raytracer.utilities.Logger
import kotlin.math.ln

/**
 * A k-d tree (axis-aligned BSP) acceleration structure. Unlike the uniform regular grid
 * ([net.dinkla.raytracer.objects.acceleration.Grid]), it adapts to the scene by recursively splitting
 * space with axis-aligned planes; the split strategy is pluggable via [builder] (see [TreeBuilder]).
 * This structure is not from Suffern's book — it was ported in from a separate diploma thesis (see
 * the project's CLAUDE.md).
 *
 * [initialize] builds the [root] node tree from the contained objects; [hit] then walks it. The tree
 * must be initialised before the first [hit].
 */
class KDTree(
    var builder: TreeBuilder = SpatialMedianBuilder(),
    var root: Node? = null,
) : CompoundWithMesh() {
    /**
     * Builds the tree: computes the bounding box (via the superclass), logs the depth the heuristic
     * `8 + 1.3·log2(n)` suggests if it differs from the builder's configured [TreeBuilder.maxDepth],
     * then delegates the actual recursive construction to [builder] and stores the result in [root].
     */
    override fun initialize() {
        super.initialize()
        val n = 8 + (1.3 * (ln(objects.size.toDouble()) / ln(2.0))).toInt()
        if (n != builder.maxDepth) {
            Logger.warn("Ideal maxDepth = " + n + ", but set to " + builder.maxDepth)
        }
        //        builder.setMaxDepth(n);
        root = builder.build(this, boundingBox)
        Statistics.print(this)
    }

    /** Intersects [ray] with the tree by descending the [root] node hierarchy. Requires [initialize] first. */
    override fun hit(
        ray: Ray,
        sr: IHit,
    ): Boolean {
        Counter.count("KDTree.hit")
        val node = requireNotNull(root) { "KDTree.root not built; call initialize() before hit()" }
        return node.hit(ray, Hit(sr))
    }

    /**
     * Shadow-ray test reusing [hit]. Deprecated because it reads [tmin] as input while also writing the
     * resulting distance back into it, which is an irregular convention for a shadow test.
     */
    @Deprecated("KDTree shadowHit uses tmin as input?")
    override fun shadowHit(
        ray: Ray,
        tmin: ShadowHit,
    ): Boolean {
        Counter.count("KDTree.shadowHit")
        val h = Hit(tmin.t)
        val b = hit(ray, h)
        tmin.t = h.t
        return b
    }

    companion object {
        internal const val maxDepth = 15
    }
}
