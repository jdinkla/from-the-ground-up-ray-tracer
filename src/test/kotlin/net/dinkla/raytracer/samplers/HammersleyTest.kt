package net.dinkla.raytracer.samplers

import org.junit.jupiter.api.BeforeEach

class HammersleyTest : IGeneratorTest() {

    @BeforeEach
    override fun initialize() {
        Hammersley().generateSamples(IGeneratorTest.NUM_SAMPLES, IGeneratorTest.NUM_SETS, samples)
    }

}
