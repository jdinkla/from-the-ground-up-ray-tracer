package net.dinkla.raytracer.utilities

import com.soywiz.korio.async.runBlockingNoSuspensions
import com.soywiz.korio.file.VfsFile
import com.soywiz.korio.file.std.NodeVfs

actual fun read(fileName: String): List<String> {

    runBlockingNoSuspensions {
        println("**********************************")
        val vfsFile = VfsFile(NodeVfs(true), "/c/workspace")
        println("**********************************")
        println(vfsFile.absolutePath)
        println(vfsFile.listNames())
    }

    val vfsFile = VfsFile(NodeVfs(true), fileName)
    runBlockingNoSuspensions {
        val bytes = vfsFile.readLines().toList()
        println("********************************** ${bytes.size}")
    }
    return listOf()
}
