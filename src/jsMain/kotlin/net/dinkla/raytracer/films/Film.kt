package net.dinkla.raytracer.films

import com.soywiz.korio.file.Vfs
import com.soywiz.korio.file.std.NodeVfs

// TODO does not work in JavaScript

//    actual fun save(filename: String) {
//        Logger.info("save to $filename")
//        GlobalScope.launch(Dispatchers.Default) {
//            val vfsFile = VfsFile(NodeVfs(true), filename)
//            vfsFile.writeBitmap(bitmap, PNG)
//            val ba = vfsFile.read()
//            node.fs.writeFileSync(filename, ba, BufferEncoding.binary)
//        }
//        // TODO JOIN!!!
//    }
// }

actual fun localVfs(): Vfs = NodeVfs(true)
