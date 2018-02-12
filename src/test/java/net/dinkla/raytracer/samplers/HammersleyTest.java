package net.dinkla.raytracer.samplers;

import org.junit.jupiter.api.BeforeEach;

public class HammersleyTest extends GeneratorTest {

    @BeforeEach
    @Override
    public void initialize() {
        new Hammersley().generateSamples(NUM_SAMPLES, NUM_SETS , samples);
    }

}
