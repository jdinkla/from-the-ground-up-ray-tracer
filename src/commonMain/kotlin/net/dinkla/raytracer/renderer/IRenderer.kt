package net.dinkla.raytracer.renderer

import net.dinkla.raytracer.films.IFilm

interface IRenderer {
    /**
     * Renders the whole image into [film]. Polls [cancellation] at a coarse granularity (per row or
     * per block) and returns early — leaving the film partially filled — once cancellation is
     * requested, instead of running to completion. The default [NoCancellation] makes the check a
     * no-op, so callers that do not cancel behave exactly as before.
     */
    fun render(
        film: IFilm,
        cancellation: CancellationToken = NoCancellation,
    )
}
