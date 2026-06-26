package net.dinkla.raytracer.ui

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

private val worldIds = setOf("YellowAndRedSphere.kt", "Bunny.kt")

class CommandLineTest :
    StringSpec({
        "a known built-in scene id is an acceptable --world value" {
            isAcceptableWorldArg("Bunny.kt", worldIds, fileExists = { false }) shouldBe true
        }

        "an existing file path is an acceptable --world value even when it is not a known id" {
            val path = "scenes/Sample.scene.kts"

            isAcceptableWorldArg(path, worldIds, fileExists = { it == path }) shouldBe true
        }

        "a value that is neither a known id nor an existing file is rejected (TASK-15 fail-fast preserved)" {
            isAcceptableWorldArg("Nope.kt", worldIds, fileExists = { false }) shouldBe false
        }
    })
