package net.dinkla.raytracer.renderer

import net.dinkla.raytracer.world.RendererCreator

expect enum class Renderer
expect fun createRenderer(renderer: Renderer): RendererCreator
