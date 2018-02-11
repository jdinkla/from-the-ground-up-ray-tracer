package net.dinkla.raytracer.samplers;

import org.junit.jupiter.api.BeforeEach;

public class JitteredTest extends GeneratorTest {

    @BeforeEach
    @Override
    public void initialize() {
        new Jittered().generateSamples(NUM_SAMPLES, NUM_SETS , samples);
    }

}
