package net.dinkla.raytracer.textures

import net.dinkla.raytracer.math.MathUtils
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.utilities.Resolution
import kotlin.math.acos
import kotlin.math.sqrt

class LightProbeMap : Mapping() {

    private var type: Type = Type.LIGHT_PROBE

    enum class Type {
        LIGHT_PROBE,
        PANORAMIC
    }

    override fun getTexelCoordinates(p: Point3D, res: Resolution): Mapped {
        val d = sqrt(p.x * p.x + p.y * p.y)
        val sinBeta = p.y / d
        val cosBeta = p.x / d

        val alpha: Double = when (type) {
            Type.LIGHT_PROBE -> acos(p.z)
            Type.PANORAMIC -> acos(-p.z)
        }

        val r = alpha * MathUtils.INV_PI
        val u = (1.0 + r * cosBeta) * 0.5
        val v = (1.0 + r * sinBeta) * 0.5

        val row = ((res.vres - 1) * v).toInt()
        val column = ((res.hres - 1) * u).toInt()

        return Mapped(row, column)
    }
}



