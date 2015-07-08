package net.dinkla.raytracer.materials;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.colors.RGBColor;
import net.dinkla.raytracer.hits.Shade;
import net.dinkla.raytracer.worlds.World;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 10.04.2010
 * Time: 15:59:02
 * To change this template use File | Settings | File Templates.
 */
abstract public class Material<C extends Color> {

    public boolean shadows;

    public Material() {
        shadows = true;
    }

    abstract public C shade(World<C> world, Shade sr);

    abstract public C areaLightShade(World<C> world, Shade sr);

    //abstract public RGBColor pathShade(Shade sr);
    
    abstract public C getLe(Shade sr);

}
