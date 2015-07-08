package net.dinkla.raytracer.math;

import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 22.05.2010
 * Time: 18:40:58
 * To change this template use File | Settings | File Templates.
 */
public class HistogramTest {

    @Test
    public void functional() {
        Histogram hist = new Histogram();
        assertEquals(0, hist.keySet().size());
        hist.add(3);
        assertEquals(1, hist.keySet().size());
        hist.add(3);
        hist.add(3);
        hist.add(3);
        assertEquals(1, hist.keySet().size());
        hist.add(2);
        assertEquals(2, hist.keySet().size());
        hist.add(2);
        assertEquals(2, hist.keySet().size());

        assertEquals((int) 4, (int) hist.get(3));
        assertEquals((int) 2, (int) hist.get(2));
        assertEquals((int) 0, (int) hist.get(1));

        hist.clear();
        assertEquals(0, hist.keySet().size());

        assertEquals((int) 0, (int) hist.get(3));
        assertEquals((int) 0, (int) hist.get(2));
        assertEquals((int) 0, (int) hist.get(1));

    }
}
