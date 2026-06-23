package net.dinkla.raytracer.textures

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.colors.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 * JVM image decoding. The sampling math is tested in `commonMain`; here the focus is the I/O seam:
 * a decoded [BufferedImage] becomes an [Image] with row 0 at the top and the right dimensions, a
 * real file round-trips through `ImageIO`, and a missing file fails loudly. Rather than depend on a
 * checked-in resource (none exists) or the working directory, the file test writes a PNG to a temp
 * file and reads it straight back, which is deterministic and self-contained.
 */
class ImageReaderTest :
    StringSpec({

        fun rgb(
            r: Int,
            g: Int,
            b: Int,
        ): Int = (r shl 16) or (g shl 8) or b

        /** A 2x2 BufferedImage with a known colour in each pixel; (x, y) with y measured from the top. */
        fun sampleBuffered(): BufferedImage {
            val img = BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB)
            img.setRGB(0, 0, rgb(255, 0, 0)) // top-left red
            img.setRGB(1, 0, rgb(0, 255, 0)) // top-right green
            img.setRGB(0, 1, rgb(0, 0, 255)) // bottom-left blue
            img.setRGB(1, 1, rgb(255, 255, 255)) // bottom-right white
            return img
        }

        "toImage carries over the dimensions of the buffered image" {
            val image = ImageReader.toImage(sampleBuffered())

            image.hres shouldBe 2
            image.vres shouldBe 2
        }

        "toImage places pixel (0,0) at the top-left, matching the buffered image" {
            val image = ImageReader.toImage(sampleBuffered())

            image.getColor(row = 0, column = 0) shouldBe Color.RED
            image.getColor(row = 0, column = 1) shouldBe Color.GREEN
            image.getColor(row = 1, column = 0) shouldBe Color.BLUE
            image.getColor(row = 1, column = 1) shouldBe Color.WHITE
        }

        "fromFile decodes a PNG written to disk back into the same raster" {
            val file = File.createTempFile("imagereadertest", ".png")
            try {
                ImageIO.write(sampleBuffered(), "png", file)

                val image = ImageReader.fromFile(file.path)

                image.hres shouldBe 2
                image.vres shouldBe 2
                image.getColor(row = 0, column = 0) shouldBe Color.RED
                image.getColor(row = 1, column = 1) shouldBe Color.WHITE
            } finally {
                file.delete()
            }
        }

        "fromFile throws when the file does not exist" {
            shouldThrow<IllegalArgumentException> {
                ImageReader.fromFile("/no/such/path/definitely-missing-image.png")
            }
        }

        "fromFile throws when the file exists but is not a decodable image" {
            val file = File.createTempFile("imagereadertest", ".png")
            try {
                // Plain text in a .png-named file: ImageIO.read returns null -> requireNotNull fails.
                file.writeText("this is not an image")

                shouldThrow<IllegalArgumentException> {
                    ImageReader.fromFile(file.path)
                }
            } finally {
                file.delete()
            }
        }
    })
