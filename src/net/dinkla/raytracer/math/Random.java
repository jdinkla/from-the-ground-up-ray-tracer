package net.dinkla.raytracer.math;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 21.05.2010
 * Time: 18:14:31
 * To change this template use File | Settings | File Templates.
 */
public class Random {

    static java.util.Random r = new java.util.Random();
    
    public static int randInt(final int high) {
        return r.nextInt(high);
    }

    public static int randInt(final int low, final int high) {
        return r.nextInt(high - low) + low;
    }

    public static float randFloat() {
        return r.nextFloat();
    }

    public static float randFloat(final float low, final float high) {
        return r.nextFloat() * (high - low) + low;
    }

    public static void setRandSeed(final int seed) {
        r.setSeed(seed);
    }

    public static void randomShuffle(List<Integer> ls) {
        int n = ls.size();
        for (int i=1; i<n; ++i) {
            int i1 = i;
            int i2 = randInt(n);
            Integer tmp = ls.get(i1);
            ls.set(i1, ls.get(i2));
            ls.set(i2, tmp);
        }
    }
}
