package net.dinkla.raytracer.films

import com.soywiz.korim.bitmap.Bitmap32
import com.soywiz.korim.color.RGBA
import com.soywiz.korim.format.PNG
import com.soywiz.korim.format.writeBitmap
import com.soywiz.korio.file.VfsFile
import com.soywiz.korio.file.std.NodeVfs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.utilities.Logger
import net.dinkla.raytracer.utilities.Resolution
import node.buffer.BufferEncoding

actual class Film {

    actual var resolution: Resolution = Resolution.RESOLUTION_720

    private val bitmap = Bitmap32(resolution.vres, resolution.hres)

    actual fun setPixel(x: Int, y: Int, color: Color) {
        bitmap[x, resolution.vres - 1 - y] = RGBA(color.toRgba())
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
        GlobalScope.launch(Dispatchers.Default) {
            val vfsFile = VfsFile(NodeVfs(true), filename)
            vfsFile.writeBitmap(bitmap, PNG)
            val ba = vfsFile.read()
            node.fs.writeFileSync(filename, ba, BufferEncoding.binary)
        }
        // TODO JOIN!!!
    }
}