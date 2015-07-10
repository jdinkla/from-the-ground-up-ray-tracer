package net.dinkla.raytracer.samplers;

import org.testng.annotations.BeforeTest;

import static org.testng.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 12.04.2010
 * Time: 10:18:17
 * To change this template use File | Settings | File Templates.
 */
public class PureRandomTest extends GeneratorTest {

    @BeforeTest
    public void initialize() {
        new PureRandom().generateSamples(NUM_SAMPLES, NUM_SETS , samples);
    }

}
