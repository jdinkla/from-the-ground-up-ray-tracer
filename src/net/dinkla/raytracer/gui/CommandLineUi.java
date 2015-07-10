package net.dinkla.raytracer.gui;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.colors.RGBColor;
import net.dinkla.raytracer.colors.RGBColor2;
import net.dinkla.raytracer.films.IFilm;
import net.dinkla.raytracer.films.PngFilm;
import net.dinkla.raytracer.objects.acceleration.kdtree.InnerNode;
import net.dinkla.raytracer.utilities.Counter;
import net.dinkla.raytracer.worlds.World;
import net.dinkla.raytracer.worlds.WorldBuilder;
import org.apache.log4j.Logger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 19.05.2010
 * Time: 22:52:09
 * To change this template use File | Settings | File Templates.
 */
public class CommandLineUi {

    static final Logger LOGGER = Logger.getLogger(CommandLineUi.class);

    public static void render(String fileNameIn, String fileNameOut) {
        LOGGER.info("Rendering " + fileNameIn + " to " + fileNameOut);

        Counter.PAUSE = false;
        Color.black = RGBColor.BLACK;
        Color.error = RGBColor.RED;
        Color.white = RGBColor.WHITE;

        World w = new World<RGBColor2>();
        WorldBuilder builder = new WorldBuilder<RGBColor2>(w);
        builder.build(new File(fileNameIn));
        w.initialize();

        PngFilm png = new PngFilm(fileNameOut);
        png.initialize(1, w.getViewPlane().resolution);
        w.render((IFilm) png);
        png.finish();
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            throw new RuntimeException("CommandLineUI expects input filename and output filename as arguments");
        }
        String fileNameIn = args[0];
        String fileNameOut = args[1];
        render(fileNameIn, fileNameOut);
    }


}
