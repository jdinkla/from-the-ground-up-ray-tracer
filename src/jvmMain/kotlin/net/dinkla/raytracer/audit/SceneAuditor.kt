package net.dinkla.raytracer.audit

import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

/**
 * Drives the scene audit: for each [WorldDefinition] it builds the world, inspects which classes it
 * uses, health-renders it, and folds the per-scene results into a [ReportModel]. Building or rendering
 * a scene can fail many ways (a missing `.ply` mesh, a bad scene), so each scene is isolated in
 * try/catch and a failure becomes a [RenderStatus.Failed] entry rather than aborting the whole audit.
 *
 * The three collaborators are injected so the orchestration is unit-testable with fakes; the real
 * runner ([main]) supplies [World.world]-building, [SceneInspector], and the low-resolution render.
 */
class SceneAuditor(
    private val buildWorld: (WorldDefinition) -> World,
    private val inspect: (World) -> Map<Category, Set<String>>,
    private val renderStatus: (World) -> RenderStatus,
) {
    fun audit(
        definitions: List<WorldDefinition>,
        catalog: ClassCatalog,
        suspectThreshold: Double,
    ): ReportModel = AuditReport.build(catalog, definitions.map(::auditOne), suspectThreshold)

    // Throwable, not Exception: the largest example scenes (tens of thousands of objects) can exhaust
    // the heap while building, raising OutOfMemoryError. Isolating it per scene turns "this one scene is
    // too big for the audit" into a FAILED row instead of aborting the whole run; the failed allocation
    // is unwound, so the next scene starts with the heap reclaimed.
    @Suppress("TooGenericExceptionCaught")
    private fun auditOne(definition: WorldDefinition): SceneAuditResult =
        try {
            val world = buildWorld(definition)
            // Inspect before rendering: inspection reads the authored, pre-initialize() tree, while the
            // render mutates the world (initialize() builds acceleration cells).
            val used = inspect(world)
            SceneAuditResult(definition.id, used, renderStatus(world))
        } catch (e: Throwable) {
            SceneAuditResult(definition.id, emptyMap(), RenderStatus.Failed(describe(e)))
        }

    private fun describe(e: Throwable): String =
        e.message?.let { "${e::class.java.simpleName}: $it" } ?: e::class.java.simpleName
}
