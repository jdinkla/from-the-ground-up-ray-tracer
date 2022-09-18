package net.dinkla.raytracer.renderer

import net.dinkla.raytracer.cameras.IColorCorrector
import net.dinkla.raytracer.world.RendererCreator

actual enum class Renderer {
    SEQUENTIAL
}

actual fun createRenderer(renderer: Renderer) : RendererCreator {
    return when(renderer) {
        Renderer.SEQUENTIAL -> { r: ISingleRayRenderer, c: IColorCorrector -> SequentialRenderer(r, c) }
    }
}
