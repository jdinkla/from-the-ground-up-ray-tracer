package net.dinkla.raytracer.textures

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.mappings.Mapping
import net.dinkla.raytracer.mappings.Texel
import net.dinkla.raytracer.math.Point3D

class ImageTextureTest :
    StringSpec({
        // A 2x2 raster with a distinct colour in each texel; rows measured from the top.
        //   (0,0)=red (0,1)=green
        //   (1,0)=blue (1,1)=white
        val image =
            Image.of(hres = 2, vres = 2) { row, column ->
                when (row to column) {
                    0 to 0 -> Color.RED
                    0 to 1 -> Color.GREEN
                    1 to 0 -> Color.BLUE
                    else -> Color.WHITE
                }
            }

        "with a mapping, samples the texel the mapping selects" {
            // A fake mapping that always points at the bottom-left texel (1,0) = blue.
            val mapping =
                object : Mapping {
                    override fun getTexelCoordinates(
                        localHitPoint: Point3D,
                        hres: Int,
                        vres: Int,
                    ): Texel = Texel(row = 1, column = 0)
                }
            val texture = ImageTexture(image, mapping)

            texture.getColor(testShade(Point3D(7.0, 7.0, 7.0))) shouldBe Color.BLUE
        }

        "without a mapping, reads the parametric u,v from the shade record" {
            val texture = ImageTexture(image, mapping = null)

            // u=0 -> column 0; v=1 -> row 0 -> red (top-left)
            texture.getColor(testShade(u = 0.0, v = 1.0)) shouldBe Color.RED
            // u=1 -> column 1; v=0 -> row 1 -> white (bottom-right)
            texture.getColor(testShade(u = 1.0, v = 0.0)) shouldBe Color.WHITE
        }

        "an out-of-range texel is clamped to the nearest edge pixel" {
            image.getColor(row = -5, column = 99) shouldBe Color.GREEN
        }
    })
