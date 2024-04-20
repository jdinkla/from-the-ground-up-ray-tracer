package net.dinkla.raytracer.ui.swing

import net.dinkla.raytracer.utilities.fileNameWithoutDirectory
import java.awt.Component
import java.io.File
import javax.swing.JScrollPane
import javax.swing.JTree
import javax.swing.border.EmptyBorder
import javax.swing.tree.DefaultMutableTreeNode

class LeftSide : Component() {

    private val root = createTreeFromDirectory(examplesDirectory)
    val tree = JTree(root).apply {
        border = EmptyBorder(10, 10, 10, 10)
    }
    val component = JScrollPane(tree)

    private fun createTreeFromDirectory(directoryName: String): DefaultMutableTreeNode {
        val root = DefaultMutableTreeNode(directoryName)
        val directory = File(directoryName)
        directory.walk().sorted().forEach { file ->
            if (file.isFile) {
                val fileName = fileNameWithoutDirectory(
                    file.absoluteFile.toString(), directory.absolutePath.toString(), File.separator
                )
                root.add(DefaultMutableTreeNode(fileName))
            }
        }
        return root
    }
}
