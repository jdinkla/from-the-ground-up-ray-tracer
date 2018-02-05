package net.dinkla.raytracer.samplers

import net.dinkla.raytracer.math.Point2D
import net.dinkla.raytracer.math.Random

class PureRandom : Generator() {

    override fun generateSamples(numSamples: Int, numSets: Int, samples: MutableList<Point2D>?) {
        assert(samples != null)
        for (p in 0 until numSets) {
            for (q in 0 until numSamples) {
                samples!!.add(Point2D(Random.randFloat(), Random.randFloat()))
            }
        }
    }

}
