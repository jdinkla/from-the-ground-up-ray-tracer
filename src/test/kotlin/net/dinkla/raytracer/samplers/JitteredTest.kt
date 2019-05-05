package net.dinkla.raytracer.samplers

import org.junit.jupiter.api.BeforeEach

class JitteredTest : AbstractGeneratorTest() {

    @BeforeEach
    override fun initialize() {
        Jittered().generateSamples(NUM_SAMPLES, NUM_SETS, samples)
    }

}
