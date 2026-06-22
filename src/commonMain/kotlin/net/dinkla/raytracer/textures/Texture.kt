package net.dinkla.raytracer.textures

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade

/**
 * A texture produces a colour for a shading point. It is the spatially-varying analogue of a
 * constant material colour: instead of a fixed `cd`, the spatially-varying materials ([net.dinkla
 * .raytracer.materials.SvMatte], [net.dinkla.raytracer.materials.SvPhong]) and their BRDFs sample a
 * [Texture] at the hit point.
 *
 * Mirrors Suffern's `Texture::get_color(const ShadeRec&)` (Ray Tracing from the Ground Up, ch. 29).
 */
interface Texture {
    fun getColor(sr: IShade): Color
}
