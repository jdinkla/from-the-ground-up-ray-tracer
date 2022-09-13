package net.dinkla.raytracer.cameras.render

import net.dinkla.raytracer.cameras.IColorCorrector

enum class Renderers(val create: (ISingleRayRenderer, IColorCorrector) -> IRenderer) {
    SEQUENTIAL( { r, c -> SequentialRenderer(r, c) }),
    FORK_JOIN({ r, c -> ForkJoinRenderer(r, c) }),
    PARALLEL({ r, c -> ParallelRenderer(r, c) }),
    COROUTINE({ r, c -> CoroutineRenderer(r, c) })
}