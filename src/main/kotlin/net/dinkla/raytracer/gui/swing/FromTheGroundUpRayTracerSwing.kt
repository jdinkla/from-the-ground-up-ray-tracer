package net.dinkla.raytracer.gui.swing

/*
 * Copyright (c) 2012, 2015, 2018 by Jörn Dinkla, www.dinkla.com, All rights reserved.
 */

import net.dinkla.raytracer.films.PngFilm
import net.dinkla.raytracer.gui.GuiUtilities
import net.dinkla.raytracer.utilities.AppProperties
import net.dinkla.raytracer.worlds.World
import net.dinkla.raytracer.worlds.WorldBuilder
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import java.io.File
import javax.swing.*

class FromTheGroundUpRayTracerSwing : ActionListener {

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

    fun about() {
        JOptionPane.showMessageDialog(frame,
                "(c) 2012, 2015, 2018 Jörn Dinkla\nwww.dinkla.net",
                "About",
                JOptionPane.PLAIN_MESSAGE)
    }

    fun quit() {
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

    fun render(file: File) {
        val w = World()
        val builder = WorldBuilder(w)
        builder.build(file)
        w.initialize()

        val vp = w.viewPlane
        val imf = ImageFrame(vp.resolution)

        w.render(imf)
        val fileName2 = GuiUtilities.getOutputPngFileName(file.name)
        val png = PngFilm(imf.film)
        png.saveAsPng(fileName2)
        imf.repaint()
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val gui = FromTheGroundUpRayTracerSwing()
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

            /*
        gui.frame.addKeyListener(
                new KeyListener() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        System.out.println(e);
                        System.out.println(e.getExtendedKeyCode());
                        switch(e.getKeyChar()) {
                            case 'a':
                                System.out.println("L");
                                w.getCamera().
                                break;
                            case 'direction':
                                System.out.println("R");
                                break;
                        }
                    }

                    @Override
                    public void keyPressed(KeyEvent e) {
                        //System.out.println(e);
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
                        //System.out.println(e);
                    }
                }
        );
        */

        }
    }

}
