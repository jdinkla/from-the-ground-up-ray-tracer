package net.dinkla.raytracer.textures;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.hits.Shade;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 02.06.2010
 * Time: 20:47:44
 * To change this template use File | Settings | File Templates.
 */
public class ConstantColor<C extends Color> extends Texture<C> {

    protected C color;

    public ConstantColor(C color) {
        this.color = color;
    }

    @Override
    public C getColor(Shade sr) {
        return color;
    }

    public void setColor(C color) {
        this.color = color;
    }

}
