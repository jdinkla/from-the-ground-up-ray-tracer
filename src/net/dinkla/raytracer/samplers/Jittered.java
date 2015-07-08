package net.dinkla.raytracer.samplers;

import net.dinkla.raytracer.math.Point2D;
import net.dinkla.raytracer.math.Random;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 21.05.2010
 * Time: 18:13:36
 * To change this template use File | Settings | File Templates.
 */
public class Jittered extends Generator {

    public void generateSamples(int numSamples, int numSets, List<Point2D> samples) {
        int n = (int) Math.sqrt(numSamples);
        for (int p=0; p<numSets; p++) {
            for (int j=0; j<n; j++) {
               for (int k=0; k<n; k++) {
                   float x = (k + Random.randFloat()) / n;
                   float y = (j + Random.randFloat()) / n;
                   samples.add(new Point2D(x, y));
               }
            }
        }
    }

}
