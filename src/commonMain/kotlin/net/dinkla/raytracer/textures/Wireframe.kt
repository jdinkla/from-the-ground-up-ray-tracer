package net.dinkla.raytracer.textures

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import kotlin.math.floor

/**
 * A wireframe texture: the xz-plane is tiled into cells of edge [size], and points within
 * [wireWidth] of a cell boundary are painted [wireColor] while cell interiors are painted
 * [fillColor]. The result reads as a grid of wires (cell outlines) over a solid fill — the book's
 * wireframe look applied to a surface via its local coordinates.
 *
 * The hit point is read in local coordinates and only its x and z are used, so the wires tile a
 * horizontal plane uniformly. As with the other procedural textures, objects are assumed
 * origin-centred and axis-aligned (TASK-18.1 limitation).
 *
 * Mirrors the wireframe idea in Suffern's book (Ray Tracing from the Ground Up, ch. 19/29).
 */
data class Wireframe(
    val size: Double = 1.0,
    val wireWidth: Double = 0.05,
    val fillColor: Color = Color.WHITE,
    val wireColor: Color = Color.BLACK,
) : Texture {
    override fun getColor(sr: IShade): Color = colorAt(sr.localHitPoint.x, sr.localHitPoint.z)

    /**
     * The wireframe colour for a point at plane coordinates ([x], [z]): [wireColor] when the point is
     * within [wireWidth] of a cell boundary along either axis, else [fillColor]. Pure and side-effect
     * free so the edge-vs-interior decision can be unit-tested directly.
     */
    fun colorAt(
        x: Double,
        z: Double,
    ): Color = if (nearBoundary(x) || nearBoundary(z)) wireColor else fillColor

    /** True when [coordinate] lies within [wireWidth] of the nearest cell boundary. */
    private fun nearBoundary(coordinate: Double): Boolean {
        val cell = coordinate / size
        val fractional = cell - floor(cell)
        val distanceToBoundary = minOf(fractional, 1.0 - fractional) * size
        return distanceToBoundary < wireWidth
    }
}
