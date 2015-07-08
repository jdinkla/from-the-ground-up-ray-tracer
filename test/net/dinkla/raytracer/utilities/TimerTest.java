package net.dinkla.raytracer.utilities;

import org.testng.annotations.Test;
import static org.testng.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 14.04.2010
 * Time: 14:23:01
 * To change this template use File | Settings | File Templates.
 */
public class TimerTest {

    @Test
    public void get() throws Exception {
        Timer t = new Timer();

        t.start();
        Thread.sleep(5);
        t.stop();
        final long x = t.getDuration();
        assertTrue(x > 0);

        t.start();
        Thread.sleep(50);
        t.stop();
        final long y = t.getDuration();
        assertTrue(y > 0);

        assertTrue(y > x);

    }

}
