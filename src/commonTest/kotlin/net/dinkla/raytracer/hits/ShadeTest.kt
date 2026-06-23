package net.dinkla.raytracer.hits

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import net.dinkla.raytracer.materials.Matte
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.objects.Sphere
import net.dinkla.raytracer.shouldBeApprox

class ShadeTest :
    StringSpec({

        // material delegates to geometricObject?.material; with no struck object the safe-call yields
        // null (the null branch of the elvis-free safe call).
        "material is null when no geometric object has been recorded" {
            val shade = Shade()

            shade.material.shouldBeNull()
        }

        // The non-null branch: once a struck object carrying a material is recorded, material returns it.
        "material returns the struck object's material when one is recorded" {
            val matte = Matte()
            val sphere = Sphere(Point3D.ORIGIN, 1.0, matte)
            val shade = Shade()
            shade.geometricObject = sphere

            shade.material shouldBe matte
        }

        // A struck object without a material still resolves through the safe call to null.
        "material is null when the struck object has no material" {
            val sphere = Sphere(Point3D.ORIGIN, 1.0)
            val shade = Shade()
            shade.geometricObject = sphere

            shade.material.shouldBeNull()
        }

        "hitPoint is the point along the ray at distance t" {
            val shade = Shade()
            shade.ray = Ray(Point3D(0.0, 0.0, 0.0), Vector3D(1.0, 0.0, 0.0))
            shade.t = 3.0

            shade.hitPoint shouldBeApprox Point3D(3.0, 0.0, 0.0)
            shade.localHitPoint shouldBeApprox Point3D(3.0, 0.0, 0.0)
        }

        "toString includes the recursion depth" {
            val shade = Shade()
            shade.depth = 2

            shade.toString() shouldContain "2"
        }
    })
