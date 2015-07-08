package net.dinkla.raytracer.objects.utilities;

import net.dinkla.raytracer.TestUtils;
import net.dinkla.raytracer.objects.acceleration.Grid;
import net.dinkla.raytracer.objects.mesh.Mesh;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 14.06.2010
 * Time: 09:20:05
 * To change this template use File | Settings | File Templates.
 */
public class BinaryPlyReaderTest {

    @Test
    public void ReadFlat() {
        Mesh mesh = new Mesh();
        Grid grid = new Grid(mesh);
        BinaryPlyReader.read(grid, TestUtils.PLY_EXAMPLE, false, false);
        //System.out.println("mesh=" + grid.getMesh());
        assertEquals(grid.getMesh().vertices.size(), 4);
        assertEquals(grid.size(), 2);
    }

    @Test
    public void ReadSmooth() {
        Mesh mesh = new Mesh();
        Grid grid = new Grid(mesh);
        BinaryPlyReader.read(grid, TestUtils.PLY_EXAMPLE, false, true);
        //System.out.println("mesh=" + grid.getMesh());
        assertEquals(grid.getMesh().vertices.size(), 4);
        assertEquals(grid.size(), 2);
    }

}
