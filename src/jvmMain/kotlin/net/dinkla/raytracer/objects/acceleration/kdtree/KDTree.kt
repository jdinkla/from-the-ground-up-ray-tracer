package net.dinkla.raytracer.objects.acceleration.kdtree

import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.hits.ShadowHit
import net.dinkla.raytracer.interfaces.Counter
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.objects.acceleration.CompoundWithMesh
import net.dinkla.raytracer.objects.acceleration.kdtree.builder.SpatialMedianBuilder
import net.dinkla.raytracer.objects.acceleration.kdtree.builder.TreeBuilder
import net.dinkla.raytracer.utilities.Logger
import kotlin.math.ln

class KDTree(
        var builder: TreeBuilder = SpatialMedianBuilder(),
        var root: Node? = null) : CompoundWithMesh() {

    override fun initialize() {
        super.initialize()
        val n = 8 + (1.3 * (ln(objects.size.toDouble()) / ln(2.0))).toInt()
        if (n != builder.maxDepth) {
            Logger.warn("Ideal maxDepth = " + n + ", but set to " + builder.maxDepth)
        }
        //        builder.setMaxDepth(n);
        root = builder.build(this, boundingBox)
        Statistics.statistics(this)
        // Logger.info(root!!.printBBoxes(0))
    }

    override fun hit(ray: Ray, sr: IHit): Boolean {
        Counter.count("KDTree.hit")
        return root!!.hit(ray, Hit(sr))
    }

    override fun shadowHit(ray: Ray, tmin: ShadowHit): Boolean {
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
