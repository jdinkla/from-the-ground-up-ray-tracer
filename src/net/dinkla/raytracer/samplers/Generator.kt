package net.dinkla.raytracer.samplers

import net.dinkla.raytracer.math.Point2D

abstract class Generator {

    abstract fun generateSamples(numSamples: Int, numSets: Int, samples: MutableList<Point2D>)

}
