package net.dinkla.raytracer.samplers

import net.dinkla.raytracer.math.Point2D
import net.dinkla.raytracer.utilities.Random

object MultiJittered : IGenerator {
    override fun generateSamples(
        numSamples: Int,
        numSets: Int,
    ): List<Point2D> {
        val n = IGenerator.sqrt(numSamples)
        val samplesPerSet = n * n
        val subcellWidth = 1.0 / samplesPerSet

        // Each set holds exactly n*n points (samplesPerSet). The per-set base offset must therefore
        // stride by samplesPerSet (p * n * n), not p * numSets — the latter overlapped the sets and
        // left later sets full of unwritten origin points (TASK-31).
        val samples = ArrayList<Point2D>(numSets * samplesPerSet)
        for (i in 0 until numSets * samplesPerSet) {
            samples.add(i, Point2D.ORIGIN)
        }

        // distribute points in the initial patterns
        for (p in 0 until numSets) {
            for (i in 0 until n) {
                for (j in 0 until n) {
                    val target = i * n + j + p * samplesPerSet
                    val x = (i * n + j) * subcellWidth + Random.double(0.0, subcellWidth)
                    val y = (j * n + i) * subcellWidth + Random.double(0.0, subcellWidth)
                    samples[target] = Point2D(x, y)
                }
            }
        }

        // shuffle x coordinates
        for (p in 0 until numSets) {
            for (i in 0 until n) {
                for (j in 0 until n) {
                    val k = Random.int(j, n)
                    val source = i * n + j + p * samplesPerSet
                    val target = i * n + k + p * samplesPerSet
                    val temp = samples[source].x
                    samples[source] = Point2D(samples[target].x, samples[source].y)
                    samples[target] = Point2D(temp, samples[target].y)
                }
            }
        }

        // shuffle y coordinates
        for (p in 0 until numSets) {
            for (i in 0 until n) {
                for (j in 0 until n) {
                    val k = Random.int(j, n)
                    val target = k * n + i + p * samplesPerSet
                    val source = j * n + i + p * samplesPerSet
                    val temp = samples[source].y
                    samples[source] = Point2D(samples[source].x, samples[target].y)
                    samples[target] = Point2D(samples[target].x, temp)
                }
            }
        }
        return samples
    }
}
