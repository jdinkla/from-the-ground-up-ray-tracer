package net.dinkla.raytracer.films;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.colors.RGBColor;
import net.dinkla.raytracer.utilities.Resolution;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 19.05.2010
 * Time: 22:20:25
 * To change this template use File | Settings | File Templates.
 */
public class PngFilm implements IFilm {

    protected String fileName;
    protected BufferedImageFilm film;

    public PngFilm(final String fileName) {
        this.fileName = fileName;
        film = new BufferedImageFilm();
    }

    public PngFilm(String fileName, BufferedImageFilm film) {
        this.fileName = fileName;
        this.film = film;
    }

    public void initialize(int numFrames, Resolution resolution) {
        film.initialize(numFrames, resolution);
    }

    public void finish() {
        File file = new File(fileName);
        try {
            ImageIO.write(film.getImg(), "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPixel(int frame, int x, int y, Color color) {
        film.setPixel(frame, x, y, color);
    }

    public void setBlock(int frame, int x, int y, int width, int height, Color color) {
        film.setBlock(frame, x, y, width, height, color);
    }

    public Resolution getResolution() {
        return film.getResolution();
    }
}
