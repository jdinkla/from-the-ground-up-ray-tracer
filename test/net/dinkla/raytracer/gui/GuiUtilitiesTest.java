package net.dinkla.raytracer.gui;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 24.06.2010
 * Time: 18:06:41
 * To change this template use File | Settings | File Templates.
 */
public class GuiUtilitiesTest {

    @Test
    public void pngOutputFileName() {
        String out = GuiUtilities.getOutputPngFileName("World73.groovy");
        assertEquals(out.substring(out.length()-11), "World73.png");
        String out2 = GuiUtilities.getOutputPngFileName("ABC.World73.groovy");
        assertEquals(out.substring(out.length()-11), "World73.png");
    }
}
