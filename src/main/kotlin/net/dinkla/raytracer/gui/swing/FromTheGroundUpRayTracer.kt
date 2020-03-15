package net.dinkla.raytracer.gui.swing

import net.dinkla.raytracer.examples.worldDef
import net.dinkla.raytracer.gui.awt.AwtFilm
import net.dinkla.raytracer.utilities.AppProperties
import net.dinkla.raytracer.world.WorldDef
import org.slf4j.LoggerFactory
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import java.io.File
import javax.swing.*
import javax.swing.border.EmptyBorder
import javax.swing.event.TreeSelectionEvent
import javax.swing.tree.DefaultMutableTreeNode

val appWidth = AppProperties.getAsInteger("display.width")
val appHeight = AppProperties.getAsInteger("display.height")

val appTitle = AppProperties["app.title"] as String
val examplesDirectory = AppProperties["examples.directory"] as String

val informationTitle = AppProperties["information.title"] as String
val informationHeader = AppProperties["information.headerText"] as String
val informationContext = AppProperties["information.contentText"] as String

val confirmationTitle = AppProperties["confirmation.title"] as String
val confirmationHeader = AppProperties["confirmation.headerText"] as String
val confirmationContext = AppProperties["confirmation.contentText"] as String

private fun createMenuBar(parent: ActionListener): JMenuBar = JMenuBar().apply {
    add(JMenu("File").apply {
        mnemonic = KeyEvent.VK_F
        addSeparator()
        add(JMenuItem("Quit").apply {
            mnemonic = KeyEvent.VK_Q
            addActionListener(parent)
        })
    })
    add(JMenu("Help").apply {
        mnemonic = KeyEvent.VK_H
        add(JMenuItem("About").apply {
            mnemonic = KeyEvent.VK_A
            addActionListener(parent)
        })
    })
}

private fun about(frame: JFrame) {
    JOptionPane.showMessageDialog(frame,
            informationHeader + '\n' + informationContext,
            informationTitle,
            JOptionPane.PLAIN_MESSAGE)
}

private fun quit(frame: JFrame) {
    val n = JOptionPane.showOptionDialog(frame,
            confirmationHeader,
            confirmationTitle,
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE, null, null, null)
    if (n == 0) {
        System.exit(0)
    }
}

class FromTheGroundUpRayTracer : ActionListener {

    private val frame: JFrame = JFrame()
    private val textArea = JTextArea()

    private var selected: String? = null

    init {
        with(frame) {
            title = appTitle
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            jMenuBar = createMenuBar(this@FromTheGroundUpRayTracer)
            setSize(appWidth, appHeight)

            val left = leftSide()
            val right = rightSide()
            val splitPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right)
            add(splitPane)

            isVisible = true
        }
    }

    private fun rightSide(): JPanel {
        val button = JButton().apply {
            text = "Render"
            addActionListener { event: ActionEvent ->
                selected?.let { render(File(it)) }
            }
        }

        with (textArea) {
            append("display the source code here")
            columns = 80
            rows = 20
            border = EmptyBorder(10, 10, 10, 10)
        }

        return JPanel().apply {
            add(button)
            add(textArea)
        }
    }

    private fun leftSide(): JTree {
        val root = DefaultMutableTreeNode(examplesDirectory)
        val directory = File(examplesDirectory)
        directory.walk().forEach { file ->
            if (file.isFile) {
                val fileName = file.absoluteFile.toString().replaceFirst(directory.absolutePath + "/", "")
                root.add(DefaultMutableTreeNode(fileName))
            }
        }
        val tree = JTree(root)
        tree.border = EmptyBorder(10, 10, 10, 10)
        tree.addTreeSelectionListener { e: TreeSelectionEvent ->
            val node = tree.lastSelectedPathComponent as? DefaultMutableTreeNode
            if (node != null) {
                selected = node.userObject.toString()
                textArea.text = File("$examplesDirectory/$selected").readText()
            }
        }
        return tree
    }

    override fun actionPerformed(e: ActionEvent) {
        when (e.actionCommand) {
            "About" -> about(frame)
            "Quit" -> quit(frame)
            else -> throw RuntimeException("Unknown Command")
        }
    }

    private fun render(file: File) {
        LOGGER.info("preview ${file.name}")
        val worldDefinition: WorldDef? = worldDef(file.name)
        if (worldDefinition != null) {
            val world = worldDefinition.world()
            world.initialize()
            val film = AwtFilm(world.viewPlane.resolution)
            val imf = ImageFrame(film)
            world.renderer?.render(film)
            imf.repaint()
        }
    }

    companion object {
        internal val LOGGER = LoggerFactory.getLogger(this::class.java)

        @JvmStatic
        fun main(args: Array<String>) {
            FromTheGroundUpRayTracer()
        }
    }
}

