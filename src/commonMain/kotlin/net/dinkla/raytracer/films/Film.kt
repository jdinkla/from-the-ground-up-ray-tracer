package net.dinkla.raytracer.films

import com.soywiz.korim.bitmap.Bitmap32
import com.soywiz.korim.color.RGBA
import com.soywiz.korim.format.PNG
import com.soywiz.korim.format.writeBitmap
import com.soywiz.korio.file.Vfs
import com.soywiz.korio.file.VfsFile
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.utilities.Logger
import net.dinkla.raytracer.utilities.Resolution

expect fun localVfs(): Vfs

class Film(override val resolution: Resolution) : IFilm {

    private val bitmap = Bitmap32(resolution.width, resolution.height)

    override fun setPixel(x: Int, y: Int, color: Color) {
        bitmap[x, resolution.height - 1 - y] = RGBA(color.toRgba())
    }

    suspend fun save(filename: String) {
        Logger.info("save to $filename")
        val vfsFile = VfsFile(localVfs(), filename)
        vfsFile.writeBitmap(bitmap, PNG)
    }
}
