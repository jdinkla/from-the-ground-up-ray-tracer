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
 * Characterization tests for [Matte.areaLightShade] (the AREA-lighting path), pinning the exact
 * colour produced before the TASK-11 nesting refactor. The scenario is built so every sampled
 * direction is identical and the geometry terms collapse to 1.0, making the output hand-computable:
 *
 *  - hit point at the origin, surface normal straight up
 *  - the light source always samples the point (0,1,0) one unit above, so wi = (0,1,0)
 *  - the source normal faces down, so nDotD = 1 (> 0 => the light emits its material's Le)
 *  - pdf = 1 and the squared distance = 1, so G/pdf = 1
 *
 * Then per sample T = (diffuse.f * Le) * nDotWi * 1, and with all samples equal the accumulator's
 * average equals T. The expected colour is L_ambient + T.
 */
internal class MatteAreaLightShadeTest :
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

        fun fakeShade(matte: Matte): IShade =
            object : IShade {
                override var ray: Ray = Ray(Point3D.ORIGIN, Vector3D(0.0, 0.0, -1.0))
                override val hitPoint: Point3D = Point3D.ORIGIN
                override var depth: Int = 0
                override val material: IMaterial = matte
                override var normal: Normal = Normal.UP
                override var t: Double = 0.0
                override var geometricObject: IGeometricObject? = null
            }

        fun fakeWorld(
            theLights: List<Light>,
            shadowed: Boolean = false,
        ): IWorld =
            object : IWorld {
                override var tracer: Tracer? = null
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
                ): Boolean = shadowed

                override fun shouldStopRecursion(depth: Int): Boolean = true
            }

        "area light shade sums ambient and the averaged diffuse contribution" {
            val matte = Matte(Ex.cd, Ex.ka, Ex.kd)
            val light = AreaLight(shadows = true).apply { source = fakeSource() }
            val sr = fakeShade(matte)
            val world = fakeWorld(listOf(light))

            val result = matte.areaLightShade(world, sr)

            // L_ambient = (cd*ka) * (white*1.0) = cd*ka
            val ambient = Ex.cd * Ex.ka
            // per sample: f = cd*(kd*INV_PI); Le = cd*kd; nDotWi = 1; G/pdf = 1
            val f = Ex.cd * (Ex.kd * INV_PI)
            val le = Ex.cd * Ex.kd
            val perSample = (f * le)
            result shouldBeApprox (ambient + perSample)
        }

        "area light shade returns only ambient when every sample is shadowed" {
            val matte = Matte(Ex.cd, Ex.ka, Ex.kd)
            val light = AreaLight(shadows = true).apply { source = fakeSource() }
            val sr = fakeShade(matte)
            val world = fakeWorld(listOf(light), shadowed = true)

            val result = matte.areaLightShade(world, sr)

            result shouldBeApprox (Ex.cd * Ex.ka)
        }

        "area light shade ignores non-area lights" {
            val matte = Matte(Ex.cd, Ex.ka, Ex.kd)
            val nonAreaLight = Ambient(ls = 1.0, color = Color.RED)
            val sr = fakeShade(matte)
            val world = fakeWorld(listOf(nonAreaLight))

            val result = matte.areaLightShade(world, sr)

            // No AreaLight => no diffuse contribution, only the ambient term.
            result shouldBeApprox (Ex.cd * Ex.ka)
        }

        // A non-shadowing area light short-circuits the shadow test (isInShadow's `!light.shadows`
        // true branch), so every sample contributes exactly as in the happy path.
        "area light shade skips the shadow test for a non-shadowing light" {
            val matte = Matte(Ex.cd, Ex.ka, Ex.kd)
            val light = AreaLight(shadows = false).apply { source = fakeSource() }
            val sr = fakeShade(matte)
            // shadowed = true would normally darken; with shadows off it must be ignored.
            val world = fakeWorld(listOf(light), shadowed = true)

            val result = matte.areaLightShade(world, sr)

            val ambient = Ex.cd * Ex.ka
            val f = Ex.cd * (Ex.kd * INV_PI)
            val le = Ex.cd * Ex.kd
            val perSample = (f * le)
            result shouldBeApprox (ambient + perSample)
        }

        "area light shade drops samples whose direction faces away from the surface" {
            val matte = Matte(Ex.cd, Ex.ka, Ex.kd)
            // Surface normal points down while the light is above => nDotWi < 0 for every sample.
            val light = AreaLight(shadows = true).apply { source = fakeSource() }
            val sr = fakeShade(matte).apply { normal = Normal.DOWN }
            val world = fakeWorld(listOf(light))

            val result = matte.areaLightShade(world, sr)

            // ambient uses ambientBRDF.rho (direction-independent), so only the ambient term remains.
            result shouldBeApprox (Ex.cd * Ex.ka)
        }
    })
