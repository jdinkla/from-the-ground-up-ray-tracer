package net.dinkla.raytracer.utilities

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class AppPropertiesTest : StringSpec({
    "get" {
        val a = AppProperties["test.id"] as String?
        a shouldBe "4321"
    }

    "getAsInteger" {
        AppProperties.getAsInteger("test.id") shouldBe 4321
    }

    "getAsDouble" {
        AppProperties.getAsDouble("test.id") shouldBe 4321.0
    }
})
