package net.dinkla.raytracer.samplers

import org.junit.jupiter.api.BeforeEach

class NRooksTest : GeneratorTest() {

    @BeforeEach
    override fun initialize() {
        NRooks().generateSamples(GeneratorTest.NUM_SAMPLES, GeneratorTest.NUM_SETS, samples)
    }

}
