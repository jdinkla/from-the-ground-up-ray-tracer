package net.dinkla.raytracer.materials

import io.kotest.core.spec.style.StringSpec
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
import net.dinkla.raytracer.textures.ConstantColor
import net.dinkla.raytracer.tracers.Tracer
import net.dinkla.raytracer.world.IWorld

/**
 * [SvPhong] must shade exactly like [Phong] when its diffuse texture is a [ConstantColor] of the same
 * colour: the spatially-varying diffuse term plus the (constant) specular highlight should reproduce
 * Phong's output. This pins the SV-Phong path against the established constant-colour Phong path.
 */
internal class SvPhongShadeTest :
    StringSpec({

        fun shade(material: IMaterial): Color {
            val sr =
                object : IShade {
                    override var ray: Ray = Ray(Point3D(0.0, 0.0, 5.0), Vector3D(0.0, 0.0, -1.0))
                    override val hitPoint: Point3D = Point3D.ORIGIN
                    override val localHitPoint: Point3D = Point3D.ORIGIN
                    override var depth: Int = 0
                    override val material: IMaterial = material
                    override var normal: Normal = Normal.UP
                    override var t: Double = 0.0
                    override var geometricObject: IGeometricObject? = null
                }
            val world =
                object : IWorld {
                    override var tracer: Tracer? = null
                    override val lights: List<Light> = listOf(PointLight(Point3D(0.0, 5.0, 5.0), 1.0, Color.WHITE))
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
            return material.shade(world, sr)
        }

        "shades identically to Phong when the diffuse texture is a constant colour" {
            val cd = Ex.cd
            val phong =
                Phong(cd, Ex.ka, Ex.kd).apply {
                    exp = 5.0
                    ks = 0.25
                    cs = Color.WHITE
                }
            val svPhong =
                SvPhong(ConstantColor(cd), Ex.ka, Ex.kd).apply {
                    exp = 5.0
                    ks = 0.25
                    cs = Color.WHITE
                }

            shade(svPhong) shouldBeApprox shade(phong)
        }
    })
