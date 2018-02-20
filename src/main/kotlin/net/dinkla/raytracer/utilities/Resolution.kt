package net.dinkla.raytracer.utilities

data class Resolution(val hres: Int, val vres: Int) {

    constructor(vres: Int) : this(vres / 9 * 16, vres) {}

    companion object {
        var RESOLUTION_32 = Resolution(32)
        var RESOLUTION_320 = Resolution(320)
        var RESOLUTION_480 = Resolution(480)
        var RESOLUTION_720 = Resolution(720)
        var RESOLUTION_1080 = Resolution(1080)
        var RESOLUTION_1440 = Resolution(1440)
        var RESOLUTION_2160 = Resolution(2160)
    }
}
