package net.dinkla.raytracer.films

import com.soywiz.korim.bitmap.Bitmap32
import com.soywiz.korim.color.RGBA
import com.soywiz.korim.format.PNG
import com.soywiz.korim.format.writeBitmap
import com.soywiz.korio.file.VfsFile
import com.soywiz.korio.file.std.localCurrentDirVfs
import kotlinx.coroutines.runBlocking
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.utilities.Logger
import net.dinkla.raytracer.utilities.Resolution

actual class Film {

    actual var resolution: Resolution = Resolution.RESOLUTION_1080

    private val bitmap = Bitmap32(resolution.width, resolution.height)

    actual fun setPixel(x: Int, y: Int, color: Color) {
        bitmap[x, resolution.width - 1 - y] = RGBA(color.toRgba())
    }

    actual fun setBlock(
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        color: Color
    ) {
    }

    actual fun save(filename: String) {
        Logger.info("save to $filename")
        runBlocking {
            val vfsFile = VfsFile(localCurrentDirVfs.vfs, filename)
            vfsFile.writeBitmap(bitmap, PNG)
        }
    }
}