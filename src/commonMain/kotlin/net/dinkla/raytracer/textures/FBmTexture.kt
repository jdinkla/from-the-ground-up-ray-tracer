package net.dinkla.raytracer.textures

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.noise.LatticeNoise

/**
 * A fractal-Brownian-motion (fBm) texture: the [noise]'s fractal sum at the local hit point is mapped
 * onto a colour ramp from [minColor] to [maxColor]. fBm's signed `[-1, 1]` output is first remapped to
 * `[0, 1]`, then clamped against [minValue]/[maxValue] and renormalised, so the caller can stretch or
 * window the contrast. The result is a soft, cloud-like colour variation.
 *
 * Mirrors Suffern's `FBmTexture` (Ray Tracing from the Ground Up, ch. 31).
 */
data class FBmTexture(
    val noise: LatticeNoise,
    val minColor: Color = Color.BLACK,
    val maxColor: Color = Color.WHITE,
    val minValue: Double = 0.0,
    val maxValue: Double = 1.0,
) : Texture {
    override fun getColor(sr: IShade): Color {
        val signed = noise.fbm(sr.localHitPoint)
        return colorFor(signed)
    }

    /**
     * Maps a signed fBm value in `[-1, 1]` to the output colour. Pure and side-effect free so the
     * value→colour mapping is directly unit-testable. The value is remapped to `[0, 1]`, windowed to
     * `[minValue, maxValue]`, renormalised, then used to lerp [minColor]→[maxColor].
     */
    fun colorFor(signed: Double): Color {
        val unit = (signed + 1.0) * HALF
        val windowed = unit.coerceIn(minValue, maxValue)
        val t = if (maxValue > minValue) (windowed - minValue) / (maxValue - minValue) else 0.0
        return minColor * (1.0 - t) + maxColor * t
    }

    companion object {
        private const val HALF = 0.5
    }
}
