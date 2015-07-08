package net.dinkla.raytracer.utilities;

/*
 * Copyright (c) 2012 by Jörn Dinkla, www.dinkla.com, All rights reserved.
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class AppProperties {

    static public Properties properties;

    static {
        properties = new Properties();
        try {
            properties.load(new FileInputStream("raytracer.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static public Object get(final Object key) {
        return properties.get(key);
    }

    static public Integer getAsInteger(final Object key) {
        return Integer.valueOf((String)properties.get(key));
    }

}
