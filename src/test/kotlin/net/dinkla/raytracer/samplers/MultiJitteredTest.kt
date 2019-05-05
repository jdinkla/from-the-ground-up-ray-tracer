package net.dinkla.raytracer.samplers

import org.junit.jupiter.api.BeforeEach

class MultiJitteredTest : AbstractGeneratorTest() {

    @BeforeEach
    override fun initialize() {
        MultiJittered().generateSamples(NUM_SAMPLES, NUM_SETS, samples)
    }

}
