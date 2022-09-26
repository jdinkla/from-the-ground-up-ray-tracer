package net.dinkla.raytracer.utilities

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class AppPropertiesTest : StringSpec({
    val key = "display.width"
    "get" {
        AppProperties[key] as String? shouldBe "1920"
    }

    "should handle umlauts" {
        AppProperties["app.title"] as String? shouldBe "\"From the ground up\" ray tracer by Jörn Dinkla"
    }

    "getAsInteger" {
        AppProperties.getAsInteger(key) shouldBe 1920
    }

    "getAsDouble" {
        AppProperties.getAsDouble(key) shouldBe 1920.0
    }
})
