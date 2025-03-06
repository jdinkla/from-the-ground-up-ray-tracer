package net.dinkla.raytracer.objects

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D

internal class AlignedBoxTest :
    StringSpec({

        val p = Point3D.ORIGIN
        val q = Point3D.UNIT

        "hit" {
            val ab = AlignedBox(p, q)
            val ray = Ray(Point3D(0.5, 0.5, -1.0), Vector3D.FORWARD)
            val sr = Hit()
            val hit = ab.hit(ray, sr)
            hit shouldBe true
            sr.t shouldBe 1.0
        }

        "shadowHit" {
            val ab = AlignedBox(p, q)
            val ray = Ray(Point3D(0.5, 0.5, -1.0), Vector3D.FORWARD)
            val sr = Hit()
            val hit = ab.shadowHit(ray, sr)
            hit shouldBe true
            sr.t shouldBe 1.0
        }
    })
