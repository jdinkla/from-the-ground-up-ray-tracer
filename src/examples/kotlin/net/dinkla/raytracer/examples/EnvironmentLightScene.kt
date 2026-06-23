package net.dinkla.raytracer.examples

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.materials.Emissive
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.samplers.PureRandom
import net.dinkla.raytracer.samplers.Sampler
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.WorldDefinition

/**
 * Coverage example (TASK-42) for the [net.dinkla.raytracer.lights.EnvironmentLight] (Suffern ch. 18):
 * a hemispherical light whose incident radiance comes from an [Emissive] material, sampled with a
 * cosine-weighted hemisphere [Sampler]. It softly lights the matte foreground spheres from all
 * directions; a large enclosing dome of the same colour makes the sky directly visible.
 *
 * Needs the WHITTED tracer: `Matte.shade` (Whitted) sums every light in `world.lights`, including this
 * one, whereas `Matte.areaLightShade` (the AREA tracer) only sums `AreaLight`s and would ignore it. So
 * the dome is a plain ambient-lit matte (not an `Emissive`, which would push the audit onto AREA), and
 * the foreground matte spheres carry `ka = 0` so the environment light is their only illumination.
 */
object EnvironmentLightScene : WorldDefinition {
    override val id: String = "EnvironmentLightScene.kt"

    private const val NUM_SAMPLES = 16
    private const val NUM_SETS = 83
    private const val ENV_RADIUS = 100.0
    private const val ENV_INTENSITY = 4.0
    private const val BALL_RADIUS = 1.5

    override fun world() =
        Builder.build {
            metadata {
                id(id)
                description("Use whitted tracer (environment light)")
            }

            camera(d = 1000.0, eye = p(0.0, 2.5, 9.0), lookAt = p(0.0, 1.0, 0.0))
            samples(NUM_SAMPLES)

            // Ambient lights only the sky dome (ka = 1.0); the foreground spheres have ka = 0.0 so they
            // are lit solely by the environment light.
            ambientLight(color = Color.WHITE, ls = 1.0)

            val skyColor = c(0.55, 0.7, 1.0)
            // PureRandom (not MultiJittered/Jittered/Regular) so the hemisphere sample buffer is fully
            // populated for any sample/set count — those sqrt-based generators underfill it (see TASK-31).
            val envSampler =
                Sampler(PureRandom, NUM_SAMPLES, NUM_SETS).apply { mapSamplesToHemiSphere(1.0) }

            // shadows = false: the enclosing emissive dome would otherwise block every hemisphere
            // shadow ray (it is the light source, not an occluder), leaving the scene unlit.
            lights {
                environmentLight(material = Emissive(skyColor, ls = ENV_INTENSITY), sampler = envSampler, shadows = false)
            }

            materials {
                matte(id = "sky", cd = skyColor, ka = 1.0, kd = 0.0)
                matte(id = "m1", cd = c(0.85, 0.3, 0.3), ka = 0.0, kd = 0.9)
                matte(id = "m2", cd = c(0.3, 0.7, 0.45), ka = 0.0, kd = 0.9)
                matte(id = "floor", cd = c(0.8, 0.8, 0.85), ka = 0.0, kd = 0.85)
            }

            objects {
                // The visible sky dome: a concave (inward-facing) sphere, ambient-lit to the sky colour;
                // it is the visible counterpart of the environment light's radiance.
                concaveSphere(material = "sky", center = p(0.0, 0.0, 0.0), radius = ENV_RADIUS)
                plane(material = "floor", point = Point3D.ORIGIN, normal = Normal.UP)
                sphere(material = "m1", center = p(-2.2, BALL_RADIUS, 1.0), radius = BALL_RADIUS)
                sphere(material = "m2", center = p(2.2, BALL_RADIUS, 1.0), radius = BALL_RADIUS)
            }
        }
}
