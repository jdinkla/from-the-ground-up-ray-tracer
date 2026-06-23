package net.dinkla.raytracer.ui.swing

import korlibs.time.DateTime
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.withContext
import net.dinkla.raytracer.examples.worldDef
import net.dinkla.raytracer.renderer.AtomicCancellationToken
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
import javax.swing.JFileChooser
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JProgressBar
import javax.swing.JScrollPane
import javax.swing.JSplitPane
import javax.swing.JTextArea
import javax.swing.JTextField
import javax.swing.JTree
import javax.swing.SwingUtilities
import javax.swing.Timer
import javax.swing.UIManager
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
private const val OUTPUT_FIELD_COLUMNS = 28
private const val SPLIT_RESIZE_WEIGHT = 0.25
private const val INITIAL_DIVIDER = 320

/** Default directory for saved PNGs: a predictable `renders/` folder under the working directory. */
private val defaultOutputDirectory: File = File(System.getProperty("user.dir"), "renders")

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
    private val cancelButton = JButton("Cancel")
    private val progressBar = JProgressBar(0, PERCENT)
    private val statusLabel = JLabel("Ready")

    /** Embedded live-preview canvas; reused across renders instead of spawning a floating window. */
    private val previewCanvas = ImageCanvas()

    /** User-configurable output directory; defaults to a predictable `renders/` folder. */
    private var outputDirectory: File = defaultOutputDirectory
    private val outputField = JTextField(outputDirectory.absolutePath, OUTPUT_FIELD_COLUMNS)

    /** Guards against launching a second render while one is already in flight. */
    private var rendering = false

    /**
     * The in-flight render's cancellation token and coroutine job. Cancel flips the token (so the
     * renderer's per-row/per-block poll stops CPU work promptly) and cancels the job (so the coroutine
     * unwinds). Both are cleared when the render finishes or is cancelled.
     */
    private var renderToken: AtomicCancellationToken? = null
    private var renderJob: Job? = null

    override val coroutineContext: CoroutineContext = Dispatchers.Default

    init {
        with(frame) {
            title = appTitle
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            jMenuBar = createMenuBar(this@FromTheGroundUpRayTracer)
            setSize(appWidth, appHeight)
            val controlsAndPreview =
                JSplitPane(JSplitPane.HORIZONTAL_SPLIT, rightSide(), previewPane()).apply {
                    resizeWeight = SPLIT_RESIZE_WEIGHT
                }
            add(
                JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSide(), controlsAndPreview).apply {
                    dividerLocation = INITIAL_DIVIDER
                },
                BorderLayout.CENTER,
            )
            add(statusBar(), BorderLayout.SOUTH)
            isVisible = true
        }
    }

    private fun leftSide(): JPanel {
        val left = LeftSide()
        left.tree.addTreeSelectionListener(treeSelectionListener(left.tree))
        return left.component
    }

    /** Scrollable host for the embedded [previewCanvas] — the render appears here, in the main window. */
    private fun previewPane(): JScrollPane =
        JScrollPane(previewCanvas).apply {
            border = EmptyBorder(10, 10, 10, 10)
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
            add(outputSelector())
            add(buttons())
            add(textArea)
        }
    }

    /**
     * Lets the user pick where PNGs are written. The path is editable directly and selectable via a
     * [JFileChooser]; it defaults to a predictable `renders/` folder rather than the old hardcoded
     * '../' relative path.
     */
    private fun outputSelector(): JPanel {
        val chooseButton =
            JButton("Choose…").apply {
                addActionListener { chooseOutputDirectory() }
            }
        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            border = EmptyBorder(0, 10, 10, 10)
            add(JLabel("Output: "))
            add(outputField)
            add(chooseButton)
        }
    }

    private fun chooseOutputDirectory() {
        val chooser =
            JFileChooser(outputDirectory).apply {
                fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                dialogTitle = "Choose output directory"
            }
        if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            outputDirectory = chooser.selectedFile
            outputField.text = outputDirectory.absolutePath
        }
    }

    /**
     * Resolves the absolute path to write [fileName] into the user-chosen output directory, creating
     * the directory if it does not yet exist. Reads the editable field so a hand-typed path is honoured.
     */
    private fun outputPath(fileName: String): String {
        outputDirectory = File(outputField.text.trim().ifEmpty { defaultOutputDirectory.absolutePath })
        outputDirectory.mkdirs()
        return File(outputDirectory, fileName).absolutePath
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
        cancelButton.addActionListener { onCancel() }
        cancelButton.isEnabled = false
        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            border = EmptyBorder(10, 10, 10, 10)
            add(renderButton)
            add(pngButton)
            add(cancelButton)
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
        val token = AtomicCancellationToken()
        renderToken = token
        setBusy(true, "Rendering ${file.name}…", cancellable = true)
        renderJob =
            launch {
                var timer: Timer? = null
                try {
                    val state = startInteractiveRender(definition)
                    timer = state.timer
                    // Passing the token lets Cancel stop the renderer's CPU work promptly mid-render.
                    val stats = Render.render(state.film, state.renderer, token)
                    if (token.isCancelled) {
                        withContext(NonCancellable + Dispatchers.Swing) { setStatus("Cancelled ${file.name}") }
                    } else {
                        val out =
                            withContext(Dispatchers.Swing) {
                                previewCanvas.repaint()
                                progressBar.value = PERCENT
                                setStatus("Rendered ${file.name} in ${stats.duration.inWholeMilliseconds} ms")
                                outputPath(outputPngFileName(file.name, DateTime.now()))
                            }
                        state.film.image.save(out)
                    }
                } catch (e: CancellationException) {
                    // User cancelled the coroutine; not an error. Re-throw so the job ends cancelled.
                    withContext(NonCancellable + Dispatchers.Swing) { setStatus("Cancelled ${file.name}") }
                    throw e
                } catch (e: Exception) {
                    reportFailure(e)
                } finally {
                    // Runs even when cancelled: stop the preview timer and restore the idle UI.
                    withContext(NonCancellable + Dispatchers.Swing) {
                        timer?.stop()
                        setBusy(false)
                    }
                }
            }
    }

    /**
     * Cancels the in-flight render: flips the cancellation token so the renderer's per-row/per-block
     * poll stops CPU work promptly, then cancels the coroutine job so it unwinds. The render coroutine's
     * `finally` block restores the idle UI (re-enabling Render/PNG).
     */
    private fun onCancel() {
        if (!rendering) {
            return
        }
        setStatus("Cancelling…")
        renderToken?.cancel()
        renderJob?.cancel()
    }

    /**
     * Builds the world off the EDT, then — on the EDT — installs the fresh film image into the
     * embedded [previewCanvas] and starts a [Timer] that repaints it and updates the progress bar
     * while the render fills the [SwingFilm] in place, giving a live "image appearing" preview in the
     * main window. Carries the wired-up [IRenderer] back to the caller so the blocking render runs off
     * the EDT.
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
            previewCanvas.image = film.image
            val startNanos = System.nanoTime()
            val timer =
                Timer(PREVIEW_INTERVAL_MS) {
                    previewCanvas.repaint()
                    val percent = (film.renderedPixels * PERCENT / film.totalPixels).toInt()
                    progressBar.value = percent
                    setStatus("Rendering… $percent%  ${elapsedMillis(startNanos) / MILLIS_PER_SECOND}s")
                }
            timer.start()
            InteractiveRender(film, timer, renderer)
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
        val out = outputPath(outputPngFileName(file.name))
        launch {
            try {
                val result = Render.render(definition, context)
                result.film.save(out)
                withContext(Dispatchers.Swing) {
                    setStatus("Saved PNG for ${file.name} in ${result.stats.duration.inWholeMilliseconds} ms")
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
        // The renderers wrap the original failure (e.g. an incompatible tracer raising
        // UnsupportedOperationException) in an IllegalStateException, so the top-level message is a
        // generic "renderer aborted". Walk the cause chain to the real cause so the dialog shows the
        // meaningful reason (e.g. "AreaLight needs AreaLighting Tracer") rather than the wrapper.
        val message = rootCauseMessage(e)
        withContext(Dispatchers.Swing) {
            setStatus("Failed: $message")
            dialog(message, "Exception occurred", JOptionPane.ERROR_MESSAGE)
        }
    }

    /**
     * Returns the deepest non-blank message in the cause chain, prefixed with the root exception's
     * simple class name so the dialog identifies the failure even when a message is terse. Falls back
     * to the class name alone when every message in the chain is null or blank.
     */
    private fun rootCauseMessage(e: Throwable): String {
        var current: Throwable = e
        var message: String? = current.message
        while (current.cause != null && current.cause !== current) {
            current = current.cause!!
            current.message?.takeUnless { it.isBlank() }?.let { message = it }
        }
        val rootType = current::class.simpleName ?: e::class.simpleName ?: "Error"
        return message?.takeUnless { it.isBlank() }?.let { "$rootType: $it" } ?: rootType
    }

    private fun setBusy(
        busy: Boolean,
        message: String? = null,
        cancellable: Boolean = false,
    ) {
        rendering = busy
        renderButton.isEnabled = !busy
        pngButton.isEnabled = !busy
        // Only the interactive render threads a cancellation token through the renderer, so Cancel is
        // offered for that path only; the PNG path has no mid-render stop and leaves the button disabled.
        cancelButton.isEnabled = busy && cancellable
        if (busy) {
            progressBar.value = 0
            message?.let { setStatus(it) }
        } else {
            renderToken = null
            renderJob = null
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

/** Everything an in-flight interactive render needs to drive and finish: the live [film] (its image
 *  is shown in the embedded preview), the preview [timer], and the wired-up [renderer] that does the
 *  off-EDT work. */
private class InteractiveRender(
    val film: SwingFilm,
    val timer: Timer,
    val renderer: IRenderer,
)

fun main() {
    SwingUtilities.invokeLater {
        // Apply the platform's native look-and-feel before any widget is created, so the whole UI
        // adopts it. Falling back to the cross-platform default on the rare platform that rejects it.
        runCatching { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()) }
        FromTheGroundUpRayTracer()
    }
}
