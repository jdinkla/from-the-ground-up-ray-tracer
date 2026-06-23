package net.dinkla.raytracer.mappings

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.math.Point3D

/**
 * The map sends directions on the unit sphere to (row, column) of an 8x8 image. The expected texels
 * are hand-derived from the latitude/longitude formula: u = phi/2pi (azimuth around y), v = 1 -
 * theta/pi (polar angle from +y), then column = (hres-1)*u, row = (vres-1)*(1-v).
 */
class SphericalMapTest :
    StringSpec({
        val map = SphericalMap()
        val hres = 8
        val vres = 8

        "the north pole (+y) maps to the top row" {
            // theta = 0 -> v = 1 -> row 0; phi = 0 -> column 0
            map.getTexelCoordinates(Point3D(0.0, 1.0, 0.0), hres, vres) shouldBe Texel(0, 0)
        }

        "the south pole (-y) maps to the bottom row" {
            // theta = pi -> v = 0 -> row (vres-1) = 7
            map.getTexelCoordinates(Point3D(0.0, -1.0, 0.0), hres, vres) shouldBe Texel(7, 0)
        }

        "the equatorial direction -z maps to the middle row and column" {
            // theta = pi/2 -> v = 0.5 -> row = floor(7*0.5) = 3
            // phi = atan2(0,-1) = pi -> u = 0.5 -> column = floor(7*0.5) = 3
            map.getTexelCoordinates(Point3D(0.0, 0.0, -1.0), hres, vres) shouldBe Texel(3, 3)
        }

        "the equatorial direction +z maps to the middle row, leftmost column" {
            // phi = atan2(0,1) = 0 -> u = 0 -> column 0
            map.getTexelCoordinates(Point3D(0.0, 0.0, 1.0), hres, vres) shouldBe Texel(3, 0)
        }

        "the equatorial direction -x exercises the negative-azimuth wrap" {
            // phi = atan2(-1, 0) = -pi/2 < 0 -> wrapped to +3pi/2 -> u = 0.75 -> column = floor(7*0.75) = 5.
            // Without the `phi < 0` correction u would be negative and the column would underflow; this
            // pins the wrap branch. theta = pi/2 -> v = 0.5 -> row = floor(7*0.5) = 3.
            map.getTexelCoordinates(Point3D(-1.0, 0.0, 0.0), hres, vres) shouldBe Texel(3, 5)
        }

        "a hit point off the unit sphere is normalised before mapping (radius cancels)" {
            // A point at radius 5 along +x maps to the same texel as the unit +x direction.
            val unit = map.getTexelCoordinates(Point3D(1.0, 0.0, 0.0), hres, vres)
            val scaled = map.getTexelCoordinates(Point3D(5.0, 0.0, 0.0), hres, vres)

            scaled shouldBe unit
        }
    })
