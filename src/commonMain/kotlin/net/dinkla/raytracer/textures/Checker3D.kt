package net.dinkla.raytracer.textures

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import kotlin.math.floor

/**
 * A solid 3D checkerboard texture: space is divided into cubes of edge [size] and adjacent cubes
 * alternate between [color1] and [color2], decided by the parity of the summed floored coordinates
 * of the (local) hit point. A small [epsilon] nudges the coordinate off cell boundaries to avoid
 * speckling, as in Suffern.
 *
 * Mirrors Suffern's `Checker3D` (Ray Tracing from the Ground Up, ch. 29).
 */
data class Checker3D(
    val size: Double = 1.0,
    val color1: Color = Color.WHITE,
    val color2: Color = Color.BLACK,
    val epsilon: Double = EPSILON_DEFAULT,
) : Texture {
    override fun getColor(sr: IShade): Color {
        val p = sr.localHitPoint
        val x = floor((p.x + epsilon) / size).toInt()
        val y = floor((p.y + epsilon) / size).toInt()
        val z = floor((p.z + epsilon) / size).toInt()
        val isEven = (x + y + z) and 1 == 0
        return if (isEven) color1 else color2
    }

    companion object {
        /** Default boundary nudge; keeps cell edges from speckling, matching the book's value. */
        private const val EPSILON_DEFAULT = 0.001
    }
}
