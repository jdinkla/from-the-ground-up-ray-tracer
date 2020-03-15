package net.dinkla.raytracer.gui.javafx

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import java.io.File
import javafx.scene.control.TreeItem

// https://stackoverflow.com/questions/34534775/configuring-a-treeview-which-scans-local-fie-system-to-only-include-folders-whic

class SceneFileTreeItem(f: File) : TreeItem<File>(f) {

    private var isFirstTimeChildren = true
    private var isFirstTimeLeaf = true
    private var isLeaf: Boolean = false

    override fun getChildren(): ObservableList<TreeItem<File>> {
        if (isFirstTimeChildren) {
            isFirstTimeChildren = false
            super.getChildren().setAll(buildChildren(this))
        }
        return super.getChildren()
    }

    override fun isLeaf(): Boolean {
        if (isFirstTimeLeaf) {
            isFirstTimeLeaf = false
            val f = value as File
            isLeaf = f.isFile
        }
        return isLeaf
    }

    private fun buildChildren(TreeItem: TreeItem<File>): ObservableList<TreeItem<File>> {
        val f = TreeItem.value
        if (f != null && f.isDirectory) {
            val files = f.listFiles()
            if (files != null) {
                val children = FXCollections.observableArrayList<TreeItem<File>>()
                for (childFile in files) {
                    children.add(SceneFileTreeItem(childFile))
                }
                return children
            }
        }
        return FXCollections.emptyObservableList()
    }
}