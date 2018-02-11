package net.dinkla.raytracer.samplers;

import net.dinkla.raytracer.math.Histogram;
import net.dinkla.raytracer.math.Point2D;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class GeneratorTest {

    static final int NUM_SAMPLES = 10000;
    static final int NUM_SETS = 10;

    protected List<Point2D> samples = new ArrayList<Point2D>();

    @BeforeEach
    public abstract void initialize();

    @Test
    public void size() {
        assertEquals(samples.size(), NUM_SAMPLES * NUM_SETS);
    }

    @Test
    public void betweenZeroAndOne() {
        for (Point2D p : samples) {
            assert p.getX() >= 0.0 && p.getX() < 1.0;
            assert p.getY() >= 0.0 && p.getY() < 1.0;
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
