package net.dinkla.raytracer.utilities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AppPropertiesTest {

    @Test
    public void get() {
        String a = (String) AppProperties.INSTANCE.get("test.id");
        assertEquals(a, "4321");
    }

}
