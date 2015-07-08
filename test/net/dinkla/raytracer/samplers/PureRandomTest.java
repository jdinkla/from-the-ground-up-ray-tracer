package net.dinkla.raytracer.samplers;

import net.dinkla.raytracer.math.Histogram;
import net.dinkla.raytracer.math.Point2D;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
