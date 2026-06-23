package net.dinkla.raytracer.ui.swing

import net.dinkla.raytracer.utilities.fileNameWithoutDirectory
import java.awt.BorderLayout
import java.io.File
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextField
import javax.swing.JTree
import javax.swing.border.EmptyBorder
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel

private const val SEARCH_BORDER = 6

/**
 * The scene picker on the left of the main window: a search field plus a [JTree] of the example
 * scene files. Typing in the search field filters the tree to the file names containing the query
 * (case-insensitive), so a specific scene is quick to find among the many examples.
 *
 * Plain holder class (it composes Swing widgets rather than being one): [component] is the panel to
 * drop into the layout; [tree] is exposed so the controller can attach a selection listener.
 */
class LeftSide {
    private val allFileNames: List<String> = collectFileNames(examplesDirectory)

    val tree =
        JTree(buildModel(allFileNames)).apply {
            border = EmptyBorder(SEARCH_BORDER, SEARCH_BORDER, SEARCH_BORDER, SEARCH_BORDER)
        }

    private val searchField =
        JTextField().apply {
            toolTipText = "Filter scenes"
            document.addDocumentListener(
                object : DocumentListener {
                    override fun insertUpdate(e: DocumentEvent?) = applyFilter(text)

                    override fun removeUpdate(e: DocumentEvent?) = applyFilter(text)

                    override fun changedUpdate(e: DocumentEvent?) = applyFilter(text)
                },
            )
        }

    val component =
        JPanel(BorderLayout()).apply {
            border = EmptyBorder(SEARCH_BORDER, SEARCH_BORDER, SEARCH_BORDER, SEARCH_BORDER)
            add(
                JPanel(BorderLayout(SEARCH_BORDER, 0)).apply {
                    add(JLabel("Search:"), BorderLayout.WEST)
                    add(searchField, BorderLayout.CENTER)
                },
                BorderLayout.NORTH,
            )
            add(JScrollPane(tree), BorderLayout.CENTER)
        }

    private fun applyFilter(query: String) {
        val needle = query.trim()
        val matches =
            if (needle.isEmpty()) {
                allFileNames
            } else {
                allFileNames.filter { it.contains(needle, ignoreCase = true) }
            }
        tree.model = buildModel(matches)
        for (row in 0 until tree.rowCount) {
            tree.expandRow(row)
        }
    }

    private fun buildModel(fileNames: List<String>): DefaultTreeModel {
        val root = DefaultMutableTreeNode(examplesDirectory)
        fileNames.forEach { root.add(DefaultMutableTreeNode(it)) }
        return DefaultTreeModel(root)
    }

    private fun collectFileNames(directoryName: String): List<String> {
        val directory = File(directoryName)
        return directory
            .walk()
            .sorted()
            .filter { it.isFile }
            .map {
                fileNameWithoutDirectory(
                    it.absoluteFile.toString(),
                    directory.absolutePath.toString(),
                    File.separator,
                )
            }.toList()
    }
}
