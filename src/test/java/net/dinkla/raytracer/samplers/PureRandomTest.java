package net.dinkla.raytracer.samplers;

import org.junit.jupiter.api.BeforeEach;

public class PureRandomTest extends GeneratorTest {

    @BeforeEach
    public void initialize() {
        new PureRandom().generateSamples(NUM_SAMPLES, NUM_SETS , samples);
    }

}
