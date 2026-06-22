package net.dinkla.raytracer.textures

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Point3D

class Checker3DTest :
    StringSpec({
        val checker = Checker3D(size = 1.0, color1 = Color.WHITE, color2 = Color.BLACK)

        "the cell at the origin uses colour1" {
            // floor(x)+floor(y)+floor(z) = 0 (even) -> colour1
            checker.getColor(testShade(Point3D(0.2, 0.3, 0.4))) shouldBe Color.WHITE
        }

        "stepping one cell along x flips to colour2" {
            // floor sums to 1 (odd) -> colour2
            checker.getColor(testShade(Point3D(1.2, 0.3, 0.4))) shouldBe Color.BLACK
        }

        "stepping one cell along x and one along y flips back to colour1" {
            // floor sums to 2 (even) -> colour1
            checker.getColor(testShade(Point3D(1.2, 1.3, 0.4))) shouldBe Color.WHITE
        }

        "a larger cell size groups neighbouring points into the same cell" {
            val coarse = Checker3D(size = 2.0, color1 = Color.WHITE, color2 = Color.BLACK)

            // Both points fall in cell floor(x/2)=0 along x, so both are colour1.
            coarse.getColor(testShade(Point3D(0.5, 0.3, 0.4))) shouldBe Color.WHITE
            coarse.getColor(testShade(Point3D(1.5, 0.3, 0.4))) shouldBe Color.WHITE
        }
    })
