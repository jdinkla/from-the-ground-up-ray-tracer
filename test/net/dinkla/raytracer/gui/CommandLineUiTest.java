package net.dinkla.raytracer.gui;

import net.dinkla.raytracer.gui.CommandLineUi;
import net.dinkla.raytracer.math.Histogram;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 */
public class CommandLineUiTest {

    @Test
    public void render() {
        String fin = "examples/benchmarks/X2.groovy";
        String fout ="build/X2.png";
        CommandLineUi.render(fin, fout);
    }

}
