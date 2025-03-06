package net.dinkla.raytracer.ui.swing

import korlibs.time.DateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.dinkla.raytracer.examples.worldDef
import net.dinkla.raytracer.renderer.Renderer
import net.dinkla.raytracer.tracers.Tracers
import net.dinkla.raytracer.utilities.Logger
import net.dinkla.raytracer.utilities.Resolution
import net.dinkla.raytracer.utilities.outputPngFileName
import net.dinkla.raytracer.utilities.save
import net.dinkla.raytracer.world.Context
import net.dinkla.raytracer.world.Render
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.io.File
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JFrame
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JSplitPane
import javax.swing.JTextArea
import javax.swing.JTree
import javax.swing.border.EmptyBorder
import javax.swing.event.TreeSelectionEvent
import javax.swing.tree.DefaultMutableTreeNode
import kotlin.coroutines.CoroutineContext

var resolution: Resolution = Resolution(width, height)

class FromTheGroundUpRayTracer :
    ActionListener,
    CoroutineScope {
    private val frame: JFrame = JFrame()
    private val textArea = JTextArea()
    private var selected: String? = null

    private val tracers = Tracers.entries.toTypedArray()
    private val tracerNames = tracers.map { it.name }.toTypedArray()
    private val renderers = Renderer.entries.toTypedArray()
    private val rendererNames = renderers.map { it.name }.toTypedArray()

    private var selectedTracer = 0
    private var selectedRenderer = 0

    override val coroutineContext: CoroutineContext = Dispatchers.Default

    init {
        with(frame) {
            title = appTitle
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            jMenuBar = createMenuBar(this@FromTheGroundUpRayTracer)
            setSize(appWidth, appHeight)
            add(JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSide(), rightSide()))
            isVisible = true
        }
    }

    private fun leftSide(): JScrollPane {
        val left = LeftSide()
        left.tree.addTreeSelectionListener(treeSelectionListener(left.tree))
        return left.component
    }

    private fun treeSelectionListener(tree: JTree) =
        { e: TreeSelectionEvent ->
            val node = tree.lastSelectedPathComponent as? DefaultMutableTreeNode
            if (node != null) {
                selected = node.userObject.toString()
                textArea.text = File("$examplesDirectory/$selected").readText()
            }
        }

    private fun rightSide(): JPanel {
        val buttons = createButtons()
        with(textArea) {
            append("display the source code here")
            columns = 80
            rows = 20
            border = EmptyBorder(10, 10, 10, 10)
        }
        val tracerComboBox =
            JComboBox(tracerNames).apply {
                addActionListener { _: ActionEvent ->
                    selectedTracer = selectedIndex
                }
            }
        val rendererComboBox =
            JComboBox(rendererNames).apply {
                addActionListener { _: ActionEvent ->
                    selectedRenderer = selectedIndex
                }
            }
        rendererComboBox.selectedIndex = 2
        val selections =
            JPanel().apply {
                layout = BoxLayout(this, BoxLayout.X_AXIS)
                border = EmptyBorder(10, 10, 10, 10)
                add(tracerComboBox)
                add(rendererComboBox)
            }
        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            add(selections)
            add(buttons)
            add(textArea)
        }
    }

    private fun createButtons(): JPanel {
        val renderButton =
            JButton().apply {
                text = "Render"
                addActionListener { _: ActionEvent ->
                    selected?.let { render(File(it)) }
                }
            }
        val pngButton =
            JButton().apply {
                text = "PNG"
                addActionListener { _: ActionEvent ->
                    selected?.let { png(File(it)) }
                }
            }
        val buttons =
            JPanel().apply {
                layout = BoxLayout(this, BoxLayout.X_AXIS)
                border = EmptyBorder(10, 10, 10, 10)
                add(renderButton)
                add(pngButton)
            }
        return buttons
    }

    override fun actionPerformed(e: ActionEvent) {
        when (e.actionCommand) {
            "About" -> about(frame)
            "Quit" -> quit(frame)
            else -> throw RuntimeException("Unknown Command")
        }
    }

    private fun render(file: File) {
        log(file, "render")
        val context = Context(tracers[selectedTracer].create, renderers[selectedRenderer].creator, resolution)
        worldDef(file.name)?.let {
            launch {
                try {
                    val world = it.world()
                    context.adapt(world)
                    world.initialize()
                    val film = SwingFilm(world.viewPlane.resolution)
                    val frame = ImageFrame(film)
                    Render.render(film, world.renderer!!)
                    frame.repaint()
                    film.image.save("../" + outputPngFileName(file.name, DateTime.now()))
                } catch (e: Exception) {
                    Logger.info(e.message ?: "an exception occurred")
                    Logger.error(e.stackTraceToString())
                    dialog(e.message, "Exception occurred", JOptionPane.ERROR_MESSAGE)
                }
            }
        }
    }

    private fun png(file: File) {
        log(file, "png")
        val context = Context(tracers[selectedTracer].create, renderers[selectedRenderer].creator, resolution)
        worldDef(file.name)?.let {
            launch {
                try {
                    val (film, _) = Render.render(it, context)
                    film.save("../" + outputPngFileName(file.name))
                    dialog(pngMessage, pngTitle, JOptionPane.INFORMATION_MESSAGE)
                } catch (e: Exception) {
                    dialog(e.message, "Exception occurred", JOptionPane.ERROR_MESSAGE)
                }
            }
        }
    }

    private fun dialog(
        message: String?,
        title: String,
        type: Int,
    ) = JOptionPane.showMessageDialog(frame, message, title, type)

    private fun log(
        file: File,
        name: String,
    ) {
        Logger.info(
            "$name ${file.name} with tracer ${tracers[selectedTracer]} and renderer ${renderers[selectedRenderer]}",
        )
    }
}

fun main() {
    FromTheGroundUpRayTracer()
}
