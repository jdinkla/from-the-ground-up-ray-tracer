package net.dinkla.raytracer.examples

import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

object Template : WorldDefinition {
    override val id: String = "Template.kt"

    override fun world(): World =
        Builder.build {
            // Black by design: this is an empty starting point for new scenes, not a defect. Opt out
            // of the audit's near-black SUSPECT list so it stays a high-signal list of real problems.
            metadata {
                id = "Template.kt"
                intentionallyEmpty = true
            }
        }
}
