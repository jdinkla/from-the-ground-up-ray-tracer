package net.dinkla.raytracer.utilities

class Resolution {

    // Resolution
    val hres: Int
    val vres: Int

    constructor(vres: Int) {
        this.hres = vres / 9 * 16
        this.vres = vres
    }

    constructor(hres: Int, vres: Int) {
        this.hres = hres
        this.vres = vres
    }

    override fun toString(): String {
        return "($hres,$vres)"
    }

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
