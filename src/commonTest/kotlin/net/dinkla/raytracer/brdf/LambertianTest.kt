package net.dinkla.raytracer.brdf

import io.kotest.core.spec.style.StringSpec
import net.dinkla.raytracer.Fixture.Ex
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
 * Covers [Lambertian]'s direction-independent reflectance: the BRDF value [f] is the constant
 * `cd * kd / PI` and the bi-hemispherical reflectance [rho] is `cd * kd`. (Its cosine-weighted
 * [sampleF] is covered separately by [LambertianSampleFTest].)
 */
internal class LambertianTest :
    StringSpec({

        fun shade(): IShade =
            object : IShade {
                override var ray: Ray = Ray(Point3D.ORIGIN, Vector3D(0.0, 0.0, -1.0))
                override val hitPoint: Point3D = Point3D.ORIGIN
                override var depth: Int = 0
                override val material: IMaterial? = null
                override var normal: Normal = Normal.UP
                override var t: Double = 0.0
                override var geometricObject: IGeometricObject? = null
            }

        val wo = Vector3D(0.0, 0.0, 1.0)
        val wi = Vector3D(0.0, 1.0, 0.0)

        "f is the constant diffuse reflectance cd*kd/PI regardless of direction" {
            val brdf = Lambertian(Ex.kd, Ex.cd)

            brdf.f(shade(), wo, wi) shouldBeApprox (Ex.cd * (Ex.kd * INV_PI))
        }

        "rho is the bi-hemispherical reflectance cd*kd" {
            val brdf = Lambertian(Ex.kd, Ex.cd)

            brdf.rho(shade(), wo) shouldBeApprox (Ex.cd * Ex.kd)
        }
    })
