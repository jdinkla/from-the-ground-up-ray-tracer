package net.dinkla.raytracer.lights

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.world.IWorld

/**
 * TODO: DirectionalLight Light implementieren
 */
class DirectionalLight(override val shadows: Boolean = true) : Light {

    var ls: Double = 0.toDouble()
    var color: Color
    private var negatedDirection: Vector3D

    // TODO cleanup direction
//    val direction
//        get() = negatedDirection
//        set(v) = this.negatedDirection = -v

    init {
        ls = 1.0
        color = Color.WHITE
        negatedDirection = -Vector3D.DOWN
    }

    override fun L(world: IWorld, sr: IShade): Color = color * ls

    override fun getDirection(sr: IShade): Vector3D = negatedDirection

    override fun inShadow(world: IWorld, ray: Ray, sr: IShade
    ): Boolean {
        return world.inShadow(ray, sr, Double.MAX_VALUE)
    }

    fun setDirection(direction: Vector3D) {
        this.negatedDirection = -direction
    }

}
