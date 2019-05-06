package net.dinkla.raytracer.utilities

data class Resolution(val hres: Int, val vres: Int) {

    constructor(vres: Int) : this((vres * IMAGE_RATIO).toInt(), vres)

    companion object {
        const val IMAGE_RATIO = 16.0 / 9.0

        val RESOLUTION_32 = Resolution(32)
        val RESOLUTION_320 = Resolution(320)
        val RESOLUTION_480 = Resolution(480)
        val RESOLUTION_720 = Resolution(720)
        val RESOLUTION_1080 = Resolution(1080)
        val RESOLUTION_1440 = Resolution(1440)
        val RESOLUTION_2160 = Resolution(2160)
        val RESOLUTION_4320 = Resolution(4320)
    }

}
