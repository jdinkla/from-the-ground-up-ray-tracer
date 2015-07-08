package net.dinkla.raytracer.math;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 16.06.2010
 * Time: 20:11:36
 * To change this template use File | Settings | File Templates.
 */
public class IntervalTest {

    @Test
    public void interval() {
        Interval i = new Interval(0.1f, 0.9f);
        assertEquals(i.p, 0.1f);
        assertEquals(i.q, 0.9f);        
    }

    @Test
    public void contains() {
        Interval i = new Interval(0.1f, 0.9f);

        assertTrue(i.contains(0.1f));
        assertTrue(i.contains(0.2f));
        assertTrue(i.contains(0.9f));
        assertFalse(i.contains(0.91f));
        assertFalse(i.contains(0.09f));
    }
    
    @Test
    public void isDisjointTo() {
        Interval i0 = new Interval(Integer.MIN_VALUE, 2);
        Interval i1 = new Interval(1, 2);
        Interval i2 = new Interval(2, 3);
        Interval i3 = new Interval(3, 4);
        Interval i4 = new Interval(4, Integer.MAX_VALUE);
        
        assertTrue(i1.isDisjointTo(i3));
        assertFalse(i1.isDisjointTo(i2));
        assertFalse(i2.isDisjointTo(i3));
        assertTrue(i3.isDisjointTo(i1));
        assertFalse(i2.isDisjointTo(i1));
        assertFalse(i3.isDisjointTo(i2));

        assertTrue(i0.isDisjointTo(i3));
        assertFalse(i0.isDisjointTo(i2));
        assertTrue(i3.isDisjointTo(i0));
        assertFalse(i2.isDisjointTo(i0));

        assertTrue(i0.isDisjointTo(i4));
        assertTrue(i4.isDisjointTo(i0));

        assertFalse(i4.isDisjointTo(i3));
        assertFalse(i3.isDisjointTo(i4));
    }


    @Test
    public void partialOverlaps() {
        Interval i0 = new Interval(Float.NEGATIVE_INFINITY, 2.5f);
        Interval i1 = new Interval(1.0f, 2.5f);
        Interval i2 = new Interval(2.0f, 3.5f);
        Interval i3 = new Interval(3.0f, 4.5f);
        Interval i4 = new Interval(4.0f, Float.POSITIVE_INFINITY);

        assertFalse(i1.partialOverlaps(i3));
        assertFalse(i3.partialOverlaps(i1));

        assertTrue(i1.partialOverlaps(i2));
        assertTrue(i2.partialOverlaps(i1));

        assertTrue(i2.partialOverlaps(i3));
        assertTrue(i3.partialOverlaps(i2));

        assertFalse(i0.partialOverlaps(i3));
        assertFalse(i3.partialOverlaps(i0));

        assertTrue(i0.partialOverlaps(i2));
        assertTrue(i2.partialOverlaps(i0));

        assertFalse(i0.partialOverlaps(i4));
        assertFalse(i4.partialOverlaps(i0));

        assertTrue(i4.partialOverlaps(i3));
        assertTrue(i3.partialOverlaps(i4));        
    }

    @Test
    public void fullyOverlaps() {
        Interval i0 = new Interval(Float.NEGATIVE_INFINITY, 2.5f);
        Interval i1 = new Interval(1.0f, 2.0f);
        Interval i2 = new Interval(1.0f, 2.5f);
        Interval i3 = new Interval(1.0f, 3.5f);

        assertTrue(i0.fullyOverlaps(i1));
        assertTrue(i0.fullyOverlaps(i2));
        assertFalse(i0.fullyOverlaps(i3));

        assertFalse(i1.fullyOverlaps(i0));
        assertFalse(i2.fullyOverlaps(i0));
        assertTrue(i3.fullyOverlaps(i2));

    }

}
