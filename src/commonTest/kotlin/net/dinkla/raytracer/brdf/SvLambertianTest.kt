package net.dinkla.raytracer.brdf

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import net.dinkla.raytracer.Fixture.Ex
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.math.MathUtils.INV_PI
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.objects.IGeometricObject
import net.dinkla.raytracer.shouldBeApprox
import net.dinkla.raytracer.textures.Checker3D
import net.dinkla.raytracer.textures.ConstantColor

/**
 * Covers [SvLambertian]'s texture-driven reflectance: [f] is `cd(hit) * kd / PI` and [rho] is
 * `cd(hit) * kd`, where the diffuse colour is read from the [Texture] at the hit point. With a
 * [ConstantColor] texture both reduce to the constant-colour [Lambertian] values; with a varying
 * texture they differ between hit points. [sampleF] is unsupported, and `kd` must lie in `[0,1]`.
 */
internal class SvLambertianTest :
    StringSpec({

        fun shadeAt(hit: Point3D): IShade =
            object : IShade {
                override var ray: Ray = Ray(Point3D.ORIGIN, Vector3D(0.0, 0.0, -1.0))
                override val hitPoint: Point3D = hit
                override val localHitPoint: Point3D = hit
                override var depth: Int = 0
                override val material: IMaterial? = null
                override var normal: Normal = Normal.UP
                override var t: Double = 0.0
                override var geometricObject: IGeometricObject? = null
            }

        val wo = Vector3D(0.0, 0.0, 1.0)
        val wi = Vector3D(0.0, 1.0, 0.0)

        "f is cd*kd/PI from the constant texture colour" {
            val brdf = SvLambertian(kd = Ex.kd, cd = ConstantColor(Ex.cd))

            brdf.f(shadeAt(Point3D.ORIGIN), wo, wi) shouldBeApprox (Ex.cd * (Ex.kd * INV_PI))
        }

        "rho is cd*kd from the constant texture colour" {
            val brdf = SvLambertian(kd = Ex.kd, cd = ConstantColor(Ex.cd))

            brdf.rho(shadeAt(Point3D.ORIGIN), wo) shouldBeApprox (Ex.cd * Ex.kd)
        }

        "rho follows the texture across different hit points" {
            val brdf = SvLambertian(kd = Ex.kd, cd = Checker3D(size = 1.0, color1 = Color.WHITE, color2 = Color.RED))

            val inWhiteCell = brdf.rho(shadeAt(Point3D(0.3, 0.0, 0.3)), wo)
            val inRedCell = brdf.rho(shadeAt(Point3D(1.3, 0.0, 0.3)), wo)

            inWhiteCell shouldNotBe inRedCell
        }

        "a diffuse coefficient outside [0,1] is rejected" {
            shouldThrow<IllegalArgumentException> {
                SvLambertian(kd = 1.5, cd = ConstantColor(Ex.cd))
            }
        }

        "two SvLambertians with the same parameters are equal" {
            val a = SvLambertian(kd = Ex.kd, cd = ConstantColor(Ex.cd))
            val b = SvLambertian(kd = Ex.kd, cd = ConstantColor(Ex.cd))

            a shouldBe b
            a.hashCode() shouldBe b.hashCode()
        }
    })
