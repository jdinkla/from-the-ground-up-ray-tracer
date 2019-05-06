package net.dinkla.raytracer.textures

import net.dinkla.raytracer.math.MathUtils.INV_PI
import net.dinkla.raytracer.math.MathUtils.INV_TWO_PI
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.utilities.Resolution

class SphericalMap : Mapping() {

    override fun getTexelCoordinates(p: Point3D, res: Resolution): Mapping.Mapped {

        val theta = Math.acos(p.y)
        var phi = Math.atan2(p.x, p.z)
        if (phi < 0) {
            phi += 2.0 * Math.PI
        }

        //        double u = phi * (1.0 / (2.0 *  Math.PI));
        val u = phi * INV_TWO_PI
        //        double v = 1 - theta * MathUtils.INV_PI;
        val v = 1 - theta * INV_PI

        val row = ((res.vres - 1) * v).toInt()
        val column = ((res.hres - 1) * u).toInt()

        return Mapped(row, column)
    }
}
