package net.dinkla.raytracer.objects.utilities;

import net.dinkla.raytracer.TestUtils;
import net.dinkla.raytracer.objects.acceleration.Grid;
import net.dinkla.raytracer.objects.mesh.Mesh;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 */
public class PlyFileTest {

    static final String ASCII_PLY_FILE = TestUtils.PLY_EXAMPLE;
    static final String BINARY_PLY_FILE = TestUtils.PLY_BINARY_EXAMPLE;
    
    @Test
    public void readHeaderAscii() {
        PlyFile plyFile = new PlyFile(ASCII_PLY_FILE);
        assertEquals(plyFile.getNumVertices(), 4);
        assertEquals(plyFile.getNumFaces(), 2);
        assertEquals(plyFile.getVertexProperties().size(), 3);
        assertEquals(plyFile.getFacesProperties().size(), 1);
        assertEquals(plyFile.getFormat(), "ascii");
        assertEquals(plyFile.getFormatVersion(), "1.0");
        assertEquals((int) plyFile.getVertexDataLength(), (int) 12);
    }

    @Test
    public void readHeaderBinary() {
        PlyFile plyFile = new PlyFile(BINARY_PLY_FILE);
        assertEquals(plyFile.getNumVertices(), 46912);
        assertEquals(plyFile.getNumFaces(), 93820);
        assertEquals(plyFile.getVertexProperties().size(), 4);
        assertEquals(plyFile.getFacesProperties().size(), 1);
        assertEquals(plyFile.getFormat(), "binary_big_endian");
        assertEquals(plyFile.getFormatVersion(), "1.0");
        assertEquals((int) plyFile.getVertexDataLength(), (int) 16);
    }

    @Test
    public void readBinary() {
        PlyFile plyFile = new PlyFile(BINARY_PLY_FILE);
        plyFile.read();

        System.out.println("test");
    }

//    @Test
//    public void ReadFlat() {
//        Mesh mesh = new Mesh();
//        Grid grid = new Grid(mesh);
//        PlyReader.read(grid, "examples/ply/TwoTriangles.ply", false, false);
//        //System.out.println("mesh=" + grid.getMesh());
//        assertEquals(grid.getMesh().vertices.size(), 4);
//        assertEquals(grid.size(), 2);
//    }
//
//    @Test
//    public void ReadSmooth() {
//        Mesh mesh = new Mesh();
//        Grid grid = new Grid(mesh);
//        PlyReader.read(grid, "examples/ply/TwoTriangles.ply", false, true);
//        //System.out.println("mesh=" + grid.getMesh());
//        assertEquals(grid.getMesh().vertices.size(), 4);
//        assertEquals(grid.size(), 2);
//    }

}
