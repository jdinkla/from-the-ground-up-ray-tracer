package net.dinkla.raytracer.textures

import net.dinkla.raytracer.math.MathUtils
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.utilities.Resolution

class LightProbeMap : Mapping() {

    var type: Type

    enum class Type {
        LIGHT_PROBE,
        PANORAMIC
    }

    init {
        this.type = Type.LIGHT_PROBE
    }

    override fun getTexelCoordinates(p: Point3D, res: Resolution): Mapping.Mapped {
        val result = newMapped()

        val d = Math.sqrt(p.x * p.x + p.y * p.y)
        val sinBeta = p.y / d
        val cosBeta = p.x / d

        val alpha: Double

        when (type) {
            LightProbeMap.Type.LIGHT_PROBE -> alpha = Math.acos(p.z)
            LightProbeMap.Type.PANORAMIC -> alpha = Math.acos(-p.z)
            else -> throw RuntimeException("LightProbeMap.getTexelCoordinates unknown type")
        }

        val r = alpha * MathUtils.INV_PI
        val u = (1.0 + r * cosBeta) * 0.5
        val v = (1.0 + r * sinBeta) * 0.5

        result.column = ((res.hres - 1) * u).toInt()
        result.row = ((res.vres - 1) * v).toInt()

        return result
    }
}



