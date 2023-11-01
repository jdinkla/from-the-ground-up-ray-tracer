package net.dinkla.raytracer.renderer

import net.dinkla.raytracer.cameras.IColorCorrector
import net.dinkla.raytracer.world.RendererCreator

actual enum class Renderer {
    SEQUENTIAL,
    FORK_JOIN,
    PARALLEL,
    NAIVE_COROUTINE,
    COROUTINE
}

actual fun createRenderer(renderer: Renderer): RendererCreator {
    return when (renderer) {
        Renderer.SEQUENTIAL -> { r: ISingleRayRenderer, c: IColorCorrector -> SequentialRenderer(r, c) }
        Renderer.FORK_JOIN -> { r: ISingleRayRenderer, c: IColorCorrector -> ForkJoinRenderer(r, c) }
        Renderer.PARALLEL -> { r: ISingleRayRenderer, c: IColorCorrector -> ParallelRenderer(r, c) }
        Renderer.NAIVE_COROUTINE -> { r: ISingleRayRenderer, c: IColorCorrector -> NaiveCoroutineRenderer(r, c) }
        Renderer.COROUTINE -> { r: ISingleRayRenderer, c: IColorCorrector -> CoroutineBlockRenderer(r, c) }
    }
}
