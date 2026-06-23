package net.dinkla.raytracer.audit

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

/**
 * Drives [AuditReport.build] with a hand-built catalog and fake per-scene results so the aggregation
 * (uncovered, multiplicity, suspects, failed, skipped, coverage counts) is asserted in isolation,
 * without rendering anything.
 */
class AuditReportTest : StringSpec({
    val catalog =
        ClassCatalog(
            mapOf(Category.GEOMETRY to setOf("a.Sphere", "a.Plane", "a.Torus")),
        )
    fun geo(vararg names: String) = mapOf(Category.GEOMETRY to names.toSet())
    val results =
        listOf(
            SceneAuditResult("s1.kt", geo("a.Sphere"), RenderStatus.Rendered(0.2)),
            SceneAuditResult("s2.kt", geo("a.Sphere", "a.Plane"), RenderStatus.Rendered(1.0)),
            SceneAuditResult("s3.kt", emptyMap(), RenderStatus.Failed("boom")),
            SceneAuditResult("s4.kt", geo("a.Plane"), RenderStatus.Skipped("stereo camera")),
        )
    val model = AuditReport.build(catalog, results, suspectThreshold = 0.999)

    "lists only catalogued classes that no scene used as uncovered" {
        model.uncovered.getValue(Category.GEOMETRY) shouldContainExactly listOf("a.Torus")
    }

    "ranks multiplicity by scene count then name, with sorted scene ids" {
        val geometry = model.multiplicity.getValue(Category.GEOMETRY)
        geometry.map { it.simpleName } shouldContainExactly listOf("Plane", "Sphere")
        geometry.first { it.simpleName == "Sphere" }.sceneIds shouldContainExactly listOf("s1.kt", "s2.kt")
        geometry.first { it.simpleName == "Plane" }.sceneIds shouldContainExactly listOf("s2.kt", "s4.kt")
    }

    "flags only renders at or above the suspect threshold" {
        model.suspects.map { it.sceneId } shouldContainExactly listOf("s2.kt")
        model.suspects.first().nearBlackFraction shouldBe 1.0
    }

    "separates build/render failures from black images" {
        model.failed shouldContainExactly listOf("s3.kt" to "boom")
    }

    "reports skipped scenes with their reason" {
        model.skipped shouldContainExactly listOf("s4.kt" to "stereo camera")
    }

    "derives covered-of-total coverage per category" {
        model.coverage(Category.GEOMETRY) shouldBe (2 to 3)
        model.sceneCount shouldBe 4
    }
})
