package net.dinkla.raytracer.objects.utilities

import net.dinkla.raytracer.PLY_EXAMPLE
import net.dinkla.raytracer.objects.acceleration.Grid
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class BinaryPlyReaderTest {

    @Disabled
    @Test
    fun readFlat() {
        val grid = Grid()
        BinaryPlyReader.read(grid, PLY_EXAMPLE, reverseNormal = false, isSmooth = false)
        //System.out.println("mesh=" + grid.getMesh());
        assertEquals(grid.mesh.vertices.size, 4)
        assertEquals(grid.size(), 2)
    }

    @Disabled
    @Test
    fun readSmooth() {
        val grid = Grid()
        BinaryPlyReader.read(grid, PLY_EXAMPLE, reverseNormal = false, isSmooth = true)
        //System.out.println("mesh=" + grid.getMesh());
        assertEquals(grid.mesh.vertices.size, 4)
        assertEquals(grid.size(), 2)
    }

}
