package net.dinkla.raytracer.materials

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import net.dinkla.raytracer.Fixture.Ex
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.lights.Ambient
import net.dinkla.raytracer.lights.Light
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.objects.IGeometricObject
import net.dinkla.raytracer.shouldBeApprox
import net.dinkla.raytracer.tracers.Tracer
import net.dinkla.raytracer.world.IWorld

/**
 * Pins [Reflective.shade]: with no lights and zero ambient the Phong direct term is black, so the
 * result is purely the mirror term `cr * kr * tracedColour`. For a perfect white mirror (kr = 1,
 * cr = white) that collapses to exactly the colour returned by the world's tracer, which makes the
 * recursion and the null-tracer fallback hand-checkable.
 */
internal class ReflectiveTest :
    StringSpec({

        // Incident ray hits the up-facing surface at 45 degrees so the reflected direction points
        // up and away (n . wi > 0).
        fun shade(): IShade =
            object : IShade {
                override var ray: Ray = Ray(Point3D(-1.0, 1.0, 0.0), Vector3D(1.0, -1.0, 0.0))
                override val hitPoint: Point3D = Point3D.ORIGIN
                override var depth: Int = 0
                override val material: IMaterial? = null
                override var normal: Normal = Normal.UP
                override var t: Double = 0.0
                override var geometricObject: IGeometricObject? = null
            }

        fun world(theTracer: Tracer?): IWorld =
            object : IWorld {
                override var tracer: Tracer? = theTracer
                override val lights: List<Light> = emptyList()
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

        fun stubTracer(traced: Color): Tracer =
            object : Tracer {
                override fun trace(
                    ray: Ray,
                    depth: Int,
                ): Color = traced
            }

        "shade reflects the traced colour for a perfect white mirror" {
            val reflective = Reflective(Color.WHITE, 0.0, 0.0).apply { kr = 1.0; cr = Color.WHITE }
            val traced = Color(0.3, 0.5, 0.7)

            val result = reflective.shade(world(stubTracer(traced)), shade())

            result shouldBeApprox traced
        }

        "shade falls back to white as the reflected colour when the world has no tracer" {
            val reflective = Reflective(Color.WHITE, 0.0, 0.0).apply { kr = 1.0; cr = Color.WHITE }

            val result = reflective.shade(world(theTracer = null), shade())

            result shouldBeApprox Color.WHITE
        }

        "shade tints the reflection by the mirror colour cr" {
            val reflective = Reflective(Color.WHITE, 0.0, 0.0).apply { kr = 1.0; cr = Color(0.5, 0.5, 0.5) }

            val result = reflective.shade(world(stubTracer(Color.WHITE)), shade())

            result shouldBeApprox Color(0.5, 0.5, 0.5)
        }

        "equality and hashCode follow the Phong base and the reflective BRDF" {
            val a = Reflective(Ex.cd, Ex.ka, Ex.kd).apply { kr = Ex.kr; cr = Ex.cr }
            val b = Reflective(Ex.cd, Ex.ka, Ex.kd).apply { kr = Ex.kr; cr = Ex.cr }

            a shouldBe b
            a.hashCode() shouldBe b.hashCode()
            a shouldNotBe Reflective(Ex.cd, Ex.ka, Ex.kd).apply { kr = Ex.kr + 0.1; cr = Ex.cr }
            a shouldNotBe null
            (a.equals("nope")) shouldBe false
        }

        "toString names the material" {
            Reflective(Ex.cd, Ex.ka, Ex.kd).toString() shouldContain "Reflective"
        }
    })
