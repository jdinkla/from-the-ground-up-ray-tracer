package net.dinkla.raytracer.world.dsl

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class MetadataScopeTest : StringSpec({

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
})
