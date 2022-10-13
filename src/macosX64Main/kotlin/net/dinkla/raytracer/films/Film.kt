package net.dinkla.raytracer.films

import com.soywiz.korio.file.Vfs
import com.soywiz.korio.file.std.localCurrentDirVfs

actual fun localVfs(): Vfs = localCurrentDirVfs.vfs


