package net.dinkla.raytracer.textures

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade

/**
 * The simplest [Texture]: returns the same [color] everywhere, regardless of the hit point. Lets a
 * spatially-varying material stand in for a plain coloured material and is a useful test fixture.
 *
 * Mirrors Suffern's `ConstantColor` (Ray Tracing from the Ground Up, ch. 29).
 */
data class ConstantColor(
    val color: Color = Color.WHITE,
) : Texture {
    override fun getColor(sr: IShade): Color = color
}
