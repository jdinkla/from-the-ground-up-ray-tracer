package net.dinkla.raytracer.lights

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.samplers.Sampler
import net.dinkla.raytracer.world.IWorld

class EnvironmentLight(override val shadows: Boolean = true) : Light {

    var sampler: Sampler? = null
    var material: IMaterial? = null
    var u: Vector3D = Vector3D.ZERO
    var v: Vector3D = Vector3D.ZERO
    var w: Vector3D = Vector3D.ZERO
    var wi: Vector3D = Vector3D.ZERO

    override fun inShadow(world: IWorld, ray: Ray, sr: IShade): Boolean {
        return world.inShadow(ray, sr, Double.MAX_VALUE)
    }

    override fun getDirection(sr: IShade): Vector3D {
        w = sr.normal.toVector3D()
        v = Vector3D(0.0034, 1.0, 0.0071).cross(w)
        u = v cross w
        val sp = sampler!!.sampleHemisphere()
        wi = (u * sp.x) + (v * sp.y) + (w * sp.z)
        return wi
    }

    override fun l(world: IWorld, sr: IShade): Color {
        return material!!.getLe(sr)
    }
}
