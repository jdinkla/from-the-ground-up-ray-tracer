package net.dinkla.raytracer.textures

import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.utilities.Resolution

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 02.06.2010
 * Time: 21:23:45
 * To change this template use File | Settings | File Templates.
 */
// TODO Implementieren
class RectangularMap : Mapping() {

    override fun getTexelCoordinates(p: Point3D, res: Resolution): Mapping.Mapped {
        return newMapped()
    }

}
