package net.dinkla.raytracer.samplers;

import net.dinkla.raytracer.math.Point2D;
import net.dinkla.raytracer.math.Random;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 21.05.2010
 * Time: 19:22:47
 * To change this template use File | Settings | File Templates.
 */
public class MultiJittered extends Generator {

    public void generateSamples(int numSamples, int numSets, List<Point2D> samples) {
        int n = (int) Math.sqrt((float)numSamples);
        float subcell_width = 1.0f / ((float) numSamples);

        // distribute points in the initial patterns
        for (int p = 0; p < numSets; p++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    final int target = i * n + j + p * numSamples;
                    final float x = (i * n + j) * subcell_width + Random.randFloat(0, subcell_width);
                    final float y = (j * n + i) * subcell_width + Random.randFloat(0, subcell_width);
                    samples.add(target, new Point2D(x, y));
                }
            }
        }

        // shuffle x coordinates
        for (int p = 0; p < numSets; p++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    int k = Random.randInt(j, n);
                    int source = i * n + j + p * numSamples;
                    int target = i * n + k + p * numSamples;
                    float temp = samples.get(source).x;
                    samples.set(source, new Point2D(samples.get(target).x, samples.get(source).y));
                    samples.set(target, new Point2D(temp, samples.get(target).y));
                }
            }
        }

        // shuffle y coordinates
        for (int p = 0; p < numSets; p++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    int k = Random.randInt(j, n);
                    int target = k * n + i + p * numSamples;
                    int source = j * n + i + p * numSamples;
                    float temp = samples.get(source).y;
                    samples.set(source, new Point2D(samples.get(source).x, samples.get(target).y));
                    samples.set(target, new Point2D(samples.get(target).x, temp));
                }
            }
        }
    }

}
