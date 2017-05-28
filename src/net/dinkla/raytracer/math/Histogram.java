package net.dinkla.raytracer.math;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 22.05.2010
 * Time: 18:34:41
 * To change this template use File | Settings | File Templates.
 */
public class Histogram {

    Map<Integer, Integer> counts;

    public Histogram() {
        counts = new TreeMap<Integer, Integer>();
    }

    public void add(Integer key) {
        counts.merge(key, 1, (a, b) -> a + b);
    }

    public Integer get(Integer key) {
        Integer elem = counts.get(key);
        return (null == elem) ? 0 : elem;
    }

    public void clear() {
        counts.clear();
    }

    public Set<Integer> keySet() {
        return counts.keySet();
    }

    public void println() {
        Integer min = Integer.MAX_VALUE;
        Integer max = Integer.MIN_VALUE;
        for (Integer k : keySet()) {
            Integer v = get(k);
            if (v>max) max = v;
            if (v<min) min = v;
            System.out.println("k=" +k + ", v=" + v);
        }
        if (min != Integer.MAX_VALUE ||  max != Integer.MIN_VALUE ) {
            System.out.println("min=" + min + ", max=" + max);
        }
    }

}
