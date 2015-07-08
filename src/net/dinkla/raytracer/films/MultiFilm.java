package net.dinkla.raytracer.films;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.colors.RGBColor;
import net.dinkla.raytracer.utilities.Resolution;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO Geht das besser zu implementieren ?
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 20.05.2010
 * Time: 18:19:34
 * To change this template use File | Settings | File Templates.
 */
public class MultiFilm implements IFilm {

    protected List<IFilm> films;

    public MultiFilm() {
        films = new ArrayList<IFilm>();
    }

    public void add(IFilm film) {
        films.add(film);    
    }

    public void initialize(int numFrames, Resolution resolution) {
        for (IFilm film : films) {
            film.initialize(numFrames, resolution);
        }
    }

    public void finish() {
        for (IFilm film : films) {
            film.finish();
        }

    }

    public void setPixel(int frame, int x, int y, Color color) {
        for (IFilm film : films) {
            film.setPixel(frame, x, y, color);
        }
    }

    public void setBlock(int frame, int x, int y, int width, int height, Color color) {
        for (IFilm film : films) {
            film.setBlock(frame, x, y, width, height, color);
        }
    }

    public Resolution getResolution() {
        return films.get(0).getResolution();
    }
}
