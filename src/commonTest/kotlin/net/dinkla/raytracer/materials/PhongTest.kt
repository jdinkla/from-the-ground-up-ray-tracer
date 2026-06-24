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
import net.dinkla.raytracer.tracers.Tracer
import net.dinkla.raytracer.world.IWorld

/**
 * Covers [Phong]'s direct ([shade]) and area-light ([areaLightShade]) lighting paths and its
 * value-type contract ([equals]/[hashCode]/[toString]). The scene is arranged so the ambient term is
 * exactly `color * ka`; every lit case is asserted to be strictly brighter than that ambient floor,
 * while every unlit case (light behind, shadowed, no light, no area light) must collapse back to it.
 */
internal class PhongTest :
    StringSpec({

        val ambient = Ex.cd * Ex.ka

        fun shade(
            material: IMaterial,
            normal: Normal = Normal.UP,
        ): IShade =
            object : IShade {
                override var ray: Ray = Ray(Point3D(0.0, 0.0, 5.0), Vector3D(0.0, 0.0, -1.0))
                override val hitPoint: Point3D = Point3D.ORIGIN
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

        // direct shade --------------------------------------------------------

        "shade adds a diffuse-plus-specular highlight on top of ambient when lit from the front" {
            val phong = Phong(Ex.cd, Ex.ka, Ex.kd)
            val lit = world(listOf(PointLight(Point3D(0.0, 5.0, 0.0), 1.0, Color.WHITE)))

            val result = phong.shade(lit, shade(phong))

            result.red shouldBeGreaterThan ambient.red
            result.green shouldBeGreaterThan ambient.green
            result.blue shouldBeGreaterThan ambient.blue
        }

        "shade returns only ambient when the light is behind the surface" {
            val phong = Phong(Ex.cd, Ex.ka, Ex.kd)
            val lit = world(listOf(PointLight(Point3D(0.0, 5.0, 0.0), 1.0, Color.WHITE)))

            // normal points away from the light, so nDotWi <= 0 and no diffuse/specular is added.
            val result = phong.shade(lit, shade(phong, Normal.DOWN))

            result shouldBeApprox ambient
        }

        "shade returns only ambient when the hit point is in shadow" {
            val phong = Phong(Ex.cd, Ex.ka, Ex.kd)
            val lit = world(listOf(PointLight(Point3D(0.0, 5.0, 0.0), 1.0, Color.WHITE)), shadowed = true)

            val result = phong.shade(lit, shade(phong))

            result shouldBeApprox ambient
        }

        "shade skips the shadow test for a light that casts no shadows" {
            val phong = Phong(Ex.cd, Ex.ka, Ex.kd)
            // shadows = false => the shadow branch is never taken even though the world is "shadowed".
            val light = PointLight(Point3D(0.0, 5.0, 0.0), 1.0, Color.WHITE, shadows = false)
            val lit = world(listOf(light), shadowed = true)

            val result = phong.shade(lit, shade(phong))

            result.red shouldBeGreaterThan ambient.red
        }

        "shade returns only ambient when there are no lights" {
            val phong = Phong(Ex.cd, Ex.ka, Ex.kd)

            val result = phong.shade(world(emptyList()), shade(phong))

            result shouldBeApprox ambient
        }

        // area-light shade ----------------------------------------------------

        "area light shade adds the averaged diffuse-plus-specular contribution to ambient when lit" {
            // TASK-54: the incoming radiance is the LIGHT emitter's own getLe (here Emissive() = WHITE),
            // not the receiver Phong's cs*ks; a non-zero emitter is enough to exceed the ambient floor.
            val phong = Phong(Ex.cd, Ex.ka, Ex.kd, Ex.exp, Ex.ks, Ex.cs)
            val light = AreaLight(shadows = true).apply { source = downwardSourceAbove(); material = Emissive() }

            val result = phong.areaLightShade(world(listOf(light)), shade(phong))

            result.red shouldBeGreaterThan ambient.red
            result.green shouldBeGreaterThan ambient.green
            result.blue shouldBeGreaterThan ambient.blue
        }

        "area light shade returns only ambient when every sample is shadowed" {
            val phong = Phong(Ex.cd, Ex.ka, Ex.kd)
            val light = AreaLight(shadows = true).apply { source = downwardSourceAbove() }

            val result = phong.areaLightShade(world(listOf(light), shadowed = true), shade(phong))

            result shouldBeApprox ambient
        }

        "area light shade ignores non-area lights" {
            val phong = Phong(Ex.cd, Ex.ka, Ex.kd)
            val pointLight = PointLight(Point3D(0.0, 5.0, 0.0), 1.0, Color.WHITE)

            val result = phong.areaLightShade(world(listOf(pointLight)), shade(phong))

            result shouldBeApprox ambient
        }

        "area light shade drops samples that face away from the surface" {
            val phong = Phong(Ex.cd, Ex.ka, Ex.kd)
            val light = AreaLight(shadows = true).apply { source = downwardSourceAbove() }

            val result = phong.areaLightShade(world(listOf(light)), shade(phong, Normal.DOWN))

            result shouldBeApprox ambient
        }

        // emitted radiance & value-type contract ------------------------------

        "getLe returns the specular reflectance cs times ks" {
            val phong = Phong(Ex.cd, Ex.ka, Ex.kd).apply { ks = Ex.ks; cs = Ex.cs }

            phong.getLe(shade(phong)) shouldBeApprox (Ex.cs * Ex.ks)
        }

        "two Phongs with the same parameters are equal and share a hash code" {
            val a = Phong(Ex.cd, Ex.ka, Ex.kd).apply { exp = Ex.exp; ks = Ex.ks; cs = Ex.cs }
            val b = Phong(Ex.cd, Ex.ka, Ex.kd).apply { exp = Ex.exp; ks = Ex.ks; cs = Ex.cs }

            a shouldBe b
            a.hashCode() shouldBe b.hashCode()
        }

        "Phongs differing in a specular parameter are not equal" {
            val a = Phong(Ex.cd, Ex.ka, Ex.kd).apply { ks = Ex.ks }
            val b = Phong(Ex.cd, Ex.ka, Ex.kd).apply { ks = Ex.ks + 0.1 }

            a shouldNotBe b
        }

        "a Phong is not equal to null or to a non-Phong value" {
            val phong = Phong(Ex.cd, Ex.ka, Ex.kd)

            phong shouldNotBe null
            (phong.equals("not a material")) shouldBe false
        }

        "toString names the material" {
            Phong(Ex.cd, Ex.ka, Ex.kd).toString() shouldContain "Phong"
        }
    })

/** An [ILightSource] one unit straight above the origin whose normal faces back down at the surface. */
private fun downwardSourceAbove(): ILightSource =
    object : ILightSource {
        override fun sample(): Point3D = Point3D(0.0, 1.0, 0.0)

        override fun pdf(sr: IShade): Double = 1.0

        override fun getNormal(p: Point3D): Normal = Normal.DOWN

        override fun getLightMaterial(): IMaterial = throw UnsupportedOperationException("not used")
    }
