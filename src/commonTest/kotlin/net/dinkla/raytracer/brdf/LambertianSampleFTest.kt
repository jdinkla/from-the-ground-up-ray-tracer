package net.dinkla.raytracer.brdf

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.doubles.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.Fixture.Ex
import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.math.MathUtils.INV_PI
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.objects.IGeometricObject
import net.dinkla.raytracer.shouldBeApprox

/**
 * Tests the cosine-weighted hemisphere sampling of [Lambertian.sampleF] (TASK-20, Suffern ch. 26).
 *
 * The contract checked here:
 *  - the sampled direction `wi` always lies in the hemisphere about the normal (`n . wi >= 0`),
 *  - the returned colour is the constant BRDF value `f = cd * kd / PI`,
 *  - the returned pdf equals `(n . wi) / PI` exactly (the cosine-weighting relationship),
 *  - and, statistically, the mean of `n . wi` over many samples approaches the analytic expectation
 *    `E[cos theta] = 2/3` of a cosine-weighted hemisphere. The sample *set* is precomputed and fixed
 *    (only the draw order is randomised), so averaging over many draws is a stable, non-flaky
 *    invariant — the pattern testing.md §9 prescribes for randomised code.
 */
internal class LambertianSampleFTest :
    StringSpec({

        // Shade with the surface normal straight up; sampleF ignores wo, so any ray direction works.
        fun shadeWithNormal(normal: Normal): IShade =
            object : IShade {
                override var ray: Ray = Ray(Point3D.ORIGIN, Vector3D(0.0, 0.0, -1.0))
                override val hitPoint: Point3D = Point3D.ORIGIN
                override var depth: Int = 0
                override val material: IMaterial? = null
                override var normal: Normal = normal
                override var t: Double = 0.0
                override var geometricObject: IGeometricObject? = null
            }

        val brdf = Lambertian(Ex.kd, Ex.cd)
        val wo = Vector3D(0.0, 0.0, 1.0)

        "sampled direction is always in the hemisphere about the normal" {
            val sr = shadeWithNormal(Normal.UP)

            repeat(2000) {
                val sample = brdf.sampleF(sr, wo)
                (sr.normal dot sample.wi) shouldBeGreaterThanOrEqual 0.0
            }
        }

        "sampleF returns the constant Lambertian BRDF value as its colour" {
            val sr = shadeWithNormal(Normal.UP)

            val sample = brdf.sampleF(sr, wo)

            sample.color shouldBeApprox brdf.f
            brdf.f shouldBeApprox (Ex.cd * (Ex.kd * INV_PI))
        }

        "pdf equals cos(theta)/PI for the sampled direction" {
            val sr = shadeWithNormal(Normal.UP)

            repeat(2000) {
                val sample = brdf.sampleF(sr, wo)
                val cosTheta = sr.normal dot sample.wi
                sample.pdf shouldBeApprox (cosTheta * INV_PI)
            }
        }

        "mean cosine over many samples matches the cosine-weighted expectation 2/3" {
            val sr = shadeWithNormal(Normal.UP)
            val n = 20_000

            var sum = 0.0
            repeat(n) { sum += sr.normal dot brdf.sampleF(sr, wo).wi }
            val mean = sum / n

            // E[cos theta] = 2/3 for a cosine-weighted hemisphere. Tolerance 0.05 (looser than the
            // default K_EPSILON) because the draw order is randomised and the fixed sample population
            // of 1000 directions carries a small discretisation bias from its exact value.
            mean shouldBe ((2.0 / 3.0) plusOrMinus 0.05)
        }

        "sampling follows a tilted normal, not a fixed axis" {
            // A sideways normal must still yield directions in its own hemisphere (n . wi >= 0),
            // proving the basis is built from the normal rather than assuming an up axis.
            val sr = shadeWithNormal(Normal.RIGHT)

            repeat(2000) {
                val sample = brdf.sampleF(sr, wo)
                (sr.normal dot sample.wi) shouldBeGreaterThanOrEqual 0.0
            }
        }
    })
