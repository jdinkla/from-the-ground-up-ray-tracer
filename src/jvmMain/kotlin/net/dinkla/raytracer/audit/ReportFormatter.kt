package net.dinkla.raytracer.audit

private const val MAX_SCENE_IDS_SHOWN = 6
private const val PERCENT = 100.0

/** Renders a [ReportModel] as a Markdown document — the human-facing audit report. */
fun ReportModel.toMarkdown(renderResolution: String): String =
    buildString {
        appendLine("# Scene audit")
        appendLine()
        appendLine(
            "$sceneCount scenes • health-rendered at $renderResolution • " +
                "near-black threshold ${percent(suspectThreshold)}",
        )
        appendLine()
        appendCoverageSummary(this@toMarkdown)
        appendUncovered(this@toMarkdown)
        appendMultiplicity(this@toMarkdown)
        appendSuspects(this@toMarkdown)
        appendList(this@toMarkdown.failed, "Failed to build or render", "every scene built and rendered")
        appendList(this@toMarkdown.skipped, "Skipped (not health-rendered)", "no scenes skipped")
    }

private fun StringBuilder.appendCoverageSummary(model: ReportModel) {
    appendLine("## Coverage summary")
    appendLine()
    appendLine("| Category | Covered | Total |")
    appendLine("| --- | ---: | ---: |")
    Category.entries.forEach { category ->
        val (covered, total) = model.coverage(category)
        appendLine("| ${category.display} | $covered | $total |")
    }
    appendLine()
}

private fun StringBuilder.appendUncovered(model: ReportModel) {
    appendLine("## Uncovered classes (no example)")
    appendLine()
    val anyUncovered = model.uncovered.values.any { it.isNotEmpty() }
    if (!anyUncovered) {
        appendLine("Every tracked class appears in at least one scene. 🎉")
        appendLine()
        return
    }
    Category.entries.forEach { category ->
        val classes = model.uncovered[category].orEmpty()
        if (classes.isNotEmpty()) {
            appendLine("### ${category.display} (${classes.size})")
            classes.forEach { appendLine("- ${simpleNameOf(it)}") }
            appendLine()
        }
    }
}

private fun StringBuilder.appendMultiplicity(model: ReportModel) {
    appendLine("## Multiplicity (scenes per class)")
    appendLine()
    Category.entries.forEach { category ->
        val entries = model.multiplicity[category].orEmpty()
        if (entries.isNotEmpty()) {
            appendLine("### ${category.display}")
            entries.forEach { appendLine("- ${it.simpleName} — ${it.count} (${sceneList(it.sceneIds)})") }
            appendLine()
        }
    }
}

private fun StringBuilder.appendSuspects(model: ReportModel) {
    appendLine("## Suspect renders (near-black)")
    appendLine()
    appendLine(
        "_Health-rendered with the scene's declared preferred tracer when it has one (scene " +
            "metadata), else an auto-selected tracer (Whitted, or area lighting for " +
            "area-light/emissive/ambient-occlusion scenes). A scene that declares no tracer but " +
            "needs a non-default one can still appear here even though it is not broken._",
    )
    appendLine()
    if (model.suspects.isEmpty()) {
        appendLine("No scene rendered (near-)black above the threshold.")
        appendLine()
        return
    }
    model.suspects.forEach { appendLine("- ${it.sceneId} — ${percent(it.nearBlackFraction)} black") }
    appendLine()
}

private fun StringBuilder.appendList(
    items: List<Pair<String, String>>,
    title: String,
    emptyNote: String,
) {
    appendLine("## $title")
    appendLine()
    if (items.isEmpty()) {
        appendLine("None — $emptyNote.")
        appendLine()
        return
    }
    items.sortedBy { it.first }.forEach { appendLine("- ${it.first} — ${it.second}") }
    appendLine()
}

private fun sceneList(ids: List<String>): String {
    if (ids.size <= MAX_SCENE_IDS_SHOWN) return ids.joinToString(", ")
    val shown = ids.take(MAX_SCENE_IDS_SHOWN).joinToString(", ")
    return "$shown, +${ids.size - MAX_SCENE_IDS_SHOWN} more"
}

private fun percent(fraction: Double): String = "${"%.1f".format(fraction * PERCENT)}%"
