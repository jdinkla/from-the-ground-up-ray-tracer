package net.dinkla.raytracer.cameras.render;

import net.dinkla.raytracer.cameras.IColorCorrector;
import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.films.IFilm;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 08.06.2010
 * Time: 19:59:06
 * To change this template use File | Settings | File Templates.
 */
public class SequentialRenderer implements IRenderer {

    final protected ISingleRayRenderer render;
    final protected IColorCorrector corrector;

    public double exposureTime = 1.0;

    public SequentialRenderer(final ISingleRayRenderer render, final IColorCorrector corrector) {
        this.render = render;
        this.corrector = corrector;
    }

    public void render(IFilm film, int frame) {
        for (int r = 0; r < film.getResolution().vres; r++) {
            for (int c = 0; c < film.getResolution().hres; c++) {
                Color color =  render.render(r, c);
                color =  color.mult(exposureTime);
                color = corrector.correct(color);
                film.setPixel(frame, c, r, color);
            }
        }
    }

}
