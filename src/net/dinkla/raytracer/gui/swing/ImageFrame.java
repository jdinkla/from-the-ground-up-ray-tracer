package net.dinkla.raytracer.gui.swing;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.films.BufferedImageFilm;
import net.dinkla.raytracer.films.IFilm;
import net.dinkla.raytracer.utilities.Resolution;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 11.04.2010
 * Time: 18:32:08
 * To change this template use File | Settings | File Templates.
 */
public class ImageFrame extends JFrame implements IFilm {

    final protected BufferedImageFilm film;
    final protected ImageCanvas canvas;
    final protected RayTracerParametersBean paramsBean;
    
    protected int counter;
    final static int steps = 100;
    final protected boolean isMainFrame;

    public ImageFrame(final Resolution resolution, final boolean isMainFrame, final RayTracerParametersBean paramsBean) {
        this.isMainFrame = isMainFrame;
        this.paramsBean = paramsBean;

        film = new BufferedImageFilm();
        film.initialize(1, resolution);
        canvas = new ImageCanvas(film.getImg());
        add(canvas);
        setSize(resolution.hres(), resolution.vres()+22);
        addWindowListener(
                new WindowAdapter() {
                    public void windowClosing(WindowEvent ev) {
                        dispose();
                        if (isMainFrame) {
                            System.exit(0);
                        }
                    }
                });
        setVisible(true);

        counter = 0;
    }

    public void initialize(int numFrames, Resolution resolution) {
        film.initialize(numFrames, resolution);
    }

    public void finish() {
        film.finish();
    }

    public void setPixel(final int frame, final int x, final int y, final Color color) {
        film.setPixel(frame, x, y, color);
        canvas.repaint();
    }

    public void setBlock(int frame, int x, int y, int width, int height, Color color) {
        film.setBlock(frame, x, y, width, height, color);
        canvas.repaint();
    }

    @Override
    public void repaint() {
        super.repaint();
        canvas.repaint();
    }

    public BufferedImageFilm getFilm() {
        return film;
    }

    public Resolution getResolution() {
        return film.getResolution();
    }
}
