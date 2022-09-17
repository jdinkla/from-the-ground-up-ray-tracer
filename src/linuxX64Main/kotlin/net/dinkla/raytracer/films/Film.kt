package net.dinkla.raytracer.films

import com.soywiz.korio.file.Vfs
import com.soywiz.korio.file.std.localCurrentDirVfs

//class Film(val resolution: Resolution) {
//
//    private val bitmap = Bitmap32(resolution.width, resolution.height)
//
//    actual fun setPixel(x: Int, y: Int, color: Color) {
//        bitmap[x, resolution.width - 1 - y] = RGBA(color.toRgba())
//    }
//
//    actual fun setBlock(
//        x: Int,
//        y: Int,
//        width: Int,
//        height: Int,
//        color: Color
//    ) {
//    }
//
//    actual fun save(filename: String) {
//        Logger.info("save to $filename")
//        runBlocking {
//            val vfsFile = VfsFile(localCurrentDirVfs.vfs, filename)
//            vfsFile.writeBitmap(bitmap, PNG)
//        }
//    }
//}

actual fun localVfs(): Vfs = localCurrentDirVfs.vfs


