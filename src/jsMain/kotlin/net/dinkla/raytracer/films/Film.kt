package net.dinkla.raytracer.films

import com.soywiz.korio.file.Vfs
import com.soywiz.korio.file.std.NodeVfs
actual fun localVfs(): Vfs = NodeVfs(true)
