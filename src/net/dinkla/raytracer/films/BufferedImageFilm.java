package net.dinkla.raytracer.films;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.utilities.Resolution;

import java.awt.image.BufferedImage;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 20.05.2010
 * Time: 18:00:22
 * To change this template use File | Settings | File Templates.
 */
public class BufferedImageFilm implements IFilm {

    protected BufferedImage img;
    protected Resolution resolution;

    public BufferedImageFilm() {
    }

    public void initialize(int numFrames, Resolution resolution) {
        this.resolution = resolution;
        img = new BufferedImage(resolution.hres, resolution.vres, BufferedImage.TYPE_INT_RGB);
    }

    public void finish() {
    }

    public void setPixel(int frame, int x, int y, Color color) {
        img.setRGB(x, resolution.vres - 1 - y, color.asInt());
    }

    public void setBlock(int frame, int x, int y, int width, int height, Color color) {
        Object pixel = null;
        pixel = img.getColorModel().getDataElements(color.asInt(), pixel);
        for (int j=0; j < height; j++) {
            for (int i=0; i < width; i++) {
                img.getRaster().setDataElements(x+i, resolution.vres - 1 - y - j, pixel);
            }
        }
    }

    public BufferedImage getImg() {
        return img;
    }

    public Resolution getResolution() {
        return resolution;
    }
}
