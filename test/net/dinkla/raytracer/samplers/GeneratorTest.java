package net.dinkla.raytracer.samplers;

import net.dinkla.raytracer.math.Histogram;
import net.dinkla.raytracer.math.Point2D;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 22.05.2010
 * Time: 18:58:20
 * To change this template use File | Settings | File Templates.
 */
public abstract class GeneratorTest {

    static final int NUM_SAMPLES = 10000;
    static final int NUM_SETS = 10;

    protected List<Point2D> samples = new ArrayList<Point2D>();

    @Before
    public abstract void initialize();

    @Test
    public void size() {
        assertEquals(samples.size(), NUM_SAMPLES * NUM_SETS);
    }

    @Test
    public void betweenZeroAndOne() {
        for (Point2D p : samples) {
            assert p.getX() >= 0.0f && p.getX() < 1.0f;
            assert p.getY() >= 0.0f && p.getY() < 1.0f;
        }
    }

    @Test
    public void distribution() {
        Histogram histX = new Histogram();
        Histogram histY = new Histogram();
        Histogram histXY = new Histogram();

        for (Point2D p : samples) {
            final int x = (int) (p.getX() * 10);
            final int y = (int) (p.getY() * 10);
            histX.add(x);
            histY.add(y);
            histXY.add(x * 10 + y);
        }

        System.out.println("--- X ---");
        histX.println();

        System.out.println("--- Y ---");
        histY.println();

        System.out.println("--- XY ---");
        histXY.println();
    }

}
