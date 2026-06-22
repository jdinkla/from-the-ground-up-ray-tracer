package net.dinkla.raytracer.textures

import net.dinkla.raytracer.colors.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 * JVM image-file loading for [ImageTexture]. Decodes a file (PNG/JPG/… via `ImageIO`) into the
 * platform-independent in-memory [Image] that [ImageTexture] samples. This is the only piece of the
 * texture stack that performs I/O, so it lives in `jvmMain`; all sampling math stays in `commonMain`
 * over the decoded raster. Mirrors the JVM I/O style of `utilities/Png.kt`.
 */
object ImageReader {
    /** Decodes the image at [fileName] into an [Image]. Throws if the file is missing or undecodable. */
    fun fromFile(fileName: String): Image {
        val file = File(fileName)
        require(file.exists()) { "Image file not found: $fileName" }
        val buffered =
            requireNotNull(ImageIO.read(file)) { "Could not decode image (unsupported format?): $fileName" }
        return toImage(buffered)
    }

    /** Converts a decoded [BufferedImage] into an [Image] of [Color]s, row 0 at the top. */
    fun toImage(buffered: BufferedImage): Image {
        val hres = buffered.width
        val vres = buffered.height
        val pixels =
            Array(hres * vres) { index ->
                val column = index % hres
                val row = index / hres
                Color.fromInt(buffered.getRGB(column, row))
            }
        return Image(hres, vres, pixels)
    }
}
