package net.dinkla.raytracer.world.dsl

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.samplers.Constant
import net.dinkla.raytracer.samplers.Sampler

class WorldScopeTest : StringSpec({

    "ambientLight" {
        val scope = WorldScope("id")
        val old = scope.world.ambientLight
        scope.ambientLight(Color.RED, 0.5)
        scope.world.ambientLight shouldNotBe old
    }

    "ambientOccluder" {
        val scope = WorldScope("id")
        val old = scope.world.ambientLight
        scope.ambientOccluder(Color.RED,Sampler(Constant()), 10)
        scope.world.ambientLight shouldNotBe old
    }

    "title" {
        val title = "someTitle"
        val scope = WorldScope("id")
        scope.title(title)
        scope.world.title shouldBe title
    }

    "description" {
        val description = "someDescription"
        val scope = WorldScope("id")
        scope.description(description)
        scope.world.description shouldBe description
    }
})
