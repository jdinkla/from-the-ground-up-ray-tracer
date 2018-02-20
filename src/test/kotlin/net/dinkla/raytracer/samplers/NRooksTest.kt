package net.dinkla.raytracer.samplers

import org.junit.jupiter.api.BeforeEach

class NRooksTest : IGeneratorTest() {

    @BeforeEach
    override fun initialize() {
        NRooks().generateSamples(IGeneratorTest.NUM_SAMPLES, IGeneratorTest.NUM_SETS, samples)
    }

}
