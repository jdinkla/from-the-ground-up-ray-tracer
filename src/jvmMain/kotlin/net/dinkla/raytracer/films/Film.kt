package net.dinkla.raytracer.films

import korlibs.image.bitmap.Bitmap32
import korlibs.image.color.RGBA
import korlibs.image.format.PNG
import korlibs.image.format.writeBitmap
import korlibs.io.file.Vfs
import korlibs.io.file.VfsFile
import korlibs.io.file.std.localCurrentDirVfs
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.utilities.Logger
import net.dinkla.raytracer.utilities.Resolution

class Film(
    override val resolution: Resolution,
) : IFilm {
    private val bitmap = Bitmap32(resolution.width, resolution.height)

    override fun setPixel(
        x: Int,
        y: Int,
        color: Color,
    ) {
        bitmap[x, resolution.height - 1 - y] = RGBA(color.toRgba())
    }

    suspend fun save(filename: String) {
        Logger.info("save to $filename")
        val vfsFile = VfsFile(localVfs(), filename)
        vfsFile.writeBitmap(bitmap, PNG)
    }
}

fun localVfs(): Vfs = localCurrentDirVfs.vfs
