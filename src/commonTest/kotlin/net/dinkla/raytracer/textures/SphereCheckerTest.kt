package net.dinkla.raytracer.textures

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Point3D

class SphereCheckerTest :
    StringSpec({
        val checker =
            SphereChecker(
                numHorizontalCheckers = 4,
                numVerticalCheckers = 2,
                color1 = Color.WHITE,
                color2 = Color.BLACK,
            )

        "the first cell (low u, low v) uses colour1" {
            // ix = int(0.1*4) = 0, iz = int(0.1*2) = 0, sum even -> colour1
            checker.colorAt(0.1, 0.1) shouldBe Color.WHITE
        }

        "stepping one horizontal band flips to colour2" {
            // ix = int(0.3*4) = 1, iz = int(0.1*2) = 0, sum odd -> colour2
            checker.colorAt(0.3, 0.1) shouldBe Color.BLACK
        }

        "stepping one horizontal and one vertical band flips back to colour1" {
            // ix = int(0.3*4) = 1, iz = int(0.6*2) = 1, sum even -> colour1
            checker.colorAt(0.3, 0.6) shouldBe Color.WHITE
        }

        "two u values straddling a band boundary get different colours" {
            checker.colorAt(0.24, 0.1) shouldBe Color.WHITE
            checker.colorAt(0.26, 0.1) shouldBe Color.BLACK
        }

        "a u value just past a band boundary is painted the grout line colour" {
            val grouted =
                SphereChecker(
                    numHorizontalCheckers = 4,
                    numVerticalCheckers = 2,
                    lineWidth = 0.05,
                    color1 = Color.WHITE,
                    color2 = Color.BLACK,
                    lineColor = Color.RED,
                )

            // u*4 = 1.02 -> fractional 0.02 < lineWidth 0.05 -> line colour
            grouted.colorAt(0.255, 0.1) shouldBe Color.RED
            // u*4 = 1.5 -> mid-cell, ordinary checker colour (ix=1, iz=0, odd) -> colour2
            grouted.colorAt(0.375, 0.1) shouldBe Color.BLACK
        }

        "a hit point on the +x axis maps to colour1 via lat/long coordinates" {
            // d=(1,0,0): u=0.25, v=0.5 -> ix=1, iz=1, sum even -> colour1
            checker.getColor(testShade(Point3D(1.0, 0.0, 0.0))) shouldBe Color.WHITE
        }
    })
