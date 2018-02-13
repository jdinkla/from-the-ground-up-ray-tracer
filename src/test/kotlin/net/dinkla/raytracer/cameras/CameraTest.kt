package net.dinkla.raytracer.cameras

import net.dinkla.raytracer.ViewPlane
import net.dinkla.raytracer.cameras.lenses.Pinhole
import net.dinkla.raytracer.cameras.render.IRenderer
import net.dinkla.raytracer.cameras.render.SequentialRenderer
import net.dinkla.raytracer.cameras.render.SimpleRenderer
import net.dinkla.raytracer.films.IFilm
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.tracers.Whitted
import net.dinkla.raytracer.worlds.World
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class CameraTest {
    @Test
    @Throws(Exception::class)
    fun testComputeUVW() {
        val viewPlane = ViewPlane()
        val lens = Pinhole(viewPlane)
        val world = World()
        val tracer = Whitted(world)
        val render = SimpleRenderer(lens, tracer)
        val renderer = SequentialRenderer(render, viewPlane)

        val c = object : Camera(lens, renderer as IRenderer) {
            override fun render(film: IFilm, frame: Int) {}
        }

        c.eye = Point3D.ORIGIN
        c.lookAt = Point3D.ORIGIN
        c.up = Vector3D.UP

        // TODO improve
        assertNotNull(c.uvw.u)
        assertNotNull(c.uvw.v)
        assertNotNull(c.uvw.w)
    }
}
