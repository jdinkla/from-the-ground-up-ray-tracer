package net.dinkla.raytracer.worlds;

import net.dinkla.raytracer.lights.AmbientOccluder;
import net.dinkla.raytracer.objects.Plane;
import net.dinkla.raytracer.tracers.AreaLighting;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 20.04.2010
 * Time: 22:14:15
 */
public class WorldBuilderTest {

    static final private String DIR = "examples";

    static private Optional<Path> findExample(final String filename) {
        BiPredicate<Path, BasicFileAttributes> pr = (p, a) -> {
            final String fn = p.getFileName().toString();
            final boolean same = (filename.equals(fn));
            //System.out.println("'" + filename + "' == '" + fn + "' : " + same);
            return same;
        };
        try {
            File f = new File(DIR);
            Stream<Path> ps = Files.find(f.toPath(), 99, pr);
            return ps.findFirst();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    
    @Test
    public void testCreate1() {
        File f = findExample("World20.groovy").get().toFile();
        World w = WorldBuilder.create(f);
        assertEquals(w.size(), 3);
        assertEquals(w.lights.size(), 1);
    }

    @Test
    public void testCreate2() {
        File f = findExample("World7.groovy").get().toFile();
        World w = WorldBuilder.create(f);
        assertEquals(w.size(), 6);
        assertEquals(w.lights.size(), 3);
    }

    @Test
    public void testCreate3() {
        File f = findExample("World14.groovy").get().toFile();
        World w = WorldBuilder.create(f);
        assertEquals(w.size(), 2);
        assertEquals(w.lights.size(), 0);
        assertEquals(AmbientOccluder.class, w.ambientLight.getClass());
    }

    @Test
    public void testCreate4() {
        File f = findExample("World17.groovy").get().toFile();
        World w = WorldBuilder.create(f);
        assertEquals(w.size(), 10);
        assertEquals(w.lights.size(), 2);
    }

    @Test
    public void testCreate5() {
        File f = findExample("World23.groovy").get().toFile();
        World w = WorldBuilder.create(f);
        assertEquals(w.size(), 26);
        assertEquals(w.lights.size(), 1);
        assertEquals(w.getTracer().getClass(), AreaLighting.class);
    }

    @Test
    public void testCreate6() {
        File f = findExample("World26.groovy").get().toFile();
        World w = WorldBuilder.create(f);
        assertEquals(w.size(), 2);
        assertEquals(w.lights.size(), 1);
    }

    @Test
    public void testCreate7() {
        File f = findExample("World34.groovy").get().toFile();
        World w = WorldBuilder.create(f);
        assertNotNull(w.getViewPlane(), "viewPlane == null");
        assertNotNull(w.getCamera(), "camera == null");
        assertNotNull(w.getTracer(), "tracer == null");
        assertEquals(w.size(), 5);
        assertEquals(w.lights.size(), 1);
    }

    @Test
    public void testCreate8() {
        File f = findExample("World38.groovy").get().toFile();
        World w = WorldBuilder.create(f);
        assertEquals(w.size(), 6);
    }
    
}
