package net.dinkla.raytracer.samplers

import net.dinkla.raytracer.math.Point2D

/**
 * Supplies points distributed over the unit disk, used to jitter a ray origin across a lens aperture
 * (the thin-lens depth-of-field model, Suffern ch. 10) or to sample a disk-shaped area light.
 *
 * [Sampler] is the production implementation (its samples are mapped onto the disk by
 * [Sampler.mapSamplesToUnitDisk]); the interface exists so consumers can be driven by a deterministic
 * fake in tests.
 */
fun interface UnitDiskSampler {
    /** A point inside the unit disk (radius 1, centred at the origin). */
    fun sampleUnitDisk(): Point2D
}
