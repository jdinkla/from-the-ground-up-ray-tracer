package net.dinkla.raytracer.samplers

import org.junit.jupiter.api.BeforeEach

class RegularTest : GeneratorTest() {

    @BeforeEach
    override fun initialize() {
        Regular().generateSamples(GeneratorTest.NUM_SAMPLES, GeneratorTest.NUM_SETS, samples)
    }

}
