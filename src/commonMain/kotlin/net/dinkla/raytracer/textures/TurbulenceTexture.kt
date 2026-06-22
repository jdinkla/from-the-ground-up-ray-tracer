package net.dinkla.raytracer.textures

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.noise.LatticeNoise

/**
 * A turbulence texture: the [noise]'s turbulence (sum of `|noise|` octaves, always `>= 0`) at the
 * local hit point scales a colour lerp from [minColor] to [maxColor]. Turbulence already lives in
 * `[0, 1]`, so it can be windowed via [minValue]/[maxValue] and renormalised for contrast. The
 * absolute-value creases give the characteristic billowy, smoky look.
 *
 * Mirrors Suffern's `TurbulenceTexture` (Ray Tracing from the Ground Up, ch. 31).
 */
data class TurbulenceTexture(
    val noise: LatticeNoise,
    val minColor: Color = Color.BLACK,
    val maxColor: Color = Color.WHITE,
    val minValue: Double = 0.0,
    val maxValue: Double = 1.0,
) : Texture {
    override fun getColor(sr: IShade): Color {
        val value = noise.turbulence(sr.localHitPoint)
        return colorFor(value)
    }

    /**
     * Maps a turbulence value in `[0, 1]` to the output colour. Pure and side-effect free so the
     * value→colour mapping is directly unit-testable. The value is windowed to `[minValue, maxValue]`,
     * renormalised, then used to lerp [minColor]→[maxColor].
     */
    fun colorFor(value: Double): Color {
        val windowed = value.coerceIn(minValue, maxValue)
        val t = if (maxValue > minValue) (windowed - minValue) / (maxValue - minValue) else 0.0
        return minColor * (1.0 - t) + maxColor * t
    }
}
