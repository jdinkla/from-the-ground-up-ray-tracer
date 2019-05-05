package net.dinkla.raytracer.samplers

import org.junit.jupiter.api.BeforeEach

class PureRandomTest : AbstractGeneratorTest() {

    @BeforeEach
    override fun initialize() {
        PureRandom().generateSamples(NUM_SAMPLES, NUM_SETS, samples)
    }

}
