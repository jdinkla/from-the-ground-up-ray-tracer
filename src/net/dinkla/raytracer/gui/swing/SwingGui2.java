package net.dinkla.raytracer.gui.swing;

/*
 * Copyright (c) 2012, 2015 by Jörn Dinkla, www.dinkla.com, All rights reserved.
 */

import net.dinkla.raytracer.ViewPlane;
import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.films.PngFilm;
import net.dinkla.raytracer.gui.GuiUtilities;
import net.dinkla.raytracer.utilities.AppProperties;
import net.dinkla.raytracer.worlds.World;
import net.dinkla.raytracer.worlds.WorldBuilder;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

public class SwingGui2 implements ActionListener {

    private JFrame frame;
    private JScrollPane pane;

    private JFileChooser fc;

    private boolean isFirst = true;

    public SwingGui2() {
    }

    public JMenuBar createMenuBar() {
        JMenuBar menuBar;
        JMenu menu;
        JMenuItem menuItem;

        menuBar = new JMenuBar();

        // File
        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);

        menuItem = new JMenuItem("Open");
        menuItem.setMnemonic(KeyEvent.VK_O);
        menuItem.addActionListener(this);
        menu.add(menuItem);

        menu.addSeparator();

        menuItem = new JMenuItem("Quit");
        menuItem.setMnemonic(KeyEvent.VK_Q);
        menuItem.addActionListener(this);
        menu.add(menuItem);

        menuBar.add(menu);

        // Help
        menu = new JMenu("Help");
        menu.setMnemonic(KeyEvent.VK_H);

        menuItem = new JMenuItem("About");
        menuItem.setMnemonic(KeyEvent.VK_A);
        menuItem.addActionListener(this);
        menu.add(menuItem);

        menuBar.add(menu);

        return menuBar;
    }

    public void about() {
        JOptionPane.showMessageDialog(frame,
                "(c) 2012, 2015 Joern Dinkla\nwww.dinkla.net",
                "About",
                JOptionPane.PLAIN_MESSAGE);
    }

    public void quit() {
        int n = JOptionPane.showOptionDialog(frame,
                "Do you really want to exit the application?",
                "Quit?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                null);
        if (n == 0) {
            System.exit(0);
        }
    }

    public void open() {
        if (null == fc) {
            fc = new JFileChooser();
        }
        if (isFirst) {
            fc.setCurrentDirectory(new File("."));
            isFirst = false;
        }
        int rc = fc.showOpenDialog(this.frame);
        if (rc == 0) {
            final File file = fc.getSelectedFile();
            try {
                render(file);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "An error occurred. See the log for details.");
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        final String cmd = e.getActionCommand();
        switch (cmd) {
            case "About":
                about();
                break;
            case "Open":
                open();
                break;
            case "Quit":
                quit();
                break;
            default:
                throw new RuntimeException("Unknown Command");
        }
    }

    public void render(File file) {
        World<Color> w = new World<Color>();
        WorldBuilder<Color> builder = new WorldBuilder<Color>(w);
        builder.build(file);
        w.initialize();

        ViewPlane vp = w.getViewPlane();
        ImageFrame imf = new ImageFrame(vp.resolution, false, null);

        if (w.isDynamic()) {

//            w.render(imf);
//            imf.repaint();
//            imf.finish();

            w.set();
            while (w.hasNext()) {
                w.render(imf);
                imf.repaint();
                w.step();
                SwingUtilities.invokeLater(() -> {
                }
                );
            }
            imf.finish();
        } else {
            w.render(imf);

            String fileName2 = GuiUtilities.getOutputPngFileName(file.getName());
            PngFilm png = new PngFilm(fileName2, imf.getFilm());
            png.finish();

            imf.repaint();
            imf.finish();
        }

    }

    public static void main(String[] args) {
        Color.black = Color.BLACK;
        Color.error = Color.RED;
        Color.white = Color.WHITE;

        SwingGui2 gui = new SwingGui2();
        gui.frame = new JFrame();
        gui.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.frame.setJMenuBar(gui.createMenuBar());
        final int width = AppProperties.getAsInteger("display.width");
        final int height = AppProperties.getAsInteger("display.height");
        gui.frame.setSize(width, height);
        gui.frame.setTitle((String) AppProperties.get("app.title"));
        gui.frame.setVisible(true);
        gui.pane = new JScrollPane();
        gui.frame.add(gui.pane);

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
                            case 'd':
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
