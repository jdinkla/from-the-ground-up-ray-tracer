package net.dinkla.raytracer.samplers

import org.junit.jupiter.api.BeforeEach

class MultiJitteredTest : IGeneratorTest() {

    @BeforeEach
    override fun initialize() {
        MultiJittered().generateSamples(IGeneratorTest.NUM_SAMPLES, IGeneratorTest.NUM_SETS, samples)
    }

}
