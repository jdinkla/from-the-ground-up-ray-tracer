package net.dinkla.raytracer.brdf

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import net.dinkla.raytracer.Fixture.Ex
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.objects.IGeometricObject
import net.dinkla.raytracer.shouldBeApprox

/**
 * Covers [GlossySpecular.f]'s two branches and the unsupported [rho]:
 *
 *  - For an up-facing surface and `wi = (0,1,0)` the mirror direction `r` is the normal itself, so
 *    `r·wo > 0` and `f = cs * ks * (r·wo)^exp`. With `wo` along the normal too, `r·wo = 1`, so the
 *    lobe term is `1^exp = 1` and `f` collapses to `cs * ks`.
 *  - When `wo` points into the surface (`r·wo <= 0`) the highlight vanishes and `f` is BLACK.
 */
internal class GlossySpecularTest :
    StringSpec({

        fun shadeUp(): IShade =
            object : IShade {
                override var ray: Ray = Ray(Point3D.ORIGIN, Vector3D(0.0, 0.0, -1.0))
                override val hitPoint: Point3D = Point3D.ORIGIN
                override var depth: Int = 0
                override val material: IMaterial? = null
                override var normal: Normal = Normal.UP
                override var t: Double = 0.0
                override var geometricObject: IGeometricObject? = null
            }

        "f returns cs*ks at the mirror direction when the viewer is along the normal" {
            val brdf = GlossySpecular(ks = Ex.ks, cs = Ex.cs, exp = Ex.exp)
            val wi = Vector3D(0.0, 1.0, 0.0)
            val wo = Vector3D(0.0, 1.0, 0.0)

            // r = (0,1,0) (the normal); r·wo = 1, so the lobe term 1^exp = 1.
            brdf.f(shadeUp(), wo, wi) shouldBeApprox (Ex.cs * Ex.ks)
        }

        "f is brighter at a higher reflection coefficient" {
            val low = GlossySpecular(ks = 0.2, cs = Color.WHITE, exp = Ex.exp)
            val high = GlossySpecular(ks = 0.8, cs = Color.WHITE, exp = Ex.exp)
            val wi = Vector3D(0.0, 1.0, 0.0)
            val wo = Vector3D(0.0, 1.0, 0.0)

            high.f(shadeUp(), wo, wi).red shouldBeGreaterThan low.f(shadeUp(), wo, wi).red
        }

        "f is black when the viewer faces into the surface (r·wo <= 0)" {
            val brdf = GlossySpecular(ks = Ex.ks, cs = Ex.cs, exp = Ex.exp)
            val wi = Vector3D(0.0, 1.0, 0.0)
            // r = (0,1,0); wo pointing down gives r·wo = -1 <= 0.
            val wo = Vector3D(0.0, -1.0, 0.0)

            brdf.f(shadeUp(), wo, wi) shouldBeApprox Color.BLACK
        }

        "two GlossySpeculars with the same reflectance are equal and share a hash code" {
            val a = GlossySpecular(ks = Ex.ks, cs = Ex.cs, exp = Ex.exp)
            val b = GlossySpecular(ks = Ex.ks, cs = Ex.cs, exp = Ex.exp)

            a shouldBe b
            a.hashCode() shouldBe b.hashCode()
        }

        "GlossySpeculars differing in the exponent are not equal" {
            val a = GlossySpecular(ks = Ex.ks, cs = Ex.cs, exp = Ex.exp)
            val b = GlossySpecular(ks = Ex.ks, cs = Ex.cs, exp = Ex.exp + 1.0)

            a shouldNotBe b
        }

        "a GlossySpecular is not equal to null or to a non-GlossySpecular value" {
            val a = GlossySpecular()

            a shouldNotBe null
            (a.equals("not a brdf")) shouldBe false
        }
    })
