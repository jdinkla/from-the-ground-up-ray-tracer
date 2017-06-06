package net.dinkla.raytracer.samplers;

import org.junit.Before;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 22.05.2010
 * Time: 18:59:36
 * To change this template use File | Settings | File Templates.
 */
public class HammersleyTest extends GeneratorTest {

    @Before
    @Override
    public void initialize() {
        new Hammersley().generateSamples(NUM_SAMPLES, NUM_SETS , samples);
    }

}
