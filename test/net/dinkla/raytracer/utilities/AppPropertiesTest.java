package net.dinkla.raytracer.utilities;

/*
 * Copyright (c) 2012 by Jörn Dinkla, www.dinkla.com, All rights reserved.
 */

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AppPropertiesTest {

    @Test
    public void get() {
        String a = (String) AppProperties.INSTANCE.get("test.id");
        assertEquals(a, "4321");
    }

}
