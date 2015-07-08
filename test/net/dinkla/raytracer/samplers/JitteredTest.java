package net.dinkla.raytracer.samplers;

import org.testng.annotations.BeforeTest;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 22.05.2010
 * Time: 19:06:38
 * To change this template use File | Settings | File Templates.
 */
public class JitteredTest extends GeneratorTest {

    @BeforeTest
    @Override
    public void initialize() {
        new Jittered().generateSamples(NUM_SAMPLES, NUM_SETS , samples);
    }

}
