package net.dinkla.raytracer.samplers;

import net.dinkla.raytracer.math.Point2D;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 22.05.2010
 * Time: 18:25:16
 * To change this template use File | Settings | File Templates.
 */
public abstract class Generator {

    public abstract void generateSamples(int numSamples, int numSets, List<Point2D> samples);

}
