package net.dinkla.raytracer.samplers

import org.junit.jupiter.api.BeforeEach

class JitteredTest : GeneratorTest() {

    @BeforeEach
    override fun initialize() {
        Jittered().generateSamples(GeneratorTest.NUM_SAMPLES, GeneratorTest.NUM_SETS, samples)
    }

}
