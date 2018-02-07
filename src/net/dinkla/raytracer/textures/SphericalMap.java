package net.dinkla.raytracer.textures;

import net.dinkla.raytracer.math.MathUtils;
import net.dinkla.raytracer.math.Point3D;
import net.dinkla.raytracer.utilities.Resolution;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 02.06.2010
 * Time: 21:13:41
 * To change this template use File | Settings | File Templates.
 */
public class SphericalMap extends Mapping {

    // TODO move to MathUtils or Constants
    final double 	invTWO_PI 	= 0.1591549430918953358;
    final double 	invPI 		= 0.3183098861837906715;

    @Override
    public Mapped getTexelCoordinates(Point3D p, Resolution res) {
        Mapped result = new Mapped();

        double theta =  Math.acos(p.getY());
        double phi =  Math.atan2(p.getX(), p.getZ());
        if (phi < 0) {
            phi += 2.0 * Math.PI;
        }

//        double u = phi * (1.0 / (2.0 *  Math.PI));
        double u =  (phi * invTWO_PI);
//        double v = 1 - theta * MathUtils.INV_PI;
        double v =  (1 - theta * invPI);

        result.column = (int) ((res.hres -1) * u);
        result.row = (int) ((res.vres -1) * v);

        return result;
    }
}
