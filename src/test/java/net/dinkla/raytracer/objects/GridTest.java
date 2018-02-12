package net.dinkla.raytracer.objects;

import net.dinkla.raytracer.objects.acceleration.Grid;
import org.junit.jupiter.api.Test;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 14.05.2010
 * Time: 21:13:42
 * To change this template use File | Settings | File Templates.
 */
public class GridTest {

    @Test
    public void testInitialize() throws Exception {

        Grid g = new Grid();

        Sphere s = new Sphere(1.0);

        g.add(s);

        g.initialize();

        //System.out.println(g.cells);
    }
    
}
