package net.dinkla.raytracer.samplers;

import net.dinkla.raytracer.math.Point2D;
import net.dinkla.raytracer.math.Random;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 21.05.2010
 * Time: 19:12:42
 * To change this template use File | Settings | File Templates.
 */
public class NRooks extends Generator {

    public void generateSamples(int numSamples, int numSets, List<Point2D> samples) {
        for (int p = 0; p < numSets; p++) {
            for (int j = 0; j < numSamples; j++) {
                final float x = (j + Random.INSTANCE.randFloat()) / numSamples;
                final float y = (j + Random.INSTANCE.randFloat()) / numSamples;
                samples.add(new Point2D(x, y));
            }
        }

        Sampler.shuffleXCoordinates(numSamples, numSets, samples);
        Sampler.shuffleYCoordinates(numSamples, numSets, samples);
    }

}