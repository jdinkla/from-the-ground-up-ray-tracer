package net.dinkla.raytracer.samplers;

import org.junit.jupiter.api.BeforeEach;

public class RegularTest extends GeneratorTest {

    @BeforeEach
    @Override
    public void initialize() {
        new Regular().generateSamples(NUM_SAMPLES, NUM_SETS , samples);
    }
    
}
