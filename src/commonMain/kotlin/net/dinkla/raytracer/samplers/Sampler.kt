package net.dinkla.raytracer.samplers

import net.dinkla.raytracer.math.MathUtils.PI
import net.dinkla.raytracer.math.Point2D
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.utilities.Random
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class Sampler(
    sampler: IGenerator = Jittered,
    private var numSamples: Int = 100,
    private var numSets: Int = 10,
) : UnitDiskSampler {
    private var shuffledIndices = ArrayList<Int>()
    private var samples = ArrayList<Point2D>()
    private var diskSamples = ArrayList<Point2D>()
    private var hemisphereSamples = ArrayList<Point3D>()
    private var sphereSamples = ArrayList<Point3D>()

    private var count: Int = 0
    private var jump: Int = 0

    // Effective points per set. The sqrt-based generators (MultiJittered/Jittered/Regular) emit
    // floor(sqrt(numSamples))^2 points per set rather than the requested numSamples, so indexing by
    // the requested count overran the array (TASK-31). Derive the stride from what the generator
    // actually produced, which keeps NRooks/PureRandom (exactly numSamples per set) unchanged.
    private val samplesPerSet: Int

    init {
        samples = ArrayList(sampler.generateSamples(numSamples, numSets).toList())
        samplesPerSet = if (numSets > 0) samples.size / numSets else samples.size
        setupShuffledIndices()
    }

    private fun setupShuffledIndices() {
        shuffledIndices = ArrayList()
        shuffledIndices.ensureCapacity(samplesPerSet * numSets)

        // Create temporary array
        val indices = ArrayList<Int>()
        for (j in 0 until samplesPerSet) {
            indices.add(j)
        }

        for (p in 0 until numSets) {
            Random.randomShuffle(indices)
            shuffledIndices.addAll(indices)
        }
    }

    fun sampleUnitSquare(): Point2D {
        if (count % samplesPerSet == 0) {
            jump = Random.int(numSets) * samplesPerSet
        }
        val index1 = jump + count++ % samplesPerSet
        val index2 = jump + shuffledIndices[index1]
        return samples[index2]
    }

    override fun sampleUnitDisk(): Point2D {
        if (count % samplesPerSet == 0) {
            jump = Random.int(numSets) * samplesPerSet
        }
        return diskSamples[jump + shuffledIndices[jump + count++ % samplesPerSet]]
    }

    fun sampleHemisphere(): Point3D {
        if (count % samplesPerSet == 0) {
            jump = Random.int(numSets) * samplesPerSet
        }
        return hemisphereSamples[jump + shuffledIndices[jump + count++ % samplesPerSet]]
    }

    fun sampleSphere(): Point3D {
        if (count % samplesPerSet == 0) {
            jump = Random.int(numSets) * samplesPerSet
        }
        return sphereSamples[jump + shuffledIndices[jump + count++ % samplesPerSet]]
    }

    fun mapSamplesToUnitDisk() {
        val size = samples.size
        var r: Double
        var phi: Double
        diskSamples = ArrayList(size)
        for (p in samples) {
            val sp = Point2D(2.0 * p.x - 1.0, 2.0 * p.y - 1.0)
            if (sp.x > -sp.y) { // sectors 1 and 2
                if (sp.x > sp.y) { // sector 1
                    r = sp.x
                    phi = sp.y / sp.x
                } else { // sector 2
                    r = sp.y
                    phi = 2 - sp.x / sp.y
                }
            } else { // sectors 3 and 4
                if (sp.x < sp.y) { // sector 3
                    r = -sp.x
                    phi = 4 + sp.y / sp.x
                } else { // sector 4
                    r = -sp.y
                    phi =
                        if (sp.y != 0.0) {
                            // avoid division by zero at origin
                            6 - sp.x / sp.y
                        } else {
                            0.0
                        }
                }
            }
            phi *= PI / 4.0
            diskSamples.add(Point2D(r * cos(phi), r * sin(phi)))
        }
    }

    fun mapSamplesToHemiSphere(exp: Double) {
        hemisphereSamples = ArrayList(samples.size)
        for (sample in samples) {
            val cos_phi = cos(2.0 * PI * sample.x)
            val sin_phi = sin(2.0 * PI * sample.x)
            val cos_theta = (1.0 - sample.y).pow(1.0 / (exp + 1.0))
            val sin_theta = sqrt(1.0 - cos_theta * cos_theta)
            val pu = sin_theta * cos_phi
            val pv = sin_theta * sin_phi
            hemisphereSamples.add(Point3D(pu, pv, cos_theta))
        }
    }

    fun mapSamplesToSphere() {
        var x: Double
        var y: Double
        var z: Double
        var r: Double
        var phi: Double
        sphereSamples = ArrayList(samples.size)
        for (j in samples.indices) {
            val p = samples[j]
            z = 1.0 - 2.0 * p.x
            r = sqrt(1.0 - z * z)
            phi = 2.0 * PI * p.y
            x = r * cos(phi)
            y = r * sin(phi)
            sphereSamples.add(Point3D(x, y, z))
        }
    }

    companion object {
        fun shuffleXCoordinates(
            numSamples: Int,
            numSets: Int,
            samples: MutableList<Point2D>,
        ) {
            for (p in 0 until numSets) {
                for (i in 0 until numSamples - 1) {
                    val target = Random.int(numSamples) + p * numSamples
                    val source = i + p * numSamples + 1
                    val temp = samples[source].x
                    samples[source] = Point2D(samples[target].x, samples[source].y)
                    samples[target] = Point2D(temp, samples[target].y)
                }
            }
        }

        fun shuffleYCoordinates(
            numSamples: Int,
            numSets: Int,
            samples: MutableList<Point2D>,
        ) {
            for (p in 0 until numSets) {
                for (i in 0 until numSamples - 1) {
                    val target = Random.int(numSamples) + p * numSamples
                    val source = i + p * numSamples + 1
                    val temp = samples[i + p * numSamples + 1].y
                    samples[source] = Point2D(samples[source].x, samples[target].y)
                    samples[target] = Point2D(samples[target].x, temp)
                }
            }
        }
    }
}
