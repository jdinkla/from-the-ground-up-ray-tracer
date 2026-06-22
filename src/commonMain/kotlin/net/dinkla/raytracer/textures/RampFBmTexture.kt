package net.dinkla.raytracer.textures

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.noise.LatticeNoise
import kotlin.math.sin

/**
 * A marble texture: a colour [ramp] indexed by a sine wave running along one axis of the local hit
 * point, with the wave's phase perturbed by the [noise]'s fractal sum. Where the surface is unperturbed
 * the ramp produces regular veins; the fBm warp bends and folds those veins into the irregular marble
 * pattern. Reuses TASK-18.2's [Ramp] for the scalar→colour mapping (this is the noise-driven form the
 * value-only `Ramp` deliberately left to this task).
 *
 * The index is `(1 + sin(axisCoordinate * frequency + amplitude * fbm)) / 2`, mapped into the ramp's
 * `[0, 1]` domain.
 *
 * Mirrors Suffern's marble / ramp-fBm texture (Ray Tracing from the Ground Up, ch. 31).
 */
data class RampFBmTexture(
    val noise: LatticeNoise,
    val ramp: Ramp = Ramp(),
    val axis: Ramp.Axis = Ramp.Axis.Y,
    val frequency: Double = 1.0,
    val amplitude: Double = DEFAULT_AMPLITUDE,
) : Texture {
    override fun getColor(sr: IShade): Color {
        val coordinate = sr.localHitPoint.component(axis)
        val noiseValue = noise.fbm(sr.localHitPoint)
        return colorFor(coordinate, noiseValue)
    }

    /**
     * The marble colour for a [coordinate] along the chosen axis, perturbed by a fractal-noise value
     * [noiseValue]. Pure and side-effect free so the index computation is directly unit-testable: it
     * forms the sine index, normalises it to `[0, 1]`, and reads the [ramp] there.
     */
    fun colorFor(
        coordinate: Double,
        noiseValue: Double,
    ): Color {
        val phase = coordinate * frequency + amplitude * noiseValue
        val index = (1.0 + sin(phase)) * HALF
        return ramp.colorAt(index)
    }

    private fun Point3D.component(axis: Ramp.Axis): Double =
        when (axis) {
            Ramp.Axis.X -> x
            Ramp.Axis.Y -> y
            Ramp.Axis.Z -> z
        }

    companion object {
        /** Default strength of the fBm warp applied to the sine phase; higher values fold the veins more. */
        const val DEFAULT_AMPLITUDE = 2.0

        private const val HALF = 0.5
    }
}
