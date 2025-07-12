package net.dinkla.raytracer.math

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import net.dinkla.raytracer.math.Point3D.Companion.ORIGIN
import net.dinkla.raytracer.math.Point3D.Companion.UNIT

class BBoxTest :
    StringSpec({

        val p = ORIGIN
        val q = UNIT
        val box = BBox(p, q)

        "should not construct if q < p" {
            shouldThrowAny {
                BBox(UNIT, ORIGIN)
            }
        }

        "should construct if p < q" {
            BBox(p, q) shouldBe BBox(Point3D(0.0, 0.0, 0.0), Point3D(1.0, 1.0, 1.0))
        }

        "should be equal for same bounds" {
            BBox(p, q) shouldBe BBox(ORIGIN, UNIT)
        }

        "should not be equal for different bounds" {
            BBox(p, q) shouldNotBe BBox(p, Point3D(2.0, 2.0, 2.0))
        }

        "should detect point inside with isInside" {
            box.isInside(Point3D(0.5, 0.5, 0.5)) shouldBe true
        }

        "should detect corner points as outside with isInside" {
            box.isInside(p) shouldBe false
            box.isInside(q) shouldBe false
        }

        "should detect point on edge as outside with isInside" {
            box.isInside(Point3D(0.0, 0.5, 0.5)) shouldBe false
            box.isInside(Point3D(1.0, 0.5, 0.5)) shouldBe false
        }

        "should detect point outside with isInside" {
            box.isInside(Point3D(-1.0, 0.0, 0.0)) shouldBe false
            box.isInside(Point3D(1.5, 0.5, 0.5)) shouldBe false
        }

        "should calculate correct volume" {
            box.volume shouldBe 1.0
            val largerBox = BBox(ORIGIN, Point3D(2.0, 3.0, 4.0))
            largerBox.volume shouldBe 24.0
        }

        "should handle degenerate box with zero volume" {
            val degenerate = BBox(p, p)
            degenerate.volume shouldBe 0.0
        }

        "should throw assertion error for invalid bounds" {
            shouldThrowAny {
                BBox(Point3D(1.0, 0.0, 0.0), Point3D(0.0, 1.0, 1.0))
            }
        }

        "should clip to another bbox correctly" {
            val other = BBox(Point3D(0.5, 0.5, 0.5), Point3D(2.0, 2.0, 2.0))
            val clipped = box.clipTo(other)
            clipped shouldBe BBox(Point3D(0.5, 0.5, 0.5), UNIT)
        }

        "should split left correctly on X axis" {
            val split = box.splitLeft(Axis.X, 0.5)
            split shouldBe BBox(ORIGIN, Point3D(0.5, 1.0, 1.0))
        }

        "should split right correctly on Y axis" {
            val split = box.splitRight(Axis.Y, 0.5)
            split shouldBe BBox(Point3D(0.0, 0.5, 0.0), UNIT)
        }
    })
