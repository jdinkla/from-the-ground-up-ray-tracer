package net.dinkla.raytracer.materials

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import net.dinkla.raytracer.Fixture.Ex
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.lights.Ambient
import net.dinkla.raytracer.lights.AreaLight
import net.dinkla.raytracer.lights.ILightSource
import net.dinkla.raytracer.lights.Light
import net.dinkla.raytracer.lights.PointLight
import net.dinkla.raytracer.math.MathUtils.INV_PI
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.objects.IGeometricObject
import net.dinkla.raytracer.shouldBeApprox
import net.dinkla.raytracer.textures.ConstantColor
import net.dinkla.raytracer.tracers.Tracer
import net.dinkla.raytracer.world.IWorld

/**
 * Covers [SvMatte]'s area-light path ([areaLightShade]), the direct-shade branches not already
 * pinned by [SvMatteShadeTest] (shadows=false skip, back-facing normal, in-shadow), the emitted
 * radiance ([getLe]) and the value-type contract ([equals]/[hashCode]/[toString]).
 *
 * The area-light scene mirrors [MatteAreaLightShadeTest] but drives the diffuse colour through a
 * [ConstantColor] texture, so the per-sample contribution still collapses to a hand-computable
 * value: every sampled direction is `wi = (0,1,0)`, pdf = 1, distance^2 = 1, source nDotD = 1.
 */
internal class SvMatteTest :
    StringSpec({

        val ambient = Ex.cd * Ex.ka

        fun texture() = ConstantColor(Ex.cd)

        fun shade(
            material: IMaterial,
            normal: Normal = Normal.UP,
        ): IShade =
            object : IShade {
                override var ray: Ray = Ray(Point3D(0.0, 0.0, 5.0), Vector3D(0.0, 0.0, -1.0))
                override val hitPoint: Point3D = Point3D.ORIGIN
                override val localHitPoint: Point3D = Point3D.ORIGIN
                override var depth: Int = 0
                override val material: IMaterial = material
                override var normal: Normal = normal
                override var t: Double = 0.0
                override var geometricObject: IGeometricObject? = null
            }

        fun world(
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

        // Source one unit straight above the origin; normal faces back down toward the surface.
        fun downwardSourceAbove(): ILightSource =
            object : ILightSource {
                override fun sample(): Point3D = Point3D(0.0, 1.0, 0.0)

                override fun pdf(sr: IShade): Double = 1.0

                override fun getNormal(p: Point3D): Normal = Normal.DOWN

                override fun getLightMaterial(): IMaterial = throw UnsupportedOperationException("not used")
            }

        // direct shade branches not already covered by SvMatteShadeTest ------

        "shade returns only ambient when the light is behind the surface" {
            val svMatte = SvMatte(texture(), Ex.ka, Ex.kd)
            val lit = world(listOf(PointLight(Point3D(0.0, 5.0, 0.0), 1.0, Color.WHITE)))

            // normal points away from the light, so nDotWi <= 0 and no diffuse is added.
            val result = svMatte.shade(lit, shade(svMatte, Normal.DOWN))

            result shouldBeApprox ambient
        }

        "shade returns only ambient when the hit point is in shadow" {
            val svMatte = SvMatte(texture(), Ex.ka, Ex.kd)
            val lit = world(listOf(PointLight(Point3D(0.0, 5.0, 0.0), 1.0, Color.WHITE)), shadowed = true)

            val result = svMatte.shade(lit, shade(svMatte))

            result shouldBeApprox ambient
        }

        "shade skips the shadow test for a light that casts no shadows" {
            val svMatte = SvMatte(texture(), Ex.ka, Ex.kd)
            // shadows = false => the shadow branch is never taken even though the world is "shadowed".
            val noShadowLight = PointLight(Point3D(0.0, 5.0, 0.0), 1.0, Color.WHITE, shadows = false)
            val lit = world(listOf(noShadowLight), shadowed = true)

            val result = svMatte.shade(lit, shade(svMatte))

            result.red shouldBeGreaterThan ambient.red
        }

        // area-light shade ----------------------------------------------------

        "area light shade sums ambient and the averaged diffuse contribution" {
            val svMatte = SvMatte(texture(), Ex.ka, Ex.kd)
            // TASK-54: Le is the LIGHT emitter's own getLe = ce*ls = (0.4,0.5,0.6)*2 = (0.8,1.0,1.2),
            // not the receiver SvMatte's getLe (cd*kd). Chosen distinct so this pins the emitter source.
            val emitter = Emissive(Color(0.4, 0.5, 0.6), ls = 2.0)
            val emitterLe = Color(0.4, 0.5, 0.6) * 2.0
            val light = AreaLight(shadows = true).apply { source = downwardSourceAbove(); material = emitter }

            val result = svMatte.areaLightShade(world(listOf(light)), shade(svMatte))

            // per sample: f = cd*(kd*INV_PI); Le = emitterLe; nDotWi = 1; G/pdf = 1
            val f = Ex.cd * (Ex.kd * INV_PI)
            result shouldBeApprox (ambient + (f * emitterLe))
        }

        "area light shade returns only ambient when every sample is shadowed" {
            val svMatte = SvMatte(texture(), Ex.ka, Ex.kd)
            val light = AreaLight(shadows = true).apply { source = downwardSourceAbove() }

            val result = svMatte.areaLightShade(world(listOf(light), shadowed = true), shade(svMatte))

            result shouldBeApprox ambient
        }

        "area light shade ignores non-area lights" {
            val svMatte = SvMatte(texture(), Ex.ka, Ex.kd)
            val nonAreaLight = PointLight(Point3D(0.0, 5.0, 0.0), 1.0, Color.WHITE)

            val result = svMatte.areaLightShade(world(listOf(nonAreaLight)), shade(svMatte))

            result shouldBeApprox ambient
        }

        "area light shade drops samples whose direction faces away from the surface" {
            val svMatte = SvMatte(texture(), Ex.ka, Ex.kd)
            val light = AreaLight(shadows = true).apply { source = downwardSourceAbove() }

            val result = svMatte.areaLightShade(world(listOf(light)), shade(svMatte, Normal.DOWN))

            result shouldBeApprox ambient
        }

        // emitted radiance & value-type contract ------------------------------

        "getLe returns the diffuse reflectance cd*kd" {
            val svMatte = SvMatte(texture(), Ex.ka, Ex.kd)

            svMatte.getLe(shade(svMatte)) shouldBeApprox (Ex.cd * Ex.kd)
        }

        "two SvMattes with the same parameters are equal and share a hash code" {
            val a = SvMatte(texture(), Ex.ka, Ex.kd)
            val b = SvMatte(texture(), Ex.ka, Ex.kd)

            a shouldBe b
            a.hashCode() shouldBe b.hashCode()
        }

        "SvMattes differing in kd are not equal" {
            val a = SvMatte(texture(), Ex.ka, Ex.kd)
            val b = SvMatte(texture(), Ex.ka, Ex.kd + 0.1)

            a shouldNotBe b
        }

        "an SvMatte is not equal to null or to a non-SvMatte value" {
            val svMatte = SvMatte(texture(), Ex.ka, Ex.kd)

            svMatte shouldNotBe null
            (svMatte.equals("not a material")) shouldBe false
        }

        "toString names the material" {
            SvMatte(texture(), Ex.ka, Ex.kd).toString() shouldContain "SvMatte"
        }
    })
