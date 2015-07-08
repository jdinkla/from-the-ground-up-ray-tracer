package net.dinkla.raytracer.factories;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 22.04.2010
 * Time: 21:37:51
 * To change this template use File | Settings | File Templates.
 */
public class AbstractFactoryTest {

    class X extends AbstractFactory {

    }

    X x;
    Map map;
    List<String> ls;

    @BeforeMethod
    public void setUp() {
        x = new X();
        map = new HashMap();
        ls = new LinkedList<String>();
    }

    @Test
    public void testNeeds0() {
        X.needs(map, "x", ls);
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testNeeds1() {
        ls.add("a");
        X.needs(map, "x", ls);
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testNeeds2() {
        ls.add("a");
        ls.add("b");
        X.needs(map, "x", ls);
    }

    @Test
    public void testNeeds3() {
        ls.add("a");
        ls.add("b");
        map.put("a", "x");
        map.put("b", "x");
        X.needs(map, "x", ls);
    }

}
