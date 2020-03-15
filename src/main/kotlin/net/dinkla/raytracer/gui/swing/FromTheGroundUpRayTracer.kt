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

class FromTheGroundUpRayTracer : ActionListener {

    private var frame: JFrame? = null
    private var pane: JScrollPane? = null

    private var fc: JFileChooser? = null

    private var isFirst = true

    fun createMenuBar(): JMenuBar {
        val menuBar: JMenuBar
        var menu: JMenu
        var menuItem: JMenuItem

        menuBar = JMenuBar()

        // File
        menu = JMenu("File")
        menu.mnemonic = KeyEvent.VK_F

        menuItem = JMenuItem("Open")
        menuItem.mnemonic = KeyEvent.VK_O
        menuItem.addActionListener(this)
        menu.add(menuItem)

        menu.addSeparator()

        menuItem = JMenuItem("Quit")
        menuItem.mnemonic = KeyEvent.VK_Q
        menuItem.addActionListener(this)
        menu.add(menuItem)

        menuBar.add(menu)

        // Help
        menu = JMenu("Help")
        menu.mnemonic = KeyEvent.VK_H

        menuItem = JMenuItem("About")
        menuItem.mnemonic = KeyEvent.VK_A
        menuItem.addActionListener(this)
        menu.add(menuItem)

        menuBar.add(menu)

        return menuBar
    }

    private fun about() {
        JOptionPane.showMessageDialog(frame,
                "(c) 2012-2020 Jörn Dinkla\nwww.dinkla.net",
                "About",
                JOptionPane.PLAIN_MESSAGE)
    }

    private fun quit() {
        val n = JOptionPane.showOptionDialog(frame,
                "Do you really want to exit the application?",
                "Quit?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, null, null)
        if (n == 0) {
            System.exit(0)
        }
    }

    fun open() {
        if (null == fc) {
            fc = JFileChooser()
        }
        if (isFirst) {
            fc!!.currentDirectory = File(".")
            isFirst = false
        }
        val rc = fc!!.showOpenDialog(this.frame)
        if (rc == 0) {
            val file = fc!!.selectedFile
            try {
                render(file)
            } catch (e: Exception) {
                e.printStackTrace()
                JOptionPane.showMessageDialog(frame, "An error occurred. See the log for details.")
            }

        }
    }

    override fun actionPerformed(e: ActionEvent) {
        val cmd = e.actionCommand
        when (cmd) {
            "About" -> about()
            "Open" -> open()
            "Quit" -> quit()
            else -> throw RuntimeException("Unknown Command")
        }
    }

    private fun render(file: File) {
        val fileName = file?.name
        if (null == fileName) {
            LOGGER.warn("preview fileChosen is null")
            return
        }
        LOGGER.info("preview $fileName")
        val wdef: WorldDef? = worldDef(fileName)
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

        @JvmStatic
        fun main(args: Array<String>) {
            val gui = FromTheGroundUpRayTracer()
            gui.frame = JFrame()
            gui.frame!!.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            gui.frame!!.jMenuBar = gui.createMenuBar()
            val width = AppProperties.getAsInteger("display.width")
            val height = AppProperties.getAsInteger("display.height")
            gui.frame!!.setSize(width, height)
            gui.frame!!.title = AppProperties["app.title"] as String?
            gui.frame!!.isVisible = true
            gui.pane = JScrollPane()
            gui.frame!!.add(gui.pane)
        }
    }

}

