package net.dinkla.raytracer.examples

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

object Template : WorldDefinition {
    override val id: String = "Template.kt"
    override fun world(): World = Builder.build {
    }
}
