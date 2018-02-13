package net.dinkla.raytracer.objects.utilities

import net.dinkla.raytracer.TestUtils
import net.dinkla.raytracer.objects.acceleration.Grid
import net.dinkla.raytracer.objects.mesh.Mesh
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.assertEquals

class PlyReaderTest {

    @Test
    fun ReadFlat() {
        val mesh = Mesh()
        val grid = Grid(mesh)
        PlyReader.read(grid, TestUtils.PLY_EXAMPLE, false, false)
        //System.out.println("mesh=" + grid.getMesh());
        assertEquals(grid.mesh.vertices.size, 4)
        assertEquals(grid.size(), 2)
    }

    @Test
    fun ReadSmooth() {
        val mesh = Mesh()
        val grid = Grid(mesh)
        PlyReader.read(grid, TestUtils.PLY_EXAMPLE, false, true)
        //System.out.println("mesh=" + grid.getMesh());
        assertEquals(grid.mesh.vertices.size, 4)
        assertEquals(grid.size(), 2)
    }

}
