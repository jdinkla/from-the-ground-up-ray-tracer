package net.dinkla.raytracer.colors;

import net.dinkla.raytracer.colors.RGBColor;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 11.04.2010
 * Time: 23:04:17
 * To change this template use File | Settings | File Templates.
 */
public class RGBColorTest {

    @Test
    public void asInt() throws Exception {
        RGBColor c1 = new RGBColor(0, 0, 1);
        assertEquals(c1.asInt(), 255);
    }

    @Test
    public void createFromInt() {
        float r = 3 / 255.0f;
        float g = 31 / 255.0f;
        float b = 139 / 255.0f;
        int rgb = new RGBColor(r, g, b).asInt();
        RGBColor c = (RGBColor) RGBColor.WHITE.createFromInt(rgb);
        assertEquals(c.red, r);
        assertEquals(c.green, g);
        assertEquals(c.blue, b);
    }
}
