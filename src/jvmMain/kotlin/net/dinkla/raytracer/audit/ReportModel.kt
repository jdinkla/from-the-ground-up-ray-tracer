package net.dinkla.raytracer.audit

/** One class and the ids of the scenes that use it, most-used first. */
data class Multiplicity(
    val fqn: String,
    val sceneIds: List<String>,
) {
    val count: Int get() = sceneIds.size
    val simpleName: String get() = simpleNameOf(fqn)
}

/** A scene flagged as a likely-broken (near-black) render. */
data class Suspect(
    val sceneId: String,
    val nearBlackFraction: Double,
)

/**
 * The computed result of a scene audit, ready to format. Built by [AuditReport.build] from the raw
 * per-scene results plus the [ClassCatalog] denominator.
 */
data class ReportModel(
    val sceneCount: Int,
    val suspectThreshold: Double,
    val uncovered: Map<Category, List<String>>,
    val multiplicity: Map<Category, List<Multiplicity>>,
    val suspects: List<Suspect>,
    val failed: List<Pair<String, String>>,
    val skipped: List<Pair<String, String>>,
) {
    /** Covered/total per category, derived from [multiplicity] and [uncovered]. */
    fun coverage(category: Category): Pair<Int, Int> {
        val covered = multiplicity[category]?.size ?: 0
        val total = covered + (uncovered[category]?.size ?: 0)
        return covered to total
    }
}

object AuditReport {
    fun build(
        catalog: ClassCatalog,
        results: List<SceneAuditResult>,
        suspectThreshold: Double,
    ): ReportModel {
        val usedBy = usageIndex(results)
        return ReportModel(
            sceneCount = results.size,
            suspectThreshold = suspectThreshold,
            uncovered = uncovered(catalog, usedBy),
            multiplicity = multiplicity(usedBy),
            suspects = suspects(results, suspectThreshold),
            failed = results.mapNotNull { r -> (r.render as? RenderStatus.Failed)?.let { r.sceneId to it.message } },
            skipped = results.mapNotNull { r -> (r.render as? RenderStatus.Skipped)?.let { r.sceneId to it.reason } },
        )
    }

    /** category -> (class fqn -> scene ids that use it). */
    private fun usageIndex(results: List<SceneAuditResult>): Map<Category, Map<String, List<String>>> {
        val index = Category.entries.associateWith { mutableMapOf<String, MutableList<String>>() }
        results.forEach { result ->
            result.used.forEach { (category, classes) ->
                classes.forEach { fqn ->
                    index.getValue(category).getOrPut(fqn) { mutableListOf() }.add(result.sceneId)
                }
            }
        }
        return index
    }

    private fun uncovered(
        catalog: ClassCatalog,
        usedBy: Map<Category, Map<String, List<String>>>,
    ): Map<Category, List<String>> =
        Category.entries.associateWith { category ->
            (catalog.denominator(category) - usedBy.getValue(category).keys)
                .sortedBy { simpleNameOf(it) }
        }

    private fun multiplicity(usedBy: Map<Category, Map<String, List<String>>>): Map<Category, List<Multiplicity>> =
        Category.entries.associateWith { category ->
            usedBy
                .getValue(category)
                .map { (fqn, ids) -> Multiplicity(fqn, ids.sorted()) }
                .sortedWith(compareByDescending<Multiplicity> { it.count }.thenBy { it.simpleName })
        }

    private fun suspects(
        results: List<SceneAuditResult>,
        threshold: Double,
    ): List<Suspect> =
        results
            .mapNotNull { r -> (r.render as? RenderStatus.Rendered)?.let { r.sceneId to it.nearBlackFraction } }
            .filter { it.second >= threshold }
            .map { Suspect(it.first, it.second) }
            .sortedWith(compareByDescending<Suspect> { it.nearBlackFraction }.thenBy { it.sceneId })
}
