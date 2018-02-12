package net.dinkla.raytracer.samplers;

import org.junit.jupiter.api.BeforeEach;

public class MultiJitteredTest extends GeneratorTest {

    @BeforeEach
    @Override
    public void initialize() {
        new MultiJittered().generateSamples(NUM_SAMPLES, NUM_SETS , samples);
    }

}
