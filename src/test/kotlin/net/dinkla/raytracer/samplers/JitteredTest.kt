package net.dinkla.raytracer.samplers

import org.junit.jupiter.api.BeforeEach

class JitteredTest : IGeneratorTest() {

    @BeforeEach
    override fun initialize() {
        Jittered().generateSamples(IGeneratorTest.NUM_SAMPLES, IGeneratorTest.NUM_SETS, samples)
    }

}
