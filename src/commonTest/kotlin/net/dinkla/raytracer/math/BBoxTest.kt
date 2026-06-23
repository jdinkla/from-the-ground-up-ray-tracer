package net.dinkla.raytracer.math

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import net.dinkla.raytracer.math.Point3D.Companion.ORIGIN
import net.dinkla.raytracer.math.Point3D.Companion.UNIT
import net.dinkla.raytracer.shouldBeApprox

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

        // ---- hit() / isHit() : the slab t-interval contract -------------------------------------

        "ray entering the box from outside reports a hit with the correct entry/exit interval" {
            // x-aligned ray from x=-1 toward +x: it enters the front face (x=0) at t=1 and exits the
            // back face (x=1) at t=2; the y/z slabs are unbounded (direction 0, origin inside the box).
            val ray = Ray(Point3D(-1.0, 0.5, 0.5), Vector3D(1.0, 0.0, 0.0))

            val hit = box.hit(ray)

            hit.isHit shouldBe true
            hit.t0 shouldBeApprox 1.0
            hit.t1 shouldBeApprox 2.0
            box.isHit(ray) shouldBe true
        }

        "ray that passes wide of the box reports no hit (t0 > t1)" {
            // origin y=2 lies above the box and the ray travels in +x, so the y slab never overlaps
            // the x slab -> t0 ends up greater than t1.
            val ray = Ray(Point3D(-1.0, 2.0, 0.5), Vector3D(1.0, 0.0, 0.0))

            val hit = box.hit(ray)

            hit.isHit shouldBe false
            box.isHit(ray) shouldBe false
        }

        "ray whose origin is inside the box still reports a hit (negative entry t)" {
            // From the centre travelling +x: the entry plane is behind the origin (t0 = -0.5) and the
            // exit plane ahead (t1 = 0.5); t1 > 0 so the interval is a genuine hit.
            val ray = Ray(Point3D(0.5, 0.5, 0.5), Vector3D(1.0, 0.0, 0.0))

            val hit = box.hit(ray)

            hit.isHit shouldBe true
            hit.t0 shouldBeApprox -0.5
            hit.t1 shouldBeApprox 0.5
        }

        "ray travelling in the negative direction hits the box (swapped axis interval)" {
            // From x=2 toward -x the inverse direction is negative, so the per-axis min/max are
            // swapped: the box is entered at t=1 (back face x=1) and exited at t=2 (front face x=0).
            val ray = Ray(Point3D(2.0, 0.5, 0.5), Vector3D(-1.0, 0.0, 0.0))

            val hit = box.hit(ray)

            hit.isHit shouldBe true
            hit.t0 shouldBeApprox 1.0
            hit.t1 shouldBeApprox 2.0
        }

        "axis-parallel ray running alongside the box but outside its slab misses" {
            // direction is 0 on x and z; origin x = 2 lies outside [0,1] on x, so that axis returns a
            // (-inf,-inf) interval that collapses t1 below t0 -> no hit (the direction==0 reject branch).
            val ray = Ray(Point3D(2.0, 0.5, 0.5), Vector3D(0.0, 1.0, 0.0))

            box.hit(ray).isHit shouldBe false
            box.isHit(ray) shouldBe false
        }

        "axis-parallel ray running through the box hits (direction==0 accept branch)" {
            // direction is 0 on x and z but both those origins lie within [0,1], so those axes are
            // unbounded; the +y motion enters at the bottom face and exits at the top.
            val ray = Ray(Point3D(0.5, -1.0, 0.5), Vector3D(0.0, 1.0, 0.0))

            val hit = box.hit(ray)

            hit.isHit shouldBe true
            hit.t0 shouldBeApprox 1.0
            hit.t1 shouldBeApprox 2.0
        }

        // ---- clipTo() : the contained-vs-intersection branches ----------------------------------

        "clipTo returns the receiver unchanged when it is already contained in the other box" {
            val outer = BBox(Point3D(-1.0, -1.0, -1.0), Point3D(2.0, 2.0, 2.0))

            box.clipTo(outer) shouldBe box
        }

        "clipTo intersects with a partially overlapping box on every axis" {
            // Overlap pushes the min up on x and the max down on y/z, exercising all six max/min picks.
            val other = BBox(Point3D(0.25, -1.0, -1.0), Point3D(2.0, 0.75, 0.6))

            box.clipTo(other) shouldBe BBox(Point3D(0.25, 0.0, 0.0), Point3D(1.0, 0.75, 0.6))
        }

        // ---- the remaining split branches (X right, Y/Z left) -----------------------------------

        "should split right correctly on X axis" {
            box.splitRight(Axis.X, 0.5) shouldBe BBox(Point3D(0.5, 0.0, 0.0), UNIT)
        }

        "should split left correctly on Y axis" {
            box.splitLeft(Axis.Y, 0.5) shouldBe BBox(ORIGIN, Point3D(1.0, 0.5, 1.0))
        }

        "should split left correctly on Z axis" {
            box.splitLeft(Axis.Z, 0.5) shouldBe BBox(ORIGIN, Point3D(1.0, 1.0, 0.5))
        }

        "should split right correctly on Z axis" {
            box.splitRight(Axis.Z, 0.5) shouldBe BBox(Point3D(0.0, 0.0, 0.5), UNIT)
        }

        // ---- companion create() : triangle bounding box (padded by epsilon) ---------------------

        "create builds an epsilon-padded box around three points" {
            val bbox = BBox.create(Point3D(0.0, 0.0, 0.0), Point3D(1.0, 0.0, 0.0), Point3D(0.0, 2.0, 3.0))

            // min/max of the three points, each side pushed out by K_EPSILON
            bbox.p.x shouldBeApprox -MathUtils.K_EPSILON
            bbox.p.y shouldBeApprox -MathUtils.K_EPSILON
            bbox.p.z shouldBeApprox -MathUtils.K_EPSILON
            bbox.q.x shouldBeApprox 1.0 + MathUtils.K_EPSILON
            bbox.q.y shouldBeApprox 2.0 + MathUtils.K_EPSILON
            bbox.q.z shouldBeApprox 3.0 + MathUtils.K_EPSILON
        }
    })
