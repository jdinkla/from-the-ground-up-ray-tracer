package net.dinkla.raytracer.samplers

import org.junit.jupiter.api.BeforeEach

class RegularTest : AbstractGeneratorTest() {

    @BeforeEach
    override fun initialize() {
        Regular().generateSamples(NUM_SAMPLES, NUM_SETS, samples)
    }

}
