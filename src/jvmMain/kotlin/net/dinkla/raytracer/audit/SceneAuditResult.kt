package net.dinkla.raytracer.audit

/** The outcome of the audit's low-resolution health render of one scene. */
sealed interface RenderStatus {
    /** The scene rendered; [nearBlackFraction] is the share of (near-)black pixels in [0, 1]. */
    data class Rendered(
        val nearBlackFraction: Double,
    ) : RenderStatus

    /** The scene was not health-rendered on purpose (e.g. a stereo scene); [reason] says why. */
    data class Skipped(
        val reason: String,
    ) : RenderStatus

    /** Building or rendering the scene threw; [message] is the failure. */
    data class Failed(
        val message: String,
    ) : RenderStatus
}

/**
 * Everything the audit learned about one scene: the production classes it [used] (empty when the
 * scene failed to build), the [render] health outcome, and whether the scene opted out of near-black
 * detection ([excludedFromNearBlack]) because it is an intentionally-empty template/scaffolding.
 */
data class SceneAuditResult(
    val sceneId: String,
    val used: Map<Category, Set<String>>,
    val render: RenderStatus,
    /**
     * `true` when the scene declared itself intentionally empty
     * ([net.dinkla.raytracer.world.Metadata.intentionallyEmpty]); such scenes are black by design and
     * are kept off the near-black SUSPECT list. Defaults to `false` so every ordinary scene is checked.
     */
    val excludedFromNearBlack: Boolean = false,
)
