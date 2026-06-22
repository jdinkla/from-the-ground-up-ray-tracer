package net.dinkla.raytracer.mappings

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.math.Point3D

/**
 * The light-probe map folds a direction onto an angular-map image. Texels are hand-derived from
 * beta = acos(z), r = (beta/pi)/sqrt(x^2+y^2), u = (1 + r*x)/2, v = (1 + r*y)/2.
 */
class LightProbeTest :
    StringSpec({
        val hres = 9
        val vres = 9

        "the forward direction +z maps to the image centre" {
            // beta = acos(1) = 0 -> r = 0 -> u = v = 0.5 -> centre texel (4,4)
            LightProbe().getTexelCoordinates(Point3D(0.0, 0.0, 1.0), hres, vres) shouldBe Texel(4, 4)
        }

        "the +x direction in the regular probe maps right of centre on the middle row" {
            // beta = acos(0) = pi/2, d = 1, r = 0.5; u = 0.75, v = 0.5
            // column = floor(8*0.75) = 6, row = floor(8*(1-0.5)) = 4
            LightProbe().getTexelCoordinates(Point3D(1.0, 0.0, 0.0), hres, vres) shouldBe Texel(4, 6)
        }

        "the panoramic probe sends the backward direction -z to the centre" {
            // panoramic uses beta = acos(-z); for z = -1, beta = acos(1) = 0 -> centre
            val panoramic = LightProbe(LightProbe.MapType.PANORAMIC)

            panoramic.getTexelCoordinates(Point3D(0.0, 0.0, -1.0), hres, vres) shouldBe Texel(4, 4)
        }
    })
