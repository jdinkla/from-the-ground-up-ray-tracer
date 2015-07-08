package net.dinkla.raytracer.samplers;

import org.testng.annotations.BeforeTest;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 22.05.2010
 * Time: 19:03:33
 * To change this template use File | Settings | File Templates.
 */
public class NRooksTest extends GeneratorTest {

    @BeforeTest
    @Override
    public void initialize() {
        new NRooks().generateSamples(NUM_SAMPLES, NUM_SETS , samples);
    }
    
}
