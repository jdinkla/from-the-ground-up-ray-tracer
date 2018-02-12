package net.dinkla.raytracer.textures

import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.utilities.Resolution

class SphericalMap : Mapping() {

    // TODO move to MathUtils or Constants
    internal val invTWO_PI = 0.1591549430918953358
    internal val invPI = 0.3183098861837906715

    override fun getTexelCoordinates(p: Point3D, res: Resolution): Mapping.Mapped {
        val result = newMapped()

        val theta = Math.acos(p.y)
        var phi = Math.atan2(p.x, p.z)
        if (phi < 0) {
            phi += 2.0 * Math.PI
        }

        //        double u = phi * (1.0 / (2.0 *  Math.PI));
        val u = phi * invTWO_PI
        //        double v = 1 - theta * MathUtils.INV_PI;
        val v = 1 - theta * invPI

        result.column = ((res.hres - 1) * u).toInt()
        result.row = ((res.vres - 1) * v).toInt()

        return result
    }
}
