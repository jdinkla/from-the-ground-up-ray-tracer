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
    })
