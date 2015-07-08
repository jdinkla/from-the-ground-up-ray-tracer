package net.dinkla.raytracer.samplers;

import net.dinkla.raytracer.math.Point2D;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 21.05.2010
 * Time: 19:07:48
 * To change this template use File | Settings | File Templates.
 */
public class Hammersley extends Generator {

    protected float phi(int j) {
        float x = 0.0f;
        float f = 0.5f;

        while (j > 0) {
            x += f * (float) (j % 2);
            j /= 2;
            f *= 0.5;
        }

        return (x);
    }

    public void generateSamples(int numSamples, int numSets, List<Point2D> samples) {
        for (int p = 0; p < numSets; p++) {
            for (int j = 0; j < numSamples; j++) {
                Point2D pv = new Point2D((float) j / (float) numSamples, phi(j));
                samples.add(pv);
            }
        }
    }
}
