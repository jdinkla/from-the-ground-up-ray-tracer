package net.dinkla.raytracer.ui.swing

import korlibs.time.DateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.withContext
import net.dinkla.raytracer.examples.worldDef
import net.dinkla.raytracer.renderer.IRenderer
import net.dinkla.raytracer.renderer.Renderer
import net.dinkla.raytracer.tracers.Tracers
import net.dinkla.raytracer.utilities.Logger
import net.dinkla.raytracer.utilities.Resolution
import net.dinkla.raytracer.utilities.outputPngFileName
import net.dinkla.raytracer.utilities.save
import net.dinkla.raytracer.world.Context
import net.dinkla.raytracer.world.Render
import net.dinkla.raytracer.world.WorldDefinition
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Toolkit
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.io.File
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JProgressBar
import javax.swing.JScrollPane
import javax.swing.JSplitPane
import javax.swing.JTextArea
import javax.swing.JTree
import javax.swing.SwingUtilities
import javax.swing.Timer
import javax.swing.border.EmptyBorder
import javax.swing.event.TreeSelectionEvent
import javax.swing.tree.DefaultMutableTreeNode
import kotlin.coroutines.CoroutineContext

private const val PREVIEW_INTERVAL_MS = 150
private const val PERCENT = 100
private const val MILLIS_PER_SECOND = 1_000L
private const val NANOS_PER_MILLI = 1_000_000L
private const val PROGRESS_BAR_WIDTH = 220
private const val PROGRESS_BAR_HEIGHT = 18
private const val STATUS_PAD_V = 4
private const val STATUS_PAD_H = 10

// Many small methods are inherent to a Swing controller (one per widget/handler); splitting them
// across types would hurt readability more than the count helps it.
@Suppress("TooManyFunctions")
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
    private val resolutions = Resolution.resolutions.toTypedArray()
    private val resolutionNames = resolutions.map { it.id }.toTypedArray()

    private var selectedTracer = 0
    private var selectedRenderer = Renderer.PARALLEL.ordinal
    private var selectedResolution = resolutions.indexOfFirst { it.height == height }.coerceAtLeast(0)

    private val renderButton = JButton("Render")
    private val pngButton = JButton("PNG")
    private val progressBar = JProgressBar(0, PERCENT)
    private val statusLabel = JLabel("Ready")

    /** Guards against launching a second render while one is already in flight. */
    private var rendering = false

    override val coroutineContext: CoroutineContext = Dispatchers.Default

    init {
        with(frame) {
            title = appTitle
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            jMenuBar = createMenuBar(this@FromTheGroundUpRayTracer)
            setSize(appWidth, appHeight)
            add(JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSide(), rightSide()), BorderLayout.CENTER)
            add(statusBar(), BorderLayout.SOUTH)
            isVisible = true
        }
    }

    private fun leftSide(): JScrollPane {
        val left = LeftSide()
        left.tree.addTreeSelectionListener(treeSelectionListener(left.tree))
        return left.component
    }

    private fun treeSelectionListener(tree: JTree) =
        { _: TreeSelectionEvent ->
            val node = tree.lastSelectedPathComponent as? DefaultMutableTreeNode
            if (node != null) {
                selected = node.userObject.toString()
                textArea.text = File("$examplesDirectory/$selected").readText()
            }
        }

    private fun rightSide(): JPanel {
        with(textArea) {
            append("display the source code here")
            columns = 80
            rows = 20
            border = EmptyBorder(10, 10, 10, 10)
        }
        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            add(selections())
            add(buttons())
            add(textArea)
        }
    }

    private fun selections(): JPanel {
        val tracerComboBox =
            JComboBox(tracerNames).apply {
                addActionListener { selectedTracer = selectedIndex }
            }
        val rendererComboBox =
            JComboBox(rendererNames).apply {
                selectedIndex = selectedRenderer
                addActionListener { selectedRenderer = selectedIndex }
            }
        val resolutionComboBox =
            JComboBox(resolutionNames).apply {
                selectedIndex = selectedResolution
                addActionListener { selectedResolution = selectedIndex }
            }
        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            border = EmptyBorder(10, 10, 10, 10)
            add(JLabel("Tracer: "))
            add(tracerComboBox)
            add(JLabel("  Renderer: "))
            add(rendererComboBox)
            add(JLabel("  Resolution: "))
            add(resolutionComboBox)
        }
    }

    private fun buttons(): JPanel {
        renderButton.addActionListener { onRender() }
        pngButton.addActionListener { onPng() }
        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            border = EmptyBorder(10, 10, 10, 10)
            add(renderButton)
            add(pngButton)
        }
    }

    private fun statusBar(): JPanel {
        progressBar.isStringPainted = true
        progressBar.preferredSize = Dimension(PROGRESS_BAR_WIDTH, PROGRESS_BAR_HEIGHT)
        return JPanel(BorderLayout()).apply {
            border = EmptyBorder(STATUS_PAD_V, STATUS_PAD_H, STATUS_PAD_V, STATUS_PAD_H)
            add(statusLabel, BorderLayout.WEST)
            add(progressBar, BorderLayout.EAST)
        }
    }

    override fun actionPerformed(e: ActionEvent) {
        when (e.actionCommand) {
            "About" -> about(frame)
            "Quit" -> quit(frame)
            else -> throw IllegalStateException("Unknown action command: ${e.actionCommand}")
        }
    }

    private fun onRender() = withSelectedScene { render(it) }

    private fun onPng() = withSelectedScene { png(it) }

    /**
     * Runs [action] for the currently selected scene, or gives the user visible feedback when nothing
     * is selected or a render is already running — instead of the previous silent no-op.
     */
    private fun withSelectedScene(action: (File) -> Unit) {
        if (rendering) {
            return
        }
        val scene = selected
        if (scene == null) {
            setStatus("Select a scene first")
            Toolkit.getDefaultToolkit().beep()
            return
        }
        action(File(scene))
    }

    private fun newContext(): Context =
        Context(
            tracers[selectedTracer].create,
            renderers[selectedRenderer].creator,
            resolutions[selectedResolution].create(),
        )

    // Broad catch is intentional at this Swing action boundary: a scene render can fail in many
    // unrelated ways (IO, arithmetic, scene-config), and an uncaught exception inside the coroutine
    // would crash silently. We surface every failure to the user via the status bar, a dialog and the log.
    @Suppress("TooGenericExceptionCaught")
    private fun render(file: File) {
        val definition = worldDef(file.name) ?: return unknownScene(file)
        log(file, "render")
        setBusy(true, "Rendering ${file.name}…")
        launch {
            val startNanos = System.nanoTime()
            try {
                val state = startInteractiveRender(definition)
                Render.render(state.film, state.renderer)
                withContext(Dispatchers.Swing) {
                    state.timer.stop()
                    state.frame.repaint()
                    progressBar.value = PERCENT
                    setStatus("Rendered ${file.name} in ${elapsedMillis(startNanos)} ms")
                }
                state.film.image.save("../" + outputPngFileName(file.name, DateTime.now()))
            } catch (e: Exception) {
                reportFailure(e)
            } finally {
                withContext(Dispatchers.Swing) { setBusy(false) }
            }
        }
    }

    /**
     * Builds the world off the EDT, then — on the EDT — opens the preview window and starts a [Timer]
     * that repaints it and updates the progress bar while the render fills the [SwingFilm] in place,
     * giving a live "image appearing" preview. Carries the wired-up [IRenderer] back to the caller so
     * the blocking render runs off the EDT.
     */
    private suspend fun startInteractiveRender(definition: WorldDefinition): InteractiveRender {
        val context = newContext()
        val world = definition.world()
        context.adapt(world)
        world.initialize()
        val film = SwingFilm(world.viewPlane.resolution)
        val renderer =
            requireNotNull(world.renderer) { "World.renderer not set; context.adapt(world) must run first" }
        return withContext(Dispatchers.Swing) {
            val imageFrame = ImageFrame(film)
            val startNanos = System.nanoTime()
            val timer =
                Timer(PREVIEW_INTERVAL_MS) {
                    imageFrame.repaint()
                    val percent = (film.renderedPixels * PERCENT / film.totalPixels).toInt()
                    progressBar.value = percent
                    setStatus("Rendering… $percent%  ${elapsedMillis(startNanos) / MILLIS_PER_SECOND}s")
                }
            timer.start()
            InteractiveRender(film, imageFrame, timer, renderer)
        }
    }

    // Broad catch is intentional at this Swing action boundary (see render); we log and surface the
    // failure to the user instead of letting it escape the coroutine unhandled.
    @Suppress("TooGenericExceptionCaught")
    private fun png(file: File) {
        val definition = worldDef(file.name) ?: return unknownScene(file)
        log(file, "png")
        setBusy(true, "Rendering ${file.name} to PNG…")
        progressBar.isIndeterminate = true
        val context = newContext()
        launch {
            val startNanos = System.nanoTime()
            try {
                val (film, _) = Render.render(definition, context)
                film.save("../" + outputPngFileName(file.name))
                withContext(Dispatchers.Swing) {
                    setStatus("Saved PNG for ${file.name} in ${elapsedMillis(startNanos)} ms")
                    dialog(pngMessage, pngTitle, JOptionPane.INFORMATION_MESSAGE)
                }
            } catch (e: Exception) {
                reportFailure(e)
            } finally {
                withContext(Dispatchers.Swing) {
                    progressBar.isIndeterminate = false
                    setBusy(false)
                }
            }
        }
    }

    private fun unknownScene(file: File) {
        setStatus("Unknown scene: ${file.name}")
        Toolkit.getDefaultToolkit().beep()
    }

    private suspend fun reportFailure(e: Exception) {
        Logger.info(e.message ?: "an exception occurred")
        Logger.error(e.stackTraceToString())
        withContext(Dispatchers.Swing) {
            setStatus("Failed: ${e.message}")
            dialog(e.message, "Exception occurred", JOptionPane.ERROR_MESSAGE)
        }
    }

    private fun setBusy(
        busy: Boolean,
        message: String? = null,
    ) {
        rendering = busy
        renderButton.isEnabled = !busy
        pngButton.isEnabled = !busy
        if (busy) {
            progressBar.value = 0
            message?.let { setStatus(it) }
        }
    }

    private fun setStatus(message: String) {
        statusLabel.text = message
    }

    private fun elapsedMillis(startNanos: Long): Long = (System.nanoTime() - startNanos) / NANOS_PER_MILLI

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
            "$name ${file.name} with tracer ${tracers[selectedTracer]}, renderer " +
                "${renderers[selectedRenderer]} and resolution ${resolutions[selectedResolution].id}",
        )
    }
}

/** Everything an in-flight interactive render needs to drive and finish: the live [film]/[frame], the
 *  preview [timer], and the wired-up [renderer] that does the off-EDT work. */
private class InteractiveRender(
    val film: SwingFilm,
    val frame: ImageFrame,
    val timer: Timer,
    val renderer: IRenderer,
)

fun main() {
    SwingUtilities.invokeLater { FromTheGroundUpRayTracer() }
}
