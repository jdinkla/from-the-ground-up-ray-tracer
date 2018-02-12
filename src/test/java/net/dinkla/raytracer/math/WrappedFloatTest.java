package net.dinkla.raytracer.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WrappedFloatTest {

    void set(WrappedFloat f) {
        f.setValue(1.23);
    }

    @Test
    public void testMethodParameter() {
        WrappedFloat f = new WrappedFloat();
        set(f);
        assert (f.getValue() == 1.23);
    }

    @Test
    public void testInitialValue() {
        WrappedFloat f = new WrappedFloat();
        assert (null == f.getValue());
    }

    @Test
    public void testComparable() {
        WrappedFloat f1 = new WrappedFloat();
        WrappedFloat f2 = new WrappedFloat();
        WrappedFloat f3 = new WrappedFloat(1.0);
        WrappedFloat f4 = new WrappedFloat(2.0);
        assertEquals(f1, f2);
        assertEquals(f3.compareTo(f4), -1);
        assertEquals(f1.compareTo(f4), -1);
        assertEquals(f2.compareTo(f4), -1);
        assertEquals(f4.compareTo(f3), 1);
    }
}
