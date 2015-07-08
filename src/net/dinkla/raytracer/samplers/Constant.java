package net.dinkla.raytracer.samplers;

import net.dinkla.raytracer.math.Point2D;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 23.05.2010
 * Time: 12:05:00
 * To change this template use File | Settings | File Templates.
 */
public class Constant extends Generator {

    public void generateSamples(int numSamples, int numSets, List<Point2D> samples) {
        int n = (int) Math.sqrt(numSamples);
        for (int j = 0; j < numSets; j++) {
            for (int p = 0; p < numSamples; p++) {
              samples.add(new Point2D(0.5f, 0.5f));
            }
        }
    }
    
}
