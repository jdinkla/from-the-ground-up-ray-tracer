package net.dinkla.raytracer.utilities

const val RATIO_16_TO_9 = 16.0 / 9.0

data class Resolution(val width: Int, val height: Int) {

    constructor(vres: Int) : this((vres * RATIO_16_TO_9).toInt(), vres)

    companion object {
        val RESOLUTION_480 = Resolution(480)
        val RESOLUTION_720 = Resolution(720)
        val RESOLUTION_1080 = Resolution(1080)
        val RESOLUTION_1440 = Resolution(1440)
        val RESOLUTION_2160 = Resolution(2160)
        val RESOLUTION_4320 = Resolution(4320)
    }
}
