package net.dinkla.raytracer.world.scripting

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

private class FakeWorldDefinition(
    override val id: String,
) : WorldDefinition {
    override fun world(): World = error("not needed for resolution test")
}

class SceneResolverTest :
    StringSpec({
        "an existing file path resolves to a FileWorldDefinition that loads via the script host" {
            val known = FakeWorldDefinition("Known.kt")

            val resolved = SceneResolver.resolveWorld("scenes/Sample.scene.kts", mapOf(known.id to known))

            resolved.shouldBeInstanceOf<FileWorldDefinition>()
            resolved.id shouldBe "Sample.scene.kts"
        }

        "a known built-in id resolves to its existing classgraph WorldDefinition unchanged" {
            val known = FakeWorldDefinition("Known.kt")

            val resolved = SceneResolver.resolveWorld("Known.kt", mapOf(known.id to known))

            (resolved === known) shouldBe true
        }

        "an unknown non-file id still fails fast with a clear, actionable message" {
            val known = FakeWorldDefinition("Known.kt")

            val ex =
                shouldThrow<IllegalArgumentException> {
                    SceneResolver.resolveWorld("DefinitelyNotAFileOrId", mapOf(known.id to known))
                }

            ex.message shouldContain "DefinitelyNotAFileOrId"
            ex.message shouldContain "--help"
        }

        "isExistingFile is true for an existing file and false for a non-existent path" {
            SceneResolver.isExistingFile("scenes/Sample.scene.kts") shouldBe true
            SceneResolver.isExistingFile("no/such/file/here.scene.kts") shouldBe false
        }
    })
