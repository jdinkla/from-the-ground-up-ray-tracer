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

private fun createMenuBar(parent: ActionListener): JMenuBar = JMenuBar().apply {
    add(JMenu("File").apply {
        mnemonic = KeyEvent.VK_F
        add(JMenuItem("Open").apply {
            mnemonic = KeyEvent.VK_O
            addActionListener(parent)
        })
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
            "(c) 2012-2020 Jörn Dinkla\nwww.dinkla.net",
            "About",
            JOptionPane.PLAIN_MESSAGE)
}

private fun quit(frame: JFrame) {
    val n = JOptionPane.showOptionDialog(frame,
            "Do you really want to exit the application?",
            "Quit?",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE, null, null, null)
    if (n == 0) {
        System.exit(0)
    }
}


class FromTheGroundUpRayTracer : ActionListener {

    private val frame: JFrame = JFrame()
    private var fileChooser = JFileChooser()
    private var isFirst = true

    init {
        with(frame) {
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            jMenuBar = createMenuBar(this@FromTheGroundUpRayTracer)
            setSize(appWidth, appHeight)
            title = AppProperties["app.title"] as String
            isVisible = true
            add(JScrollPane())
        }
    }

    fun open() {
        if (isFirst) {
            fileChooser.currentDirectory = File(".")
            isFirst = false
        }
        val rc = fileChooser.showOpenDialog(this.frame)
        if (rc == 0) {
            val file = fileChooser.selectedFile
            try {
                render(file)
            } catch (e: Exception) {
                e.printStackTrace()
                JOptionPane.showMessageDialog(frame, "An error occurred. See the log for details.")
            }
        }
    }

    override fun actionPerformed(e: ActionEvent) {
        when (e.actionCommand) {
            "About" -> about(frame)
            "Open" -> open()
            "Quit" -> quit(frame)
            else -> throw RuntimeException("Unknown Command")
        }
    }

    private fun render(file: File) {
        LOGGER.info("preview ${file.name}")
        val wdef: WorldDef? = worldDef(file.name)
        if (wdef != null) {
            val w = wdef.world()
            w.initialize()
            val film = AwtFilm(w.viewPlane.resolution)
            val imf = ImageFrame(film)
            w.renderer?.render(film)
            imf.repaint()
        }
    }

    companion object {
        internal val LOGGER = LoggerFactory.getLogger(this::class.java)

        val appWidth = AppProperties.getAsInteger("display.width")
        val appHeight = AppProperties.getAsInteger("display.height")

        @JvmStatic
        fun main(args: Array<String>) {
            FromTheGroundUpRayTracer()
        }
    }
}

