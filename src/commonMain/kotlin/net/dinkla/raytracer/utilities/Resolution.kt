package net.dinkla.raytracer.utilities

private const val RATIO_16_TO_9 = 16.0 / 9.0

data class Resolution(
    val width: Int,
    val height: Int,
) {
    constructor(height: Int) : this((height * RATIO_16_TO_9).toInt(), height)

    enum class Predefined(
        val id: String,
        val height: Int,
    ) {
        RESOLUTION_480("480p", 480),
        RESOLUTION_720("720p", 720),
        RESOLUTION_1080("1080p", 1080),
        RESOLUTION_1440("1440p", 1440),
        RESOLUTION_2160("2160p", 2160),
        RESOLUTION_4320("4320p", 4320),
        ;

        fun create(): Resolution = Resolution(height)
    }

    companion object {
        val resolutions = Predefined.entries

        /**
         * Resolves a resolution id (e.g. `"1080p"`) to its [Resolution], failing fast with a clear,
         * actionable message that names the bad value and lists the valid ids. Pure validation logic,
         * unit-tested directly; the CLI's Clikt `choice` enforces the same rule at parse time, but
         * non-CLI callers go through here.
         */
        fun fromId(id: String): Resolution {
            val predefined =
                resolutions.firstOrNull { it.id == id }
                    ?: throw IllegalArgumentException(
                        "Unknown resolution '$id'. Valid options: ${resolutions.joinToString(", ") { it.id }}.",
                    )
            return predefined.create()
        }
    }
}
