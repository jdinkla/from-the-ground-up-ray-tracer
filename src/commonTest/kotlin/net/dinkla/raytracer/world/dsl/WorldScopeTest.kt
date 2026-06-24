package net.dinkla.raytracer.world.dsl

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import net.dinkla.raytracer.cameras.StereoMode
import net.dinkla.raytracer.cameras.StereoViewing
import net.dinkla.raytracer.cameras.lenses.FishEye
import net.dinkla.raytracer.cameras.lenses.Pinhole
import net.dinkla.raytracer.cameras.lenses.Spherical
import net.dinkla.raytracer.cameras.lenses.ThinLens
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.samplers.Constant
import net.dinkla.raytracer.samplers.Sampler

class WorldScopeTest :
    StringSpec({
        "ambientLight" {
            val scope = WorldScope()
            val old = scope.world.ambientLight
            scope.ambientLight(Color.RED, 0.5)
            scope.world.ambientLight shouldNotBe old
        }

        "ambientOccluder" {
            val scope = WorldScope()
            val old = scope.world.ambientLight
            scope.ambientOccluder(Sampler(Constant()), 10)
            scope.world.ambientLight shouldNotBe old
        }

        // samples(n>0) passes the require guard and sets the view-plane sample count.
        "samples sets the per-pixel sample count for a positive value" {
            val scope = WorldScope()

            scope.samples(8)

            scope.world.viewPlane.numSamples shouldBe 8
        }

        // maxDepth(n) sets the view-plane maximal recursion depth, mirroring the samples(n) path.
        "maxDepth sets the maximal recursion depth for a positive value" {
            val scope = WorldScope()

            scope.maxDepth(12)

            scope.world.viewPlane.maximalRecursionDepth shouldBe 12
        }

        // The default (untouched) recursion depth stays 5, so every existing scene renders byte-identically.
        "a fresh world keeps the default maximal recursion depth of 5" {
            val scope = WorldScope()

            scope.world.viewPlane.maximalRecursionDepth shouldBe 5
        }

        // maxDepth(n<=0) fails the require guard with a descriptive IllegalArgumentException.
        "maxDepth rejects a non-positive depth with a descriptive message" {
            val scope = WorldScope()

            val ex =
                shouldThrow<IllegalArgumentException> {
                    scope.maxDepth(0)
                }

            ex.message shouldContain "maxDepth must be positive"
        }

        // samples(n<=0) fails the require guard with a descriptive IllegalArgumentException
        // (the failure branch of `require(n > 0)`).
        "samples rejects a non-positive count with a descriptive message" {
            val scope = WorldScope()

            val ex =
                shouldThrow<IllegalArgumentException> {
                    scope.samples(0)
                }

            ex.message shouldContain "samples must be positive"
        }

        "the default camera is a pinhole (no depth of field)" {
            val scope = WorldScope()

            scope.world.camera.lens.shouldBeInstanceOf<Pinhole>()
        }

        "thinLensCamera selects a ThinLens with the declared focal-plane distance and aperture radius" {
            val scope = WorldScope()
            val eye = Point3D(2.0, 1.0, 10.0)
            val lookAt = Point3D(2.0, 1.0, 0.0)

            scope.thinLensCamera(
                d = 1000.0,
                f = 74.0,
                lensRadius = 1.5,
                eye = eye,
                lookAt = lookAt,
            )

            scope.world.camera.eye shouldBe eye
            scope.world.camera.lookAt shouldBe lookAt
            val lens = scope.world.camera.lens.shouldBeInstanceOf<ThinLens>()
            lens.d shouldBe 1000.0
            lens.f shouldBe 74.0
            lens.lensRadius shouldBe 1.5
            lens.sampler.shouldNotBeNull()
        }

        "fishEyeCamera selects a FishEye lens with the declared field of view" {
            val scope = WorldScope()
            val eye = Point3D(0.0, 6.0, 0.0)
            val lookAt = Point3D(0.0, 6.0, -1.0)

            scope.fishEyeCamera(maxPsi = 120.0, eye = eye, lookAt = lookAt)

            scope.world.camera.eye shouldBe eye
            scope.world.camera.lookAt shouldBe lookAt
            val lens = scope.world.camera.lens.shouldBeInstanceOf<FishEye>()
            lens.maxPsi shouldBe 120.0
        }

        "sphericalCamera selects a Spherical lens with the declared azimuth and polar half-angles" {
            val scope = WorldScope()
            val eye = Point3D(0.0, 6.0, 0.0)
            val lookAt = Point3D(0.0, 6.0, -1.0)

            scope.sphericalCamera(maxLambda = 180.0, maxPsi = 90.0, eye = eye, lookAt = lookAt)

            scope.world.camera.eye shouldBe eye
            scope.world.camera.lookAt shouldBe lookAt
            val lens = scope.world.camera.lens.shouldBeInstanceOf<Spherical>()
            lens.maxLambda shouldBe 180.0
            lens.maxPsi shouldBe 90.0
        }

        "a fresh world has no stereo camera so the normal single-camera path is used" {
            val scope = WorldScope()

            scope.world.stereoCamera shouldBe null
        }

        "stereoCamera selects a stereo camera with the declared separation, mode and viewing" {
            val scope = WorldScope()
            val eye = Point3D(0.0, 0.0, 0.0)
            val lookAt = Point3D(0.0, 0.0, -1.0)

            scope.stereoCamera(
                eye = eye,
                lookAt = lookAt,
                separation = 4.0,
                mode = StereoMode.TRANSVERSE,
                viewing = StereoViewing.ANAGLYPH,
            )

            val stereo = scope.world.stereoCamera
            stereo.shouldNotBeNull()
            stereo.eye shouldBe eye
            stereo.lookAt shouldBe lookAt
            stereo.separation shouldBe 4.0
            stereo.mode shouldBe StereoMode.TRANSVERSE
            stereo.viewing shouldBe StereoViewing.ANAGLYPH
        }
    })
