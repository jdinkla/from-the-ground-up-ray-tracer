package net.dinkla.raytracer.examples.lights.area

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.objects.arealights.RectangleLight
import net.dinkla.raytracer.samplers.MultiJittered
import net.dinkla.raytracer.samplers.Sampler
import net.dinkla.raytracer.tracers.Tracers
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

/**
 * Two rectangle area lights of different sizes above four spheres on a floor: the small (warm) light
 * casts crisp shadows, the large (magenta) light casts soft ones, so the two penumbrae contrast
 * directly. Render with the AREA tracer. Formerly World60.kt.
 */
object TwoAreaLightsAndSpheres : WorldDefinition {
    override val id: String = "TwoAreaLightsAndSpheres.kt"

    private const val NUM_SAMPLES = 16

    private val sampler1 =
        Sampler(MultiJittered, 2500, 100).apply {
            mapSamplesToUnitDisk()
        }

    override fun world(): World =
        Builder.build {
            metadata {
                description("Two area lights (small and large) over spheres; use the AREA tracer.")
                preferredTracer(Tracers.AREA)
            }

            camera(d = 1000.0, eye = p(0, 11, 26), lookAt = p(0, 1, -2))

            ambientLight(color = Color.WHITE, ls = 0.4)

            materials {
                phong(id = "green", cd = c(0.1, 0.8, 0.2), ka = 0.25, kd = 0.85, ks = 0.3, exp = 20.0)
                matte(id = "gray", cd = c(0.7, 0.7, 0.7), ka = 0.5, kd = 0.85)
                emissive(id = "warm", ce = c(1.0, 0.8, 0.55), le = 14.0)
                emissive(id = "magenta", ce = c(1.0, 0.2, 1.0), le = 2.5)
            }

            // Small light -> crisp shadows; large light -> soft shadows.
            val small =
                RectangleLight(p0 = p(-5.75, 9.0, -3.0), a = v(1.5, 0.0, 0.0), b = v(0.0, 0.0, 1.5), sampler = sampler1).apply {
                    material = world.materials["warm"]
                }
            val large =
                RectangleLight(p0 = p(1.0, 9.0, -4.0), a = v(8.0, 0.0, 0.0), b = v(0.0, 0.0, 8.0), sampler = sampler1).apply {
                    material = world.materials["magenta"]
                }

            objects {
                plane(material = "gray", point = Point3D.ORIGIN, normal = Normal.UP)
                sphere(center = p(-5, 2, 0), radius = 2.0, material = "green")
                sphere(center = p(5, 2, 0), radius = 2.0, material = "green")
                sphere(center = p(-2, 2, -7), radius = 2.0, material = "green")
                sphere(center = p(3, 2, -7), radius = 2.0, material = "green")
            }

            lights {
                areaLight(of = small, numSamples = NUM_SAMPLES)
                areaLight(of = large, numSamples = NUM_SAMPLES)
            }
        }
}
