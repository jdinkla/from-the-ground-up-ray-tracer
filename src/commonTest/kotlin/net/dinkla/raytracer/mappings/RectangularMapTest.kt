package net.dinkla.raytracer.mappings

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.math.Point3D

/**
 * The default rectangular map reads x as the column axis and z as the row axis over the rectangle
 * `[-1,1] x [-1,1]`. u = (x+1)/2, v = (z+1)/2, then column = (hres-1)*u, row = (vres-1)*(1-v).
 */
class RectangularMapTest :
    StringSpec({
        val map = RectangularMap()
        val hres = 8
        val vres = 8

        "the rectangle centre maps to the middle texel" {
            // u = v = 0.5 -> column = row = floor(7*0.5) = 3
            map.getTexelCoordinates(Point3D(0.0, 0.0, 0.0), hres, vres) shouldBe Texel(3, 3)
        }

        "the +x +z corner maps to the top-right texel" {
            // u = 1 -> column 7; v = 1 -> row 0
            map.getTexelCoordinates(Point3D(1.0, 0.0, 1.0), hres, vres) shouldBe Texel(0, 7)
        }

        "the -x -z corner maps to the bottom-left texel" {
            // u = 0 -> column 0; v = 0 -> row 7
            map.getTexelCoordinates(Point3D(-1.0, 0.0, -1.0), hres, vres) shouldBe Texel(7, 0)
        }

        "custom axes and extents rescale the mapping" {
            // Map x in [-2,2] to the column and y in [-2,2] to the row.
            val custom =
                RectangularMap(
                    uAxis = RectangularMap.Axis.X,
                    vAxis = RectangularMap.Axis.Y,
                    uExtent = 2.0,
                    vExtent = 2.0,
                )

            // x = 2 -> u = 1 -> column 7; y = -2 -> v = 0 -> row 7
            custom.getTexelCoordinates(Point3D(2.0, -2.0, 0.0), hres, vres) shouldBe Texel(7, 7)
        }
    })
