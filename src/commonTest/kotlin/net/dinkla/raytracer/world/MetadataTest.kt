package net.dinkla.raytracer.world

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.tracers.Tracers

class MetadataTest :
    StringSpec({

        "metadata built without a tracer defaults preferredTracer to null (today's behaviour)" {
            val metadata = Metadata(id = "Scene.kt")

            metadata.preferredTracer shouldBe null
        }

        "metadata records the preferred tracer it is constructed with" {
            val metadata = Metadata(id = "Scene.kt", preferredTracer = Tracers.AREA)

            metadata.preferredTracer shouldBe Tracers.AREA
        }

        "metadata built without the flag is not intentionallyEmpty (today's behaviour)" {
            val metadata = Metadata(id = "Scene.kt")

            metadata.intentionallyEmpty shouldBe false
        }

        "metadata records that it was constructed as intentionallyEmpty" {
            val metadata = Metadata(id = "Template.kt", intentionallyEmpty = true)

            metadata.intentionallyEmpty shouldBe true
        }
    })
