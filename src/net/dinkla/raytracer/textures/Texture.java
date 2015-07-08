package net.dinkla.raytracer.textures;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.hits.Shade;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 02.06.2010
 * Time: 20:46:34
 * To change this template use File | Settings | File Templates.
 */
public abstract class Texture<C extends Color> {

    abstract public C getColor(Shade sr);
}
