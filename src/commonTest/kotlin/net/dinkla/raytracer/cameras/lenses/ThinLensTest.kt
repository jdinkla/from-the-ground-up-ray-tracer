package net.dinkla.raytracer.cameras.lenses

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import net.dinkla.raytracer.ViewPlane
import net.dinkla.raytracer.math.Basis
import net.dinkla.raytracer.math.Point2D
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.samplers.UnitDiskSampler
import net.dinkla.raytracer.shouldBeApprox
import kotlin.math.sqrt

/**
 * Proves the thin-lens depth-of-field geometry (Suffern ch. 10). Prior to TASK-26 [ThinLens] was a
 * stub that returned a fixed `(u+v-w)` ray; these tests replace the old stub-characterizing tests and
 * pin the now-correct depth-of-field model: lens samples spread across the aperture yet converge on a
 * single focal point at distance [ThinLens.f], so the focal plane is sharp and other depths blur.
 *
 * Determinism: a hand-written [UnitDiskSampler] fake replays a fixed queue of unit-disk points, so the
 * derived expectations below are exact and the tests are not flaky.
 */
class ThinLensTest :
    StringSpec({
        // Basis comes out exactly axis-aligned: u=(1,0,0), v=(0,1,0), w=(0,0,1), so pm(x,y,z)=(x,y,-z).
        val eye = Point3D.ORIGIN
        val uvw = Basis.create(eye, Point3D(0.0, 0.0, -1.0), Vector3D.UP)

        // Small even view plane so the pixel-centre offsets are trivial: 0.5*width=2, 0.5*height=1.
        fun viewPlane() = ViewPlane().apply { resolution = net.dinkla.raytracer.utilities.Resolution(4, 2) }

        // A fake aperture sampler that replays a fixed queue of unit-disk points (deterministic).
        class QueuedDiskSampler(private val points: List<Point2D>) : UnitDiskSampler {
            private var i = 0

            override fun sampleUnitDisk(): Point2D = points[i++ % points.size]
        }

        val d = 1.0
        val f = 4.0
        val lensRadius = 2.0

        fun thinLens(sampler: UnitDiskSampler? = null) =
            ThinLens(viewPlane(), eye, uvw).apply {
                this.d = d
                this.f = f
                this.lensRadius = lensRadius
                this.sampler = sampler
            }

        // For pixel (r=0, c=4): px = 1*(4 - 2) = 2.0, py = 1*(0 - 1) = -1.0.
        // Focal point F = eye + (px*f/d)*u + (py*f/d)*v - f*w = (8, -4, -4).
        val r = 0
        val c = 4
        val focalPoint = Point3D(8.0, -4.0, -4.0)

        // Parameter where a ray reaches the focal plane (depth z = -f for this basis): origin.z + t*dir.z = -f.
        fun pointOnFocalPlane(ray: net.dinkla.raytracer.math.Ray): Point3D {
            val t = (-f - ray.origin.z) / ray.direction.z
            return ray.linear(t)
        }

        "getRaySingle is the sharp pinhole-equivalent ray from the lens centre through the focal point" {
            val lens = thinLens()

            val ray = lens.getRaySingle(r, c)

            ray.origin shouldBe eye
            ray.direction shouldBeApprox Vector3D(8.0, -4.0, -4.0).normalize()
            pointOnFocalPlane(ray) shouldBeApprox focalPoint
        }

        "getRaySampled directions are normalized" {
            val lens = thinLens(QueuedDiskSampler(listOf(Point2D(0.5, 0.0))))

            val ray = lens.getRaySampled(r, c, Point2D(0.0, 0.0))

            ray.direction.length shouldBeApprox 1.0
        }

        "a disk sample places the ray origin on the lens, scaled by lensRadius along u and v" {
            // unit-disk (0.5, 0.0) -> lens offset (lensRadius*0.5, 0) = (1.0, 0.0) -> origin (1,0,0).
            val lens = thinLens(QueuedDiskSampler(listOf(Point2D(0.5, 0.0))))

            val ray = lens.getRaySampled(r, c, Point2D(0.0, 0.0))

            ray.origin shouldBeApprox Point3D(1.0, 0.0, 0.0)
        }

        "every lens sample for an in-focus pixel converges on the same focal point" {
            // Three different unit-disk points -> three different origins on the aperture.
            val samples = listOf(Point2D(0.5, 0.0), Point2D(0.0, 0.5), Point2D(-0.25, -0.25))
            val lens = thinLens(QueuedDiskSampler(samples))

            val rays = samples.map { lens.getRaySampled(r, c, Point2D(0.0, 0.0)) }

            // Origins are genuinely spread across the lens, not all at the eye.
            rays[0].origin shouldBeApprox Point3D(1.0, 0.0, 0.0)
            rays[1].origin shouldBeApprox Point3D(0.0, 1.0, 0.0)
            rays[2].origin shouldBeApprox Point3D(-0.5, -0.5, 0.0)
            rays[0].origin shouldNotBe rays[1].origin

            // Yet all converge on the focal point at distance f -> the focal plane is sharp.
            rays.forEach { pointOnFocalPlane(it) shouldBeApprox focalPoint }
        }

        "lens-sample origins stay within the aperture radius of the eye in the u/v plane" {
            val samples = listOf(Point2D(0.5, 0.0), Point2D(0.0, 0.5), Point2D(-0.25, -0.25))
            val lens = thinLens(QueuedDiskSampler(samples))

            samples.forEach { _ ->
                val ray = lens.getRaySampled(r, c, Point2D(0.0, 0.0))
                val du = ray.origin.x - eye.x
                val dv = ray.origin.y - eye.y
                sqrt(du * du + dv * dv) shouldBeLessThan lensRadius + 1e-9
            }
        }

        "points off the focal plane get a spread across lens samples (blur)" {
            // Same pixel, two different aperture points.
            val lensA = thinLens(QueuedDiskSampler(listOf(Point2D(0.5, 0.0))))
            val lensB = thinLens(QueuedDiskSampler(listOf(Point2D(-0.5, 0.0))))

            val rayA = lensA.getRaySampled(r, c, Point2D(0.0, 0.0))
            val rayB = lensB.getRaySampled(r, c, Point2D(0.0, 0.0))

            // At the focal plane (z = -f) they coincide (sharp)...
            pointOnFocalPlane(rayA) shouldBeApprox pointOnFocalPlane(rayB)

            // ...but at a different depth (z = -2f) the two rays hit different world points -> blur.
            val depth = -2.0 * f
            val tA = (depth - rayA.origin.z) / rayA.direction.z
            val tB = (depth - rayB.origin.z) / rayB.direction.z
            (rayA.linear(tA) == rayB.linear(tB)) shouldBe false
        }

        "a zero lens radius collapses to a sharp pinhole ray regardless of the disk sample" {
            val lens =
                ThinLens(viewPlane(), eye, uvw).apply {
                    this.d = d
                    this.f = f
                    this.lensRadius = 0.0
                    this.sampler = QueuedDiskSampler(listOf(Point2D(0.9, -0.3)))
                }

            val sampled = lens.getRaySampled(r, c, Point2D(0.0, 0.0))
            val single = lens.getRaySingle(r, c)

            sampled.origin shouldBeApprox eye
            sampled.direction shouldBeApprox single.direction
        }

        "getRaySampled requires a sampler to be set" {
            val lens = thinLens(sampler = null)

            val exception =
                io.kotest.assertions.throwables.shouldThrow<IllegalArgumentException> {
                    lens.getRaySampled(r, c, Point2D(0.0, 0.0))
                }
            exception.message shouldBe "ThinLens.sampler not set; assign a lens sampler before rendering"
        }
    })
