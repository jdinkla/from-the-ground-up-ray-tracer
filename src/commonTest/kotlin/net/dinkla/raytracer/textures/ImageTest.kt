package net.dinkla.raytracer.textures

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.colors.Color

/**
 * The in-memory raster [ImageTexture] samples. The behaviour that matters is `getColor`'s per-axis
 * clamping (an out-of-range row or column must coerce to the nearest edge pixel, not throw or wrap),
 * and the constructor's dimension/size invariants. A small 3x2 raster with a distinct colour per
 * texel makes each clamp direction observable.
 */
class ImageTest :
    StringSpec({
        // A 3-wide, 2-tall raster (hres=3, vres=2); row 0 at the top.
        //   (0,0)=R (0,1)=G (0,2)=B
        //   (1,0)=W (1,1)=Y (1,2)=BLACK
        val image =
            Image.of(hres = 3, vres = 2) { row, column ->
                when (row to column) {
                    0 to 0 -> Color.RED
                    0 to 1 -> Color.GREEN
                    0 to 2 -> Color.BLUE
                    1 to 0 -> Color.WHITE
                    1 to 1 -> Color.YELLOW
                    else -> Color.BLACK
                }
            }

        "an in-bounds coordinate returns that exact pixel" {
            image.getColor(row = 0, column = 1) shouldBe Color.GREEN
            image.getColor(row = 1, column = 2) shouldBe Color.BLACK
        }

        "a negative row clamps to the top edge" {
            // row -3 -> 0; column 2 stays -> top-right (0,2) = blue.
            image.getColor(row = -3, column = 2) shouldBe Color.BLUE
        }

        "a row past the bottom clamps to the bottom edge" {
            // row 99 -> vres-1 = 1; column 0 stays -> bottom-left (1,0) = white.
            image.getColor(row = 99, column = 0) shouldBe Color.WHITE
        }

        "a negative column clamps to the left edge" {
            // column -7 -> 0; row 1 stays -> (1,0) = white.
            image.getColor(row = 1, column = -7) shouldBe Color.WHITE
        }

        "a column past the right edge clamps to the right edge" {
            // column 50 -> hres-1 = 2; row 0 stays -> (0,2) = blue.
            image.getColor(row = 0, column = 50) shouldBe Color.BLUE
        }

        "both coordinates out of range clamp to the diagonal corner" {
            // row -1 -> 0, column 99 -> 2 -> (0,2) = blue.
            image.getColor(row = -1, column = 99) shouldBe Color.BLUE
        }

        "the of factory lays pixels out in row-major order from the top" {
            // Confirms `of` indexes row = index / hres, column = index % hres.
            image.getColor(row = 0, column = 0) shouldBe Color.RED
            image.getColor(row = 1, column = 1) shouldBe Color.YELLOW
        }

        "a non-positive width is rejected" {
            shouldThrow<IllegalArgumentException> {
                Image(hres = 0, vres = 1, pixels = arrayOf(Color.RED))
            }
        }

        "a non-positive height is rejected" {
            shouldThrow<IllegalArgumentException> {
                Image(hres = 1, vres = 0, pixels = arrayOf(Color.RED))
            }
        }

        "a pixel array whose size does not match the dimensions is rejected" {
            shouldThrow<IllegalArgumentException> {
                // 2x2 needs 4 pixels; only 3 supplied.
                Image(hres = 2, vres = 2, pixels = arrayOf(Color.RED, Color.GREEN, Color.BLUE))
            }
        }
    })
