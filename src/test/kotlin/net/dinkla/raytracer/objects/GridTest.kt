package net.dinkla.raytracer.objects

import net.dinkla.raytracer.objects.acceleration.Grid
import org.junit.jupiter.api.Test

class GridTest {

    @Test
    @Throws(Exception::class)
    fun testInitialize() {

        val g = Grid()

        val s = Sphere(1.0)

        g.add(s)

        g.initialize()

        //System.out.println(g.cells);
    }

}
