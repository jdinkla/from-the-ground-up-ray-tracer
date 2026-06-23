package net.dinkla.raytracer.audit

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.string.shouldContain

class ReportFormatterTest : StringSpec({
    val catalog =
        ClassCatalog(
            mapOf(Category.GEOMETRY to setOf("a.Sphere", "a.Torus")),
        )
    val results =
        listOf(
            SceneAuditResult("lit.kt", mapOf(Category.GEOMETRY to setOf("a.Sphere")), RenderStatus.Rendered(0.1)),
            SceneAuditResult("black.kt", mapOf(Category.GEOMETRY to setOf("a.Sphere")), RenderStatus.Rendered(1.0)),
            SceneAuditResult("broken.kt", emptyMap(), RenderStatus.Failed("missing mesh")),
            SceneAuditResult("stereo.kt", emptyMap(), RenderStatus.Skipped("stereo camera")),
        )
    val markdown = AuditReport.build(catalog, results, 0.999).toMarkdown("160x90")

    "has the headline sections" {
        markdown shouldContain "# Scene audit"
        markdown shouldContain "## Coverage summary"
        markdown shouldContain "## Uncovered classes"
        markdown shouldContain "## Multiplicity"
        markdown shouldContain "## Suspect renders"
    }

    "names the uncovered class by its simple name" {
        markdown shouldContain "Torus"
    }

    "shows multiplicity with a scene count" {
        markdown shouldContain "Sphere — 2"
    }

    "reports the near-black render as a percentage" {
        markdown shouldContain "black.kt — 100.0% black"
    }

    "lists failures and skips with their reasons" {
        markdown shouldContain "broken.kt — missing mesh"
        markdown shouldContain "stereo.kt — stereo camera"
    }
})
