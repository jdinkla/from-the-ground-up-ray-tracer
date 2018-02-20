package net.dinkla.raytracer.samplers

import org.junit.jupiter.api.BeforeEach

class PureRandomTest : IGeneratorTest() {

    @BeforeEach
    override fun initialize() {
        PureRandom().generateSamples(IGeneratorTest.NUM_SAMPLES, IGeneratorTest.NUM_SETS, samples)
    }

}
