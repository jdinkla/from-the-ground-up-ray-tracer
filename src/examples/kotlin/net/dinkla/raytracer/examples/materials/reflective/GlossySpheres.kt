package net.dinkla.raytracer.examples.materials.reflective

import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.world.Builder.build
import net.dinkla.raytracer.world.WorldDefinition

/**
 * Glossy spheres — Suffern's *Ray Tracing from the Ground Up*, figure 25.1.
 *
 * Three [glossyReflector][net.dinkla.raytracer.world.dsl.MaterialsScope.glossyReflector] spheres rest
 * on a bright floor: a deep blue and a deep red satin sphere behind, and a more polished champagne
 * sphere in front. Each reflects the floor and its neighbours through a Phong lobe rather than a
 * perfect mirror, so the reflections are blurred — the satin look of the photo. The `exp` exponent
 * sets the glossiness: the lower-`exp` blue/red spheres reflect softly, the higher-`exp` champagne
 * sphere more sharply.
 *
 * Glossy reflection samples one random direction per ray, so the scene opts into multi-sample
 * anti-aliasing with `samples(...)` to average out the per-pixel noise. Render with the Whitted
 * tracer, e.g.:
 *
 * `./gradlew run --args="--world=GlossySpheres.kt --tracer=WHITTED --renderer=FORK_JOIN --resolution=1080p"`
 */
object GlossySpheres : WorldDefinition {
    override val id: String = "GlossySpheres.kt"

    override fun world() =
        build {
            metadata {
                title = "Glossy spheres"
                description = "Three glossy reflecting spheres (Suffern fig. 25.1)"
            }

            camera(d = 1100.0, eye = p(0.4, 2.3, 7.0), lookAt = p(0.0, 0.85, 0.0))

            samples(64)

            ambientLight(color = c(1.0, 1.0, 1.0), ls = 0.35)

            lights {
                pointLight(location = p(8.0, 10.0, 9.0), ls = 2.6)
                pointLight(location = p(-7.0, 6.0, 6.0), ls = 1.1)
            }

            materials {
                // Bright, slightly warm floor — its reflection is what lights up the lower half of each sphere.
                matte(id = "floor", cd = c(0.82, 0.80, 0.76), ka = 0.55, kd = 0.7)

                // Deep blue satin sphere: soft, blurred reflections (low exponent).
                glossyReflector(id = "blue", cd = c(0.04, 0.09, 0.42), ka = 0.3, kd = 0.55, exp = 70.0, kr = 0.55, ks = 0.7)

                // Deep red satin sphere.
                glossyReflector(id = "red", cd = c(0.5, 0.05, 0.05), ka = 0.3, kd = 0.55, exp = 70.0, kr = 0.55, ks = 0.7)

                // Champagne sphere: more polished, sharper reflections (high exponent), warm reflection tint.
                glossyReflector(
                    id = "champagne",
                    cd = c(0.45, 0.42, 0.34),
                    ka = 0.3,
                    kd = 0.45,
                    exp = 900.0,
                    kr = 0.8,
                    cr = c(0.96, 0.93, 0.84),
                    ks = 0.9,
                )
            }

            objects {
                plane(material = "floor", point = Point3D.ORIGIN, normal = Normal.UP)
                sphere(material = "blue", center = p(-1.0, 1.0, 0.0), radius = 1.0)
                sphere(material = "red", center = p(1.0, 1.0, 0.0), radius = 1.0)
                sphere(material = "champagne", center = p(0.0, 0.8, 1.55), radius = 0.8)
            }
        }
}
