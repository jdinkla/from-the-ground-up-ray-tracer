package net.dinkla.raytracer.math;


import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 22.05.2010
 * Time: 19:41:42
 * To change this template use File | Settings | File Templates.
 */
public class RandomTest {

    final int NUM = 1000;
    
    @Test
    public void randInt() {
        for (int i=0; i<NUM; i++) {
            int r = Random.randInt(10);
            assert 0 <= r;
            assert r < 10;
        }

        for (int i=0; i<NUM; i++) {
            int r = Random.randInt(18, 44);
            assert 18 <= r;
            assert r < 44;
        }
    }

    @Test
    public void randFloat() {
        for (int i=0; i<NUM; i++) {
            float r = Random.randFloat();
            assert 0.0f <= r;
            assert r < 1.0f;
        }
    }

    @Test
    public void randomShuffle() {
        List<Integer> ls = new ArrayList<Integer>();

        assertEquals(0, ls.size());
        Random.randomShuffle(ls);
        assertEquals(0, ls.size());

        ls.add(11);
        ls.add(12);
        ls.add(13);

        assertEquals(3, ls.size());

        Histogram i0 = new Histogram();
        Histogram i1 = new Histogram();
        Histogram i2 = new Histogram();

        for (int i=0; i<NUM; i++) {
            Random.randomShuffle(ls);
            assertEquals(3, ls.size());
            assertTrue(11 == ls.get(0) || 11 == ls.get(1) || 11 == ls.get(2));
            assertTrue(12 == ls.get(0) || 12 == ls.get(1) || 12 == ls.get(2));
            assertTrue(13 == ls.get(0) || 13 == ls.get(1) || 13 == ls.get(2));
            i0.add(ls.get(0));
            i1.add(ls.get(1));
            i2.add(ls.get(2));
        }
        
        assertEquals(3, i0.keySet().size());
        assertEquals(3, i1.keySet().size());
        assertEquals(3, i2.keySet().size());
    }
    
}
