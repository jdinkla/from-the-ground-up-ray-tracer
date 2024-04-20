package net.dinkla.raytracer.renderer

import net.dinkla.raytracer.world.RendererCreator

enum class Renderer(val creator: RendererCreator) {
    SEQUENTIAL(::SequentialRenderer),
    FORK_JOIN(::ForkJoinRenderer),
    PARALLEL(::ParallelRenderer),
    NAIVE_COROUTINE(::NaiveCoroutineRenderer),
    COROUTINE(::CoroutineBlockRenderer)
}
