package net.dinkla.raytracer.materials

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldNotBe
import net.dinkla.raytracer.Fixture.Ex
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.lights.Ambient
import net.dinkla.raytracer.lights.Light
import net.dinkla.raytracer.lights.PointLight
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.objects.IGeometricObject
import net.dinkla.raytracer.shouldBeApprox
import net.dinkla.raytracer.textures.Checker3D
import net.dinkla.raytracer.textures.ConstantColor
import net.dinkla.raytracer.tracers.Tracer
import net.dinkla.raytracer.world.IWorld

/**
 * [SvMatte] must shade exactly like [Matte] when its texture is a [ConstantColor] of the same
 * colour — that pins the texture-driven diffuse path against the established constant-colour path —
 * and must produce different colours at different hit points when the texture varies in space.
 */
internal class SvMatteShadeTest :
    StringSpec({

        fun shadeAt(
            material: IMaterial,
            hit: Point3D,
        ): Color {
            val sr =
                object : IShade {
                    override var ray: Ray = Ray(Point3D(0.0, 0.0, 5.0), Vector3D(0.0, 0.0, -1.0))
                    override val hitPoint: Point3D = hit
                    override val localHitPoint: Point3D = hit
                    override var depth: Int = 0
                    override val material: IMaterial = material
                    override var normal: Normal = Normal.UP
                    override var t: Double = 0.0
                    override var geometricObject: IGeometricObject? = null
                }
            return material.shade(world(listOf(PointLight(Point3D(0.0, 5.0, 0.0), 1.0, Color.WHITE))), sr)
        }

        "shades identically to Matte when the texture is a constant colour" {
            val cd = Ex.cd
            val matte = Matte(cd, Ex.ka, Ex.kd)
            val svMatte = SvMatte(ConstantColor(cd), Ex.ka, Ex.kd)

            val matteColor = shadeAt(matte, Point3D.ORIGIN)
            val svColor = shadeAt(svMatte, Point3D.ORIGIN)

            svColor shouldBeApprox matteColor
        }

        "produces different colours at hit points that fall in different checker cells" {
            val svMatte = SvMatte(Checker3D(size = 1.0, color1 = Color.WHITE, color2 = Color.RED), Ex.ka, Ex.kd)

            val inWhiteCell = shadeAt(svMatte, Point3D(0.3, 0.0, 0.3))
            val inRedCell = shadeAt(svMatte, Point3D(1.3, 0.0, 0.3))

            inWhiteCell shouldNotBe inRedCell
        }
    })

private fun world(theLights: List<Light>): IWorld =
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
        ): Boolean = false

        override fun shouldStopRecursion(depth: Int): Boolean = true
    }
