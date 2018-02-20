package net.dinkla.raytracer.samplers

import net.dinkla.raytracer.math.Point2D

interface IGenerator {

    fun generateSamples(numSamples: Int, numSets: Int, samples: MutableList<Point2D>)

}
