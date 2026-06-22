package net.dinkla.raytracer.world.dsl

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import net.dinkla.raytracer.cameras.StereoMode
import net.dinkla.raytracer.cameras.StereoViewing
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
