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
 * Covers [SvPhong]'s area-light path ([areaLightShade]), the direct-shade branches not already
 * pinned by [SvPhongShadeTest] (shadows=false skip, back-facing normal, in-shadow), the emitted
 * radiance ([getLe] = `cs*ks`) and the value-type contract ([equals]/[hashCode]/[toString]).
 *
 * The ambient term is exactly `cd * ka`; every lit case is asserted strictly brighter than that
 * ambient floor, while every unlit case collapses back to it. For the lit area-light case `ks`/`cs`
 * are set non-zero so the source's emitted radiance `getLe = cs*ks` is non-zero (otherwise the
 * area-light contribution would vanish).
 */
internal class SvPhongTest :
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

        fun downwardSourceAbove(): ILightSource =
            object : ILightSource {
                override fun sample(): Point3D = Point3D(0.0, 1.0, 0.0)

                override fun pdf(sr: IShade): Double = 1.0

                override fun getNormal(p: Point3D): Normal = Normal.DOWN

                override fun getLightMaterial(): IMaterial = throw UnsupportedOperationException("not used")
            }

        // direct shade branches not already covered by SvPhongShadeTest ------

        "shade returns only ambient when the light is behind the surface" {
            val svPhong = SvPhong(texture(), Ex.ka, Ex.kd)
            val lit = world(listOf(PointLight(Point3D(0.0, 5.0, 0.0), 1.0, Color.WHITE)))

            val result = svPhong.shade(lit, shade(svPhong, Normal.DOWN))

            result shouldBeApprox ambient
        }

        "shade returns only ambient when the hit point is in shadow" {
            val svPhong = SvPhong(texture(), Ex.ka, Ex.kd)
            val lit = world(listOf(PointLight(Point3D(0.0, 5.0, 0.0), 1.0, Color.WHITE)), shadowed = true)

            val result = svPhong.shade(lit, shade(svPhong))

            result shouldBeApprox ambient
        }

        "shade skips the shadow test for a light that casts no shadows" {
            val svPhong = SvPhong(texture(), Ex.ka, Ex.kd)
            val noShadowLight = PointLight(Point3D(0.0, 5.0, 0.0), 1.0, Color.WHITE, shadows = false)
            val lit = world(listOf(noShadowLight), shadowed = true)

            val result = svPhong.shade(lit, shade(svPhong))

            result.red shouldBeGreaterThan ambient.red
        }

        // area-light shade ----------------------------------------------------

        "area light shade adds the averaged diffuse-plus-specular contribution to ambient when lit" {
            // ks/cs non-zero so the area light's emitted radiance getLe = cs*ks is non-zero.
            val svPhong = SvPhong(texture(), Ex.ka, Ex.kd).apply { ks = Ex.ks; cs = Ex.cs; exp = Ex.exp }
            val light = AreaLight(shadows = true).apply { source = downwardSourceAbove() }

            val result = svPhong.areaLightShade(world(listOf(light)), shade(svPhong))

            result.red shouldBeGreaterThan ambient.red
            result.green shouldBeGreaterThan ambient.green
            result.blue shouldBeGreaterThan ambient.blue
        }

        "area light shade returns only ambient when every sample is shadowed" {
            val svPhong = SvPhong(texture(), Ex.ka, Ex.kd).apply { ks = Ex.ks; cs = Ex.cs }
            val light = AreaLight(shadows = true).apply { source = downwardSourceAbove() }

            val result = svPhong.areaLightShade(world(listOf(light), shadowed = true), shade(svPhong))

            result shouldBeApprox ambient
        }

        "area light shade ignores non-area lights" {
            val svPhong = SvPhong(texture(), Ex.ka, Ex.kd)
            val pointLight = PointLight(Point3D(0.0, 5.0, 0.0), 1.0, Color.WHITE)

            val result = svPhong.areaLightShade(world(listOf(pointLight)), shade(svPhong))

            result shouldBeApprox ambient
        }

        "area light shade drops samples that face away from the surface" {
            val svPhong = SvPhong(texture(), Ex.ka, Ex.kd).apply { ks = Ex.ks; cs = Ex.cs }
            val light = AreaLight(shadows = true).apply { source = downwardSourceAbove() }

            val result = svPhong.areaLightShade(world(listOf(light)), shade(svPhong, Normal.DOWN))

            result shouldBeApprox ambient
        }

        // emitted radiance & value-type contract ------------------------------

        "getLe returns the specular reflectance cs times ks" {
            val svPhong = SvPhong(texture(), Ex.ka, Ex.kd).apply { ks = Ex.ks; cs = Ex.cs }

            svPhong.getLe(shade(svPhong)) shouldBeApprox (Ex.cs * Ex.ks)
        }

        "two SvPhongs with the same parameters are equal and share a hash code" {
            val a = SvPhong(texture(), Ex.ka, Ex.kd).apply { ks = Ex.ks; cs = Ex.cs; exp = Ex.exp }
            val b = SvPhong(texture(), Ex.ka, Ex.kd).apply { ks = Ex.ks; cs = Ex.cs; exp = Ex.exp }

            a shouldBe b
            a.hashCode() shouldBe b.hashCode()
        }

        "SvPhongs differing in a specular parameter are not equal" {
            val a = SvPhong(texture(), Ex.ka, Ex.kd).apply { ks = Ex.ks }
            val b = SvPhong(texture(), Ex.ka, Ex.kd).apply { ks = Ex.ks + 0.1 }

            a shouldNotBe b
        }

        "an SvPhong is not equal to null or to a non-SvPhong value" {
            val svPhong = SvPhong(texture(), Ex.ka, Ex.kd)

            svPhong shouldNotBe null
            (svPhong.equals("not a material")) shouldBe false
        }

        "toString names the material" {
            SvPhong(texture(), Ex.ka, Ex.kd).toString() shouldContain "SvPhong"
        }
    })
