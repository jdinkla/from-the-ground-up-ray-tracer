package net.dinkla.raytracer.utilities

private const val RATIO_16_TO_9 = 16.0 / 9.0

data class Resolution(val width: Int, val height: Int) {

    constructor(height: Int) : this((height * RATIO_16_TO_9).toInt(), height)

    enum class Predefined(val id: String, val height: Int) {
        RESOLUTION_480("480p", 480),
        RESOLUTION_720("720p", 720),
        RESOLUTION_1080("1080p", 1080),
        RESOLUTION_1440("1440p", 1440),
        RESOLUTION_2160("2160p", 2160),
        RESOLUTION_4320("4320p", 4320);

        fun create(): Resolution = Resolution(height)
    }
    companion object {
        val resolutions = Predefined.values().toList()
    }
}
