package net.dinkla.raytracer.samplers;

import net.dinkla.raytracer.math.Histogram;
import net.dinkla.raytracer.math.Point2D;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;

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

    @BeforeTest
    public abstract void initialize();

    @Test
    public void size() {
        assertEquals(samples.size(), NUM_SAMPLES * NUM_SETS);
    }

    @Test
    public void betweenZeroAndOne() {
        for (Point2D p : samples) {
            assert p.x >= 0.0f && p.x < 1.0f;
            assert p.y >= 0.0f && p.y < 1.0f;
        }
    }

    @Test
    public void distribution() {
        Histogram histX = new Histogram();
        Histogram histY = new Histogram();
        Histogram histXY = new Histogram();

        for (Point2D p : samples) {
            final int x = (int) (p.x * 10);
            final int y = (int) (p.y * 10);
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
