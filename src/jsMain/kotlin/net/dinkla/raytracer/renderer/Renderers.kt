package net.dinkla.raytracer.renderer

import net.dinkla.raytracer.world.RendererFactory

enum class Renderers(val create: RendererFactory) {
    SEQUENTIAL( { r, c -> SequentialRenderer(r, c) })
}
