package net.dinkla.raytracer.samplers

import org.junit.jupiter.api.BeforeEach

class MultiJitteredTest : GeneratorTest() {

    @BeforeEach
    override fun initialize() {
        MultiJittered().generateSamples(GeneratorTest.NUM_SAMPLES, GeneratorTest.NUM_SETS, samples)
    }

}
