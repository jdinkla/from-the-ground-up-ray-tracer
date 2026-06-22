package net.dinkla.raytracer.textures

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.noise.LatticeNoise
import kotlin.math.floor

/**
 * A wrapped-fBm texture: the [noise]'s fractal sum is amplified by [expansionNumber] and the integer
 * part discarded (`value = expansion * fbm; value -= floor(value)`). Wrapping a smooth field through
 * the saw-tooth discontinuity at each integer produces sharp, marbled colour transitions far busier
 * than plain fBm — the basis of the marble look. The wrapped value in `[0, 1)` lerps [minColor] to
 * [maxColor].
 *
 * Mirrors Suffern's `WrappedFBmTexture` (Ray Tracing from the Ground Up, ch. 31).
 */
data class WrappedFBmTexture(
    val noise: LatticeNoise,
    val minColor: Color = Color.BLACK,
    val maxColor: Color = Color.WHITE,
    val expansionNumber: Double = DEFAULT_EXPANSION,
) : Texture {
    override fun getColor(sr: IShade): Color {
        val signed = noise.fbm(sr.localHitPoint)
        return colorFor(signed)
    }

    /**
     * Maps a signed fBm value to the output colour by amplifying it by [expansionNumber] and keeping
     * the fractional part. Pure and side-effect free so the wrapping logic is directly unit-testable.
     */
    fun colorFor(signed: Double): Color {
        val expanded = expansionNumber * signed
        val wrapped = expanded - floor(expanded)
        return minColor * (1.0 - wrapped) + maxColor * wrapped
    }

    companion object {
        /** Default amplification before wrapping; higher values pack more bands into the field. */
        const val DEFAULT_EXPANSION = 2.0
    }
}
