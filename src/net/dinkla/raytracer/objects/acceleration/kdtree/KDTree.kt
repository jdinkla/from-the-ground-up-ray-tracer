package net.dinkla.raytracer.objects.acceleration.kdtree

import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.ShadowHit
import net.dinkla.raytracer.math.*
import net.dinkla.raytracer.objects.compound.Compound
import net.dinkla.raytracer.objects.mesh.Mesh
import net.dinkla.raytracer.utilities.Counter
import org.apache.log4j.Logger

class KDTree : Compound {

    var builder: IKDTreeBuilder

    var root: AbstractNode? = null

    var mesh: Mesh? = null

    constructor() : super() {
        mesh = null
        root = null
        builder = SpatialMedianBuilder()
    }

    constructor(mesh: Mesh) : super() {
        this.mesh = mesh
        root = null
        builder = SpatialMedianBuilder()
    }

    override fun initialize() {
        super.initialize()
        val n = 8 + (1.3 * (Math.log(objects.size.toDouble()) / Math.log(2.0))).toInt()
        if (n != builder.maxDepth) {
            LOGGER.warn("Ideal maxDepth = " + n + ", but set to " + builder.maxDepth)
        }
        //        builder.setMaxDepth(n);
        root = builder.build(this, boundingBox)
        Statistics.statistics(this)
        LOGGER.info(root!!.printBBoxes(0))
    }

    override fun hit(ray: Ray, sr: Hit): Boolean {
        Counter.count("KDTree.hit")
        return root!!.hit(ray, sr)
    }

    override fun shadowHit(ray: Ray, tmin: ShadowHit): Boolean {
        Counter.count("KDTree.shadowHit")
        val h = Hit()
        h.t = tmin.t
        val b = hit(ray, h)
        tmin.t = h.t
        return b
    }

    companion object {

        internal val LOGGER = Logger.getLogger(KDTree::class.java)

        internal var maxDepth = 15
    }


}
