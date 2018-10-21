package net.dinkla.raytracer.examples

import net.dinkla.raytracer.ViewPlane
import net.dinkla.raytracer.cameras.Camera
import net.dinkla.raytracer.cameras.lenses.Pinhole
import net.dinkla.raytracer.cameras.render.SequentialRenderer
import net.dinkla.raytracer.cameras.render.SimpleRenderer
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.lights.Ambient
import net.dinkla.raytracer.lights.PointLight
import net.dinkla.raytracer.materials.Matte
import net.dinkla.raytracer.materials.Phong
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.objects.Plane
import net.dinkla.raytracer.objects.Sphere
import net.dinkla.raytracer.utilities.Resolution
import net.dinkla.raytracer.worlds.World

object WithoutDslWorld10 {

    fun create(): World {

        val viewPlane = ViewPlane()
        viewPlane.resolution = Resolution.RESOLUTION_1440

        val w = World()

        val lens = Pinhole(viewPlane)
        lens.d = 8000.0

        val sr = SimpleRenderer(lens, w.tracer)
        val sr2 = SequentialRenderer(sr, viewPlane)
        w.camera = Camera(lens, sr2)

        val ambientLight = Ambient()
        ambientLight.ls = 0.5
        w.ambientLight = ambientLight

        w.lights = listOf(
                PointLight(Point3D(100, 50, 150), 3.14),
                PointLight(Point3D(-100, 50, -30), 1.641, Color(0.9, 0.0, 0.0)),
                PointLight(Point3D(400, 180, -200), 2.641, Color(0.0, 0.9, 1.0))

        )

        val materials = mapOf(
                "m1" to Phong(Color(1.0, 1.0, 0.0), ka = 0.25, kd = 0.65, exp = 25.0, ks = 1.0),
                "m2" to Phong(Color(0.71, 0.40, 0.16), ka = 0.25, kd = 0.65, exp = 1.0, ks = 0.1),
                "m3" to Phong(Color(0.5, 0.5, 0.5), ka = 0.25, kd = 0.55, exp = 15.0, ks = 0.9),
                "m4" to Matte(Color(0.5, 0.5, 0.99), ka = 0.1, kd = 0.5)
        )

        val s1 = Sphere(Point3D(10, -5, 0), 27.0)
        s1.material = materials["m1"]

        val s2 = Sphere(Point3D(-30, 15, -50), 27.0)
        s2.material = materials["m2"]

        val p1 = Plane(Point3D(0, -100, 0))
        p1.material = materials["m3"]

        val p2 = Plane(Point3D(0, 100, 0), Normal(0, -1, 0))
        p2.material = materials["m4"]

        val objs = listOf(s1, s2)

        w.add(objs)

        return w
    }
}