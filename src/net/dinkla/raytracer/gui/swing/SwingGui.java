package net.dinkla.raytracer.gui.swing;

import net.dinkla.raytracer.ViewPlane;
import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.films.IFilm;
import net.dinkla.raytracer.films.PngFilm;
import net.dinkla.raytracer.gui.GuiUtilities;
import net.dinkla.raytracer.objects.acceleration.kdtree.InnerNode;
import net.dinkla.raytracer.utilities.Counter;
import net.dinkla.raytracer.worlds.World;
import net.dinkla.raytracer.worlds.WorldBuilder;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 14.04.2010
 * Time: 18:50:07
 * To change this template use File | Settings | File Templates.
 * <p/>
 * -server -Dlog4j.configuration=file:log4j.properties -ea -agentlib:hprof=cpu=samples
 *
 * -Dlog4j.configuration=file:log4j.properties -ea -Xms1024M -Xmx1536M -server
 */
public class SwingGui {

    public static void main(String[] args) {
        final World w = new World();
        final WorldBuilder builder = new WorldBuilder(w);
        String fileName = "examples/objects/World30.groovy";

//        String fileName = "examples/materials/reflective/World80.groovy";
        //String fileName = "examples/objects/mesh/World49.groovy";
        //String fileName = "examples/objects/acceleration/grid/World37.groovy";
        //String fileName = "examples/objects/acceleration/kdtree/World76.groovy";
//        String fileName = "examples/objects/World24.groovy";
        //String fileName = "examples/objects/World7.groovy";
//        String fileName = "examples/objects/beveled/World78.groovy";
//        String fileName = "examples/materials/transparent/World71.groovy";
//        String fileName = "examples/textures/World72.groovy";
        //String fileName = "examples/objects/grid/World42.groovy";
//        String fileName = "examples/lights/ambient/World57.groovy";
//        String fileName = "examples/World28.groovy";
//        String fileName = "examples/materials/transparent/World34.groovy";
//        String fileName = "examples/World66.groovy";
//        String fileName = "examples/objects/mesh/World53.groovy";
//        String fileName = "examples/objects/mesh/World73.groovy";
//        String fileName = "examples/objects/acceleration/kdtree/World74.groovy";
//        String fileName = "examples/objects/acceleration/kdtree/World75.groovy";
//        String fileName = "examples/objects/acceleration/kdtree/World78.groovy";
        //String fileName = "examples/objects/acceleration/kdtree/World76.groovy";
        //String fileName = "examples/materials/reflective/World81.groovy";
//        String fileName = "examples/materials/reflective/World82.groovy";

        final File file = new File(fileName); 
        builder.build(file);
        w.initialize();

        final ViewPlane vp = w.getViewPlane();
        final ImageFrame imf = new ImageFrame(vp.getResolution(), true, null);

        w.getCamera().render((IFilm) imf, 0);

        String fileName2 = GuiUtilities.getOutputPngFileName(file.getName());
        PngFilm png = new PngFilm(fileName2, imf.getFilm());
        png.finish();

        imf.repaint();
        imf.finish();

        Counter.stats(30);

        System.out.println("Hits");
        InnerNode.hits.println();
        
        System.out.println("fails");
        InnerNode.fails.println();
    }

}
