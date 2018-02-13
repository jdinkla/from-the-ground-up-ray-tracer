package net.dinkla.raytracer.objects.utilities

import net.dinkla.raytracer.TestUtils
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.assertEquals

class PlyFileTest {

    @Test
    fun readHeaderAscii() {
        val plyFile = PlyFile(TestUtils.PLY_EXAMPLE)
        assertEquals(plyFile.numVertices, 4)
        assertEquals(plyFile.numFaces, 2)
        assertEquals(plyFile.vertexProperties.size, 3)
        assertEquals(plyFile.facesProperties.size, 1)
        assertEquals(plyFile.format, "ascii")
        assertEquals(plyFile.formatVersion, "1.0")
        assertEquals(plyFile.vertexDataLength, 12)
    }

    @Test
    fun readHeaderBinary() {
        val plyFile = PlyFile(TestUtils.PLY_BINARY_EXAMPLE)
        assertEquals(plyFile.numVertices, 46912)
        assertEquals(plyFile.numFaces, 93820)
        assertEquals(plyFile.vertexProperties.size, 4)
        assertEquals(plyFile.facesProperties.size, 1)
        assertEquals(plyFile.format, "binary_big_endian")
        assertEquals(plyFile.formatVersion, "1.0")
        assertEquals(plyFile.vertexDataLength, 16)
    }

    @Test
    fun readBinary() {
        val plyFile = PlyFile(TestUtils.PLY_BINARY_EXAMPLE)
        plyFile.read()
        //System.out.println("test");
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
