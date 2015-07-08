package net.dinkla.raytracer.cameras.render;

import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.math.Ray;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 08.06.2010
 * Time: 19:32:46
 * To change this template use File | Settings | File Templates.
 */
public interface ISingleRayRenderer {

    public Color render(int r, int c);
    
}
