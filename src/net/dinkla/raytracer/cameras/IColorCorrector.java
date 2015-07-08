package net.dinkla.raytracer.cameras;

import net.dinkla.raytracer.colors.Color;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 08.06.2010
 * Time: 20:11:23
 * To change this template use File | Settings | File Templates.
 */
public interface IColorCorrector<C extends Color> {

    public C correct(final C color);

}
