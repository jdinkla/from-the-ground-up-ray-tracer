package net.dinkla.raytracer.utilities;

import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 09.06.2010
 * Time: 21:59:18
 * To change this template use File | Settings | File Templates.
 */
public class Counter {

    static final String EMPTY = "                                                            ";

    public static boolean PAUSE = false;
    protected static final Counter INSTANCE = new Counter();

    // For each thread-id there is a map
    protected ConcurrentHashMap<Long, TreeMap<String, Integer>> instances;

//    protected TreeMap<String, Integer> map;
//    protected ConcurrentHashMap<String, Integer> map;

    private Counter() {
        instances = new ConcurrentHashMap<Long, TreeMap<String, Integer>>();
    }

    static public void count(String key) {
        if (!PAUSE) {
            Long id = Thread.currentThread().getId();
            TreeMap<String, Integer> map = INSTANCE.instances.get(id);
            if (null == map) {
                map = new TreeMap<String, Integer>();
                INSTANCE.instances.put(id, map);
            }
            Integer c = map.get(key);
            if (null == c) {
                c = 0;
            }
            map.put(key, c+1);
        }
    }

    static public void stats(final int columns) {
        TreeMap<String, Integer> results = new TreeMap<String, Integer>();
        for (Long id : INSTANCE.instances.keySet()) {
            TreeMap<String, Integer> map = INSTANCE.instances.get(id);
            for (String key : map.keySet()) {
                Integer c = results.get(key);
                if (null == c) {
                    c = 0;
                }
                results.put(key, c + map.get(key));
            }
        }

        System.out.println("Counter");
        for (String key : results.keySet()) {
            int spaces = columns - key.length() - 1;
            int count = results.get(key);                                 
            System.out.println(key + ":" + EMPTY.substring(0, spaces) + count);
        }
    }

}
