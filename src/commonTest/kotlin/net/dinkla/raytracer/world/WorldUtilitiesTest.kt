package net.dinkla.raytracer.world

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class WorldUtilitiesTest :
    StringSpec({
        "repeat3 should iterative in three dimensions" {
            val list = mutableListOf<String>()
            repeat3(2) { x, y, z ->
                list.add("$x$y$z")
            }
            list shouldBe listOf("000", "001", "010", "011", "100", "101", "110", "111")
        }
    })
