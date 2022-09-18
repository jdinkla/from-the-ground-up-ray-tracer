package net.dinkla.raytracer.renderer

import net.dinkla.raytracer.world.RendererFactory

enum class Renderers(val create: RendererFactory) {
    SEQUENTIAL( { r, c -> SequentialRenderer(r, c) }),
    FORK_JOIN({ r, c -> ForkJoinRenderer(r, c) }),
    PARALLEL({ r, c -> ParallelRenderer(r, c) }),
    NAIVE_COROUTINE({ r, c -> NaiveCoroutineRenderer(r, c) }),
    COROUTINE({ r, c -> CoroutineBlockRenderer(r, c) })
}
