package net.dinkla.raytracer.renderer

import net.dinkla.raytracer.world.RendererFactory

enum class Renderers(val create: RendererFactory) {
    SEQUENTIAL( { r, c -> SequentialRenderer(r, c) }),
    FORK_JOIN({ r, c -> ForkJoinRenderer(r, c) }),
    PARALLEL({ r, c -> ParallelRenderer(r, c) }),
    COROUTINE({ r, c -> CoroutineRenderer(r, c) })
}
