package net.dinkla.raytracer.math;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 10.04.2010
 * Time: 23:23:59
 * To change this template use File | Settings | File Templates.
 */
public class WrappedFloatTest {

    void set(WrappedFloat f) {
        f.setValue(1.23f);
    }

    @Test
    void testMethodParameter() {
        WrappedFloat f = new WrappedFloat();
        set(f);
        assert (f.value == 1.23f);
    }

    @Test
    void testInitialValue() {
        WrappedFloat f = new WrappedFloat();
        assert (null == f.value);
    }

    @Test
    void testComparable() {
        WrappedFloat f1 = new WrappedFloat();
        WrappedFloat f2 = new WrappedFloat();
        WrappedFloat f3 = new WrappedFloat(1.0f);
        WrappedFloat f4 = new WrappedFloat(2.0f);
        assertEquals(f1, f2);
        assertEquals(f3.compareTo(f4), -1);
        assertEquals(f1.compareTo(f4), -1);
        assertEquals(f2.compareTo(f4), -1);
        assertEquals(f4.compareTo(f3), 1);
    }
}
