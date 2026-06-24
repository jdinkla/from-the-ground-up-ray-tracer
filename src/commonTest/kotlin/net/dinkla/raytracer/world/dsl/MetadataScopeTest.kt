package net.dinkla.raytracer.world.dsl

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.tracers.Tracers

class MetadataScopeTest :
    StringSpec({

        "should create" {
            // Given
            val scope = MetadataScope()

            // When
            scope.id("someId")
            scope.title("someTitle")
            scope.description("someDescription")

            // Then
            scope.metadata.id shouldBe "someId"
            scope.metadata.title shouldBe "someTitle"
            scope.metadata.description shouldBe "someDescription"
        }

        "a scene that declares no tracer has a null preferred tracer (today's default)" {
            val scope = MetadataScope()

            scope.id("someId")

            scope.metadata.preferredTracer shouldBe null
        }

        "the preferredTracer setter records the declared tracer" {
            val scope = MetadataScope()

            scope.preferredTracer(Tracers.MULTIPLE_OBJECTS)

            scope.metadata.preferredTracer shouldBe Tracers.MULTIPLE_OBJECTS
        }

        "the preferredTracer property can be assigned directly" {
            val scope = MetadataScope()

            scope.preferredTracer = Tracers.PATH_TRACE

            scope.metadata.preferredTracer shouldBe Tracers.PATH_TRACE
        }

        "a scene that declares nothing is not intentionallyEmpty (today's default)" {
            val scope = MetadataScope()

            scope.id("someId")

            scope.metadata.intentionallyEmpty shouldBe false
        }

        "the intentionallyEmpty setter marks the scene" {
            val scope = MetadataScope()

            scope.intentionallyEmpty()

            scope.metadata.intentionallyEmpty shouldBe true
        }

        "the intentionallyEmpty property can be assigned directly" {
            val scope = MetadataScope()

            scope.intentionallyEmpty = true

            scope.metadata.intentionallyEmpty shouldBe true
        }
    })
