package net.dinkla.raytracer.audit

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

private fun sceneThat(
    theId: String,
    failsToBuild: Boolean = false,
): WorldDefinition =
    object : WorldDefinition {
        override val id: String = theId

        override fun world(): World =
            if (failsToBuild) error("cannot build $theId") else Builder.build { camera() }
    }

class SceneAuditorTest : StringSpec({
    val catalog = ClassCatalog(mapOf(Category.GEOMETRY to setOf("a.Sphere")))

    // Inspect/render are injected fakes so the orchestration is tested without rendering.
    fun auditorRecording(usage: Map<Category, Set<String>>) =
        SceneAuditor(
            buildWorld = { it.world() },
            inspect = { usage },
            renderStatus = { RenderStatus.Rendered(0.0) },
        )

    "audits every scene and keeps going after one fails to build" {
        val auditor = auditorRecording(mapOf(Category.GEOMETRY to setOf("a.Sphere")))

        val model = auditor.audit(listOf(sceneThat("ok.kt"), sceneThat("bad.kt", failsToBuild = true)), catalog, 0.999)

        model.sceneCount shouldBe 2
        model.failed shouldContainExactly listOf("bad.kt" to "IllegalStateException: cannot build bad.kt")
    }

    "a failed scene contributes no usage, a healthy one does" {
        val auditor = auditorRecording(mapOf(Category.GEOMETRY to setOf("a.Sphere")))

        val model = auditor.audit(listOf(sceneThat("ok.kt"), sceneThat("bad.kt", failsToBuild = true)), catalog, 0.999)

        val sphere = model.multiplicity.getValue(Category.GEOMETRY).single()
        sphere.simpleName shouldBe "Sphere"
        sphere.sceneIds shouldContainExactly listOf("ok.kt")
    }
})
