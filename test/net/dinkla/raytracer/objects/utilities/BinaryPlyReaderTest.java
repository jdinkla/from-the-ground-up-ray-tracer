package net.dinkla.raytracer.objects.utilities;

import net.dinkla.raytracer.TestUtils;
import net.dinkla.raytracer.objects.acceleration.Grid;
import net.dinkla.raytracer.objects.mesh.Mesh;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BinaryPlyReaderTest {

//    @Test
    public void ReadFlat() {
        Mesh mesh = new Mesh();
        Grid grid = new Grid(mesh);
        BinaryPlyReader.read(grid, TestUtils.PLY_EXAMPLE, false, false);
        //System.out.println("mesh=" + grid.getMesh());
        assertEquals(grid.getMesh().getVertices().size(), 4);
        assertEquals(grid.size(), 2);
    }

//    @Test
    public void ReadSmooth() {
        Mesh mesh = new Mesh();
        Grid grid = new Grid(mesh);
        BinaryPlyReader.read(grid, TestUtils.PLY_EXAMPLE, false, true);
        //System.out.println("mesh=" + grid.getMesh());
        assertEquals(grid.getMesh().getVertices().size(), 4);
        assertEquals(grid.size(), 2);
    }

}
