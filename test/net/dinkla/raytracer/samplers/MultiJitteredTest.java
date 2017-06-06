package net.dinkla.raytracer.samplers;

import org.junit.Before;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 22.05.2010
 * Time: 19:07:06
 * To change this template use File | Settings | File Templates.
 */
public class MultiJitteredTest extends GeneratorTest {

    @Before
    @Override
    public void initialize() {
        new MultiJittered().generateSamples(NUM_SAMPLES, NUM_SETS , samples);
    }

}
