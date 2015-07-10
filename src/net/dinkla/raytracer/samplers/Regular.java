package net.dinkla.raytracer.samplers;

import net.dinkla.raytracer.math.Point2DF;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 21.05.2010
 * Time: 19:10:50
 * To change this template use File | Settings | File Templates.
 */
public class Regular extends Generator {

    public void generateSamples(int numSamples, int numSets, List<Point2DF> samples) {
        int n = (int) Math.sqrt(numSamples);
        for (int j = 0; j < numSets; j++) {
            for (int p = 0; p < n; p++) {
                for (int q = 0; q < n; q++) {
                    samples.add(new Point2DF((q + 0.5f) / n, (p + 0.5f) / n));
                }
            }
        }

    }
}
