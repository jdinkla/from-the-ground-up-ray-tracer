package net.dinkla.raytracer.samplers

import org.junit.jupiter.api.BeforeEach

class RegularTest : IGeneratorTest() {

    @BeforeEach
    override fun initialize() {
        Regular().generateSamples(IGeneratorTest.NUM_SAMPLES, IGeneratorTest.NUM_SETS, samples)
    }

}
