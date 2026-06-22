package net.dinkla.raytracer.textures

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.noise.LatticeNoise
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.sqrt

/**
 * A wood texture: concentric growth rings around the y axis (the trunk axis) blended between a light
 * [lightColor] and a dark [darkColor]. The ring radius is the distance from the trunk axis in the xz
 * plane, scaled by [ringFrequency] and warped by the [noise]'s turbulence (scaled by [ringWarp]) so the
 * rings wobble like real grain instead of being perfect circles. The fractional part of the warped
 * radius selects a position within a ring, which a sine ring profile turns into the light/dark blend.
 *
 * Mirrors Suffern's wood texture (Ray Tracing from the Ground Up, ch. 31), simplified to the
 * ring-around-the-y-axis core.
 */
data class Wood(
    val noise: LatticeNoise,
    val lightColor: Color = DEFAULT_LIGHT_COLOR,
    val darkColor: Color = DEFAULT_DARK_COLOR,
    val ringFrequency: Double = 1.0,
    val ringWarp: Double = DEFAULT_RING_WARP,
) : Texture {
    override fun getColor(sr: IShade): Color {
        val p = sr.localHitPoint
        val radius = sqrt(p.x * p.x + p.z * p.z)
        val turbulence = noise.turbulence(p)
        return colorFor(radius, turbulence)
    }

    /**
     * The wood colour for a trunk-axis [radius] perturbed by a non-negative [turbulence] value. Pure
     * and side-effect free so the ring computation is directly unit-testable: it warps the radius,
     * forms the ring profile, and lerps [lightColor]→[darkColor]. The blend factor stays in `[0, 1]`.
     */
    fun colorFor(
        radius: Double,
        turbulence: Double,
    ): Color {
        val warpedRadius = radius * ringFrequency + ringWarp * turbulence
        val ringPosition = warpedRadius - floor(warpedRadius)

        // Triangle ring profile in [0,1]: 0 at the ring edges, 1 at the ring centre.
        val ring = 1.0 - abs(ringPosition - HALF) * 2.0
        return lightColor * (1.0 - ring) + darkColor * ring
    }

    companion object {
        /** Default strength of the turbulence warp applied to the ring radius. */
        const val DEFAULT_RING_WARP = 0.4

        /** Default pale heartwood colour. */
        val DEFAULT_LIGHT_COLOR = Color(0.66, 0.45, 0.24)

        /** Default dark grain colour. */
        val DEFAULT_DARK_COLOR = Color(0.30, 0.16, 0.06)

        private const val HALF = 0.5
    }
}
