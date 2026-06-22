package net.dinkla.raytracer.world

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

// A minimal WorldDefinition whose world() is never invoked by the id-validation path under test.
private class FakeWorldDefinition(
    override val id: String,
) : WorldDefinition {
    override fun world(): World = error("not needed for id validation")
}

class RenderTest :
    StringSpec({
        "requireWorldDef returns the matching scene for a known id" {
            val known = FakeWorldDefinition("KnownScene.kt")
            val available = mapOf(known.id to known)

            requireWorldDef("KnownScene.kt", available) shouldBe known
        }

        "requireWorldDef fails fast on an unknown id with a clear, actionable message" {
            val available = mapOf<String, WorldDefinition>("KnownScene.kt" to FakeWorldDefinition("KnownScene.kt"))

            val ex = shouldThrow<IllegalArgumentException> { requireWorldDef("Missing.kt", available) }

            ex.message shouldContain "Missing.kt"
            ex.message shouldContain "--help"
        }
    })
