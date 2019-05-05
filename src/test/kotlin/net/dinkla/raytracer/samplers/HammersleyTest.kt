package net.dinkla.raytracer.samplers

import org.junit.jupiter.api.BeforeEach

class HammersleyTest : AbstractGeneratorTest() {

    @BeforeEach
    override fun initialize() {
        Hammersley().generateSamples(NUM_SAMPLES, NUM_SETS, samples)
    }

}
