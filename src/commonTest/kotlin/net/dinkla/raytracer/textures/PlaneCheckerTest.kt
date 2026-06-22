package net.dinkla.raytracer.textures

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Point3D

class PlaneCheckerTest :
    StringSpec({
        val checker = PlaneChecker(size = 1.0, color1 = Color.WHITE, color2 = Color.BLACK)

        "the cell at the origin uses colour1" {
            // floor(x)+floor(z) = 0 (even) -> colour1
            checker.colorAt(0.5, 0.5) shouldBe Color.WHITE
        }

        "stepping one cell along x flips to colour2" {
            // floor(x)+floor(z) = 1 (odd) -> colour2
            checker.colorAt(1.5, 0.5) shouldBe Color.BLACK
        }

        "stepping one cell along both x and z flips back to colour1" {
            // floor(x)+floor(z) = 2 (even) -> colour1
            checker.colorAt(1.5, 1.5) shouldBe Color.WHITE
        }

        "two points straddling a cell boundary in x get different colours" {
            checker.colorAt(0.9, 0.5) shouldBe Color.WHITE
            checker.colorAt(1.1, 0.5) shouldBe Color.BLACK
        }

        "negative coordinates keep the alternating parity" {
            // floor(-0.5) = -1, floor(0.5) = 0, sum = -1 (odd) -> colour2
            checker.colorAt(-0.5, 0.5) shouldBe Color.BLACK
        }

        "the checker reads the xz coordinates of the local hit point" {
            // y is ignored; (x,z) = (0.5, 0.5) -> colour1
            checker.getColor(testShade(Point3D(0.5, 7.0, 0.5))) shouldBe Color.WHITE
        }

        "a point on a cell boundary is painted the grout line colour" {
            val grouted =
                PlaneChecker(
                    size = 1.0,
                    lineWidth = 0.05,
                    color1 = Color.WHITE,
                    color2 = Color.BLACK,
                    lineColor = Color.RED,
                )

            // x just past the integer boundary at 1.0 (distance 0.02 < lineWidth 0.05) -> line colour
            grouted.colorAt(1.02, 0.5) shouldBe Color.RED
            // well inside the cell -> the ordinary checker colour, not the line
            grouted.colorAt(0.5, 0.5) shouldBe Color.WHITE
        }
    })
