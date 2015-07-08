package net.dinkla.raytracer.cameras.render;

import net.dinkla.raytracer.films.IFilm;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 08.06.2010
 * Time: 19:58:34
 * To change this template use File | Settings | File Templates.
 */
public interface IRenderer {

    public void render(IFilm film, final int frame);

}
