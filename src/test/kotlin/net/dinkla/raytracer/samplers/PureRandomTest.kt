package net.dinkla.raytracer.samplers

import org.junit.jupiter.api.BeforeEach

class PureRandomTest : GeneratorTest() {

    @BeforeEach
    override fun initialize() {
        PureRandom().generateSamples(GeneratorTest.NUM_SAMPLES, GeneratorTest.NUM_SETS, samples)
    }

}
