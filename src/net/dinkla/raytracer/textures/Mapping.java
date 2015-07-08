package net.dinkla.raytracer.textures;

import net.dinkla.raytracer.math.Point3D;
import net.dinkla.raytracer.utilities.Resolution;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 02.06.2010
 * Time: 21:11:36
 * To change this template use File | Settings | File Templates.
 */
abstract public class Mapping {

    public class Mapped {
        public int row;
        public int column;
    }

    public abstract Mapped getTexelCoordinates(final Point3D p, final Resolution res); 

}
