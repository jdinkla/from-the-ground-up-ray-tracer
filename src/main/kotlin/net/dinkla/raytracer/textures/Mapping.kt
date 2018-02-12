package net.dinkla.raytracer.textures

import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.utilities.Resolution

abstract class Mapping {

    inner class Mapped {
        var row: Int = 0
        var column: Int = 0
    }

    fun newMapped() = Mapped()

    abstract fun getTexelCoordinates(p: Point3D, res: Resolution): Mapped

}
