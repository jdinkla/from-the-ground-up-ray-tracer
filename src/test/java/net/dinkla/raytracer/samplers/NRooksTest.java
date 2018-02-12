package net.dinkla.raytracer.samplers;

import org.junit.jupiter.api.BeforeEach;

public class NRooksTest extends GeneratorTest {

    @BeforeEach
    @Override
    public void initialize() {
        new NRooks().generateSamples(NUM_SAMPLES, NUM_SETS , samples);
    }
    
}
