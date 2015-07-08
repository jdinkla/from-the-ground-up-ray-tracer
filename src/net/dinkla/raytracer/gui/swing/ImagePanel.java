package net.dinkla.raytracer.gui.swing;

/*
 * Copyright (c) 2012 by Jörn Dinkla, www.dinkla.com, All rights reserved.
 */

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.films.BufferedImageFilm;
import net.dinkla.raytracer.films.IFilm;
import net.dinkla.raytracer.utilities.Resolution;

import javax.swing.*;

public class ImagePanel extends JPanel implements IFilm {

    final protected BufferedImageFilm film;
    final protected ImageCanvas canvas;

    public ImagePanel(final Resolution resolution) {
        film = new BufferedImageFilm();
        film.initialize(1, resolution);
        canvas = new ImageCanvas(film.getImg());
        add(canvas);
    }

    public void initialize(int numFrames, Resolution resolution) {
        film.initialize(numFrames, resolution);
    }

    public void finish() {
        film.finish();
    }

    public void setPixel(int frame, int x, int y, Color color) {
        film.setPixel(frame, x, y, color);
        canvas.repaint();
    }

    public void setBlock(int frame, int x, int y, int width, int height, Color color) {
        film.setBlock(frame, x, y, width, height, color);
        canvas.repaint();
    }

    public BufferedImageFilm getFilm() {
        return film;
    }

    public Resolution getResolution() {
        return film.getResolution();
    }


}
