package net.dinkla.raytracer.materials

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import net.dinkla.raytracer.Fixture.Ex
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.lights.Ambient
import net.dinkla.raytracer.lights.DirectLight
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

// A hand-written direct light whose direction, shadow flag and occlusion are fully controllable, so
// each branch of Matte.shade's per-light loop can be selected deterministically.
private class FakeLight(
    private val direction: Vector3D,
    override val shadows: Boolean,
    private val occluded: Boolean,
) : DirectLight {
    override fun l(
        world: IWorld,
        sr: IShade,
    ): Color = Color.WHITE

    override fun getDirection(sr: IShade): Vector3D = direction

    override fun inShadow(
        world: IWorld,
        ray: Ray,
        sr: IShade,
    ): Boolean = occluded
}

// World with zero ambient (so the ambient term in Matte.shade is exactly black) holding [theLights].
private fun directWorld(theLights: List<Light>): IWorld =
    object : IWorld {
        override var tracer: Tracer? = null
        override val lights: List<Light> = theLights
        override val ambientLight: Ambient = Ambient(ls = 0.0, color = Color.BLACK)
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

// Hit point at the origin, surface normal straight up, incident ray heading -z (so wo points +z).
private fun upwardShade(): IShade =
    object : IShade {
        override var ray: Ray = Ray(Point3D.ORIGIN, Vector3D(0.0, 0.0, -1.0))
        override val hitPoint: Point3D = Point3D.ORIGIN
        override val localHitPoint: Point3D = Point3D.ORIGIN
        override var depth: Int = 0
        override val material: IMaterial? = null
        override var normal: Normal = Normal.UP
        override var t: Double = 0.0
        override var geometricObject: IGeometricObject? = null
    }

internal class MatteTest :
    StringSpec({

        "should detect equality" {
            val matte1 = Matte(Ex.cd, Ex.ka, Ex.kd)
            val matte2 = Matte(Ex.cd, Ex.ka, Ex.kd)
            matte1 shouldBe matte2
        }

        "should detect inequality in color" {
            val matte1 = Matte(Ex.cd, Ex.ka, Ex.kd)
            val matte2 = Matte(Ex.cr, Ex.ka, Ex.kd)
            matte1 shouldNotBe matte2
        }

        "should detect inequality in ka" {
            val matte1 = Matte(Ex.cd, Ex.ka, Ex.kd)
            val matte2 = Matte(Ex.cd, Ex.ka + 0.1, Ex.kd)
            matte1 shouldNotBe matte2
        }

        "should detect inequality in kd" {
            val matte1 = Matte(Ex.cd, Ex.ka, Ex.kd)
            val matte2 = Matte(Ex.cd, Ex.ka, Ex.kd + 0.1)
            matte1 shouldNotBe matte2
        }

        "should get ka from ambient BRDF" {
            val matte1 = Matte(Ex.cd, Ex.ka, Ex.kd)
            matte1.ka shouldBe Ex.ka
        }

        "should get kd from diffuse BRDF" {
            val matte1 = Matte(Ex.cd, Ex.ka, Ex.kd)
            matte1.kd shouldBe Ex.kd
        }

        // shade's per-light loop skips a light whose direction faces away from the surface
        // (nDotWi <= 0): with zero ambient only the black ambient term remains. (Matte.shade L50 false.)
        "shade ignores a light whose direction faces away from the surface" {
            val matte = Matte(Ex.cd, Ex.ka, Ex.kd)
            // Light points straight down while the surface normal points up => nDotWi = -1 <= 0.
            val light = FakeLight(Vector3D(0.0, -1.0, 0.0), shadows = true, occluded = false)

            val result = matte.shade(directWorld(listOf(light)), upwardShade())

            result shouldBeApprox Color.BLACK
        }

        // A non-shadowing light skips the shadow test entirely (Matte.shade L52 false) and contributes
        // its full diffuse term: f = cd*(kd/PI), l = white, nDotWi = 1.
        "shade adds a non-shadowing light's diffuse contribution without a shadow test" {
            val matte = Matte(Ex.cd, Ex.ka, Ex.kd)
            val light = FakeLight(Vector3D(0.0, 1.0, 0.0), shadows = false, occluded = false)

            val result = matte.shade(directWorld(listOf(light)), upwardShade())

            // ambient term is black; per-light term = (f * white) * 1.0 = cd * (kd/PI).
            result shouldBeApprox (Ex.cd * (Ex.kd * INV_PI))
        }

        // A shadowing light that reports the point as occluded contributes nothing (Matte.shade L56
        // false): only the black ambient term remains.
        "shade drops a shadowing light's contribution when the point is in shadow" {
            val matte = Matte(Ex.cd, Ex.ka, Ex.kd)
            val light = FakeLight(Vector3D(0.0, 1.0, 0.0), shadows = true, occluded = true)

            val result = matte.shade(directWorld(listOf(light)), upwardShade())

            result shouldBeApprox Color.BLACK
        }
    })
