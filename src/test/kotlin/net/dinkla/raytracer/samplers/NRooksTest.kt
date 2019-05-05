package net.dinkla.raytracer.samplers

import org.junit.jupiter.api.BeforeEach

class NRooksTest : AbstractGeneratorTest() {

    @BeforeEach
    override fun initialize() {
        NRooks().generateSamples(NUM_SAMPLES, NUM_SETS, samples)
    }

}
