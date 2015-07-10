package net.dinkla.raytracer.samplers;

import net.dinkla.raytracer.math.Point2DF;
import net.dinkla.raytracer.math.Random;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 21.05.2010
 * Time: 18:23:01
 * To change this template use File | Settings | File Templates.
 */
public class PureRandom extends Generator {

    public void generateSamples(int numSamples, int numSets, List<Point2DF> samples) {
        assert samples != null;
        for (int p = 0; p < numSets; p++) {
            for (int q = 0; q < numSamples; q++) {
                samples.add(new Point2DF(Random.randFloat(), Random.randFloat()));
            }
        }
    }

}
