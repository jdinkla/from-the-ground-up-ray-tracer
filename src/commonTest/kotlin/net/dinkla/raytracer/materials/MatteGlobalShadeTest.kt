package net.dinkla.raytracer.materials

import io.kotest.core.spec.style.StringSpec
import net.dinkla.raytracer.Fixture.Ex
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.lights.Ambient
import net.dinkla.raytracer.lights.AreaLight
import net.dinkla.raytracer.lights.ILightSource
import net.dinkla.raytracer.lights.Light
import net.dinkla.raytracer.math.MathUtils.INV_PI
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.objects.IGeometricObject
import net.dinkla.raytracer.shouldBeApprox
import net.dinkla.raytracer.tracers.Tracer
import net.dinkla.raytracer.world.IWorld

/**
 * Tests [Matte.globalShade] (TASK-48, Suffern ch. 26, Listing 26.7). The hybrid global tracer computes
 * **direct** illumination by sampling the lights only at the first hit (`depth == 0`) and uses path
 * tracing for the **indirect** bounce at every depth (Fig 26.11). So:
 *
 *  - at `depth == 0`: `globalShade == areaLightShade + pathShade` (direct + indirect);
 *  - at `depth > 0`: `globalShade == pathShade` (indirect only — no direct double-counting).
 *
 * The scene is built like [MatteAreaLightShadeTest] so the direct term is hand-computable — but the
 * global tracer's direct term ([Matte.globalShade]'s `globalDirect`) deliberately differs from
 * [Matte.areaLightShade] in two ways: it omits the ambient term and it uses the **light's** emitted
 * radiance (`light.getLightMaterial().getLe`), not the receiving surface's `getLe`. The world's tracer
 * returns a fixed `incoming` so the indirect term collapses to `cd * kd * incoming` (the cosine-weighted
 * invariant proven in [MattePathShadeTest]). Both terms are therefore deterministic despite the random
 * diffuse sampling.
 */
internal class MatteGlobalShadeTest :
    StringSpec({

        // Source one unit straight above the origin; normal faces back down toward the surface.
        val samplePoint = Point3D(0.0, 1.0, 0.0)
        val sourceNormal = Normal.DOWN

        fun fakeSource(): ILightSource =
            object : ILightSource {
                override fun sample(): Point3D = samplePoint

                override fun pdf(sr: IShade): Double = 1.0

                override fun getNormal(p: Point3D): Normal = sourceNormal

                override fun getLightMaterial(): IMaterial = throw UnsupportedOperationException("not used")
            }

        fun fakeShade(
            matte: Matte,
            atDepth: Int,
        ): IShade =
            object : IShade {
                override var ray: Ray = Ray(Point3D.ORIGIN, Vector3D(0.0, 0.0, -1.0))
                override val hitPoint: Point3D = Point3D.ORIGIN
                override var depth: Int = atDepth
                override val material: IMaterial = matte
                override var normal: Normal = Normal.UP
                override var t: Double = 0.0
                override var geometricObject: IGeometricObject? = null
            }

        fun fakeWorld(
            theLights: List<Light>,
            incoming: Color,
        ): IWorld =
            object : IWorld {
                override var tracer: Tracer? =
                    object : Tracer {
                        override fun trace(
                            ray: Ray,
                            depth: Int,
                        ): Color = incoming
                    }
                override val lights: List<Light> = theLights
                override val ambientLight: Ambient = Ambient(ls = 1.0, color = Color.WHITE)
                override var backgroundColor: Color = Color.BLACK

                override fun hit(
                    ray: Ray,
                    sr: IHit,
                ): Boolean = false

                override fun inShadow(
                    ray: Ray,
                    sr: IShade,
                    d: Double,
                ): Boolean = false

                override fun shouldStopRecursion(depth: Int): Boolean = true
            }

        val incoming = Color(0.4, 0.6, 0.8)

        // The light panel's emitted radiance is its material's getLe = ce*ls.
        val lightLe = Color(0.5, 0.7, 0.9) * 2.0

        fun emissiveLight(): AreaLight =
            AreaLight(shadows = true).apply {
                source = fakeSource()
                material = Emissive(ce = Color(0.5, 0.7, 0.9), ls = 2.0)
            }

        // The direct (global) term has no ambient and uses the LIGHT's radiance:
        //   per sample f = cd*(kd*INV_PI), le = lightLe, nDotWi = 1, G/pdf = 1.
        val direct = (Ex.cd * (Ex.kd * INV_PI)) * lightLe

        // The indirect (path) term collapses to cd*kd*incoming for cosine-weighted diffuse sampling.
        val indirect = Ex.cd * Ex.kd * incoming

        "at depth 0 returns the direct light-sampled term (no ambient) plus the indirect path bounce" {
            val matte = Matte(Ex.cd, Ex.ka, Ex.kd)
            val sr = fakeShade(matte, atDepth = 0)
            val world = fakeWorld(listOf(emissiveLight()), incoming)

            val result = matte.globalShade(world, sr)

            result shouldBeApprox (direct + indirect)
        }

        "at a deeper bounce returns only the indirect path term (no direct double-counting)" {
            val matte = Matte(Ex.cd, Ex.ka, Ex.kd)
            val sr = fakeShade(matte, atDepth = 1)
            val world = fakeWorld(listOf(emissiveLight()), incoming)

            val result = matte.globalShade(world, sr)

            result shouldBeApprox indirect
        }
    })
