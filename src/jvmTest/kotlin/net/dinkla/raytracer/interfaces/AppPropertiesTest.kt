package net.dinkla.raytracer.interfaces

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe

class AppPropertiesTest : AnnotationSpec() {

    @Test
    fun get() {
        val a = AppProperties["test.id"] as String?
        a shouldBe "4321"
    }

    @Test
    fun getAsInteger() {
        AppProperties.getAsInteger("test.id") shouldBe 4321
    }

    @Test
    fun getAsDouble() {
        AppProperties.getAsDouble("test.id") shouldBe 4321.0
    }
}
