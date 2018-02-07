package net.dinkla.raytracer.films;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.utilities.Resolution;

public interface IFilm {

    void initialize(int numFrames, Resolution resolution);
    void finish(); // finalize is already present in Java

    //void setPixel(int frame, int x, int y, int rgb);
    void setPixel(int frame, int x, int y, Color color);
    void setBlock(int frame, int x, int y, int width, int height, Color color);

    Resolution getResolution();
}