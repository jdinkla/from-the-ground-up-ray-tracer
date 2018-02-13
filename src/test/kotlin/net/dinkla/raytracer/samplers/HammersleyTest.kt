package net.dinkla.raytracer.samplers

import org.junit.jupiter.api.BeforeEach

class HammersleyTest : GeneratorTest() {

    @BeforeEach
    override fun initialize() {
        Hammersley().generateSamples(GeneratorTest.NUM_SAMPLES, GeneratorTest.NUM_SETS, samples)
    }

}
