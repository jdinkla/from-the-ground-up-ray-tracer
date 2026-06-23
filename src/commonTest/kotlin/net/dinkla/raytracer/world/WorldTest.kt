package net.dinkla.raytracer.world

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.shouldBeApprox

/**
 * Integration-level tests for [World] assembled through the [Builder] DSL (the spec permits the DSL
 * for world-assembly tests). They pin the delegation of `hit`/`inShadow` to the world's compound and
 * the recursion-depth threshold of `shouldStopRecursion`.
 */
internal class WorldTest :
    StringSpec({

        // A unit sphere at the origin, ready to be intersected.
        fun sceneWithUnitSphere(): World {
            val world =
                Builder.build {
                    camera(d = 1.0, eye = p(0, 0, 5), lookAt = p(0, 0, 0), up = Vector3D.UP)
                    materials {
                        matte(id = "white", cd = Color.WHITE)
                    }
                    objects {
                        sphere(material = "white", center = Point3D.ORIGIN, radius = 1.0)
                    }
                }
            world.initialize()
            return world
        }

        "hit returns true and records the struck object for a ray through the unit sphere" {
            val world = sceneWithUnitSphere()
            val ray = Ray(Point3D(0.0, 0.0, 5.0), Vector3D(0.0, 0.0, -1.0))
            val sr = Shade()

            val hit = world.hit(ray, sr)

            hit shouldBe true
            // The nearest intersection of the unit sphere is at z = 1, i.e. 4 units from the origin.
            sr.t shouldBeApprox 4.0
            (sr.geometricObject != null) shouldBe true
        }

        "hit returns false for a ray that misses every object" {
            val world = sceneWithUnitSphere()
            // Parallel to -z but offset far in +x, so it never reaches the unit sphere.
            val ray = Ray(Point3D(10.0, 0.0, 5.0), Vector3D(0.0, 0.0, -1.0))
            val sr = Shade()

            world.hit(ray, sr) shouldBe false
        }

        "inShadow reports an occluder between the shading point and the light" {
            val world = sceneWithUnitSphere()
            // A shadow ray from behind the sphere aimed at it; the light distance is large enough that
            // the sphere lies between the origin of the ray and the light.
            val ray = Ray(Point3D(0.0, 0.0, 5.0), Vector3D(0.0, 0.0, -1.0))
            val sr = Shade()

            world.inShadow(ray, sr, d = 100.0) shouldBe true
        }

        "inShadow reports no occluder when the ray misses every object" {
            val world = sceneWithUnitSphere()
            val ray = Ray(Point3D(10.0, 0.0, 5.0), Vector3D(0.0, 0.0, -1.0))
            val sr = Shade()

            world.inShadow(ray, sr, d = 100.0) shouldBe false
        }

        "shouldStopRecursion is false up to the maximal recursion depth and true beyond it" {
            val world = sceneWithUnitSphere()
            val max = world.viewPlane.maximalRecursionDepth

            world.shouldStopRecursion(max) shouldBe false
            world.shouldStopRecursion(max + 1) shouldBe true
        }
    })
