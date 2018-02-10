package net.dinkla.raytracer.gui.swing

import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D

class RayTracerParametersBean {
    var eyeX: Double? = null
    var eyeY: Double? = null
    var eyeZ: Double? = null
    var lookAtX: Double? = null
    var lookAtY: Double? = null
    var lookAtZ: Double? = null
    var upZ: Double? = null
    var upY: Double? = null
    var upX: Double? = null
    var d: Double? = null
    var numProcessors: Int? = null
    var hres: Int? = null
    var size: Double? = null
    var gamma: Double? = null
    var numSamples: Int? = null
    var maxDepth: Int? = null
    var isShowOutOfGamut: Boolean? = null
    var vres: Int? = null
    var fileName: String? = null
    var worldProgram: String? = null
    var exposureTime: Double? = null

    var eye: Point3D
        get() = Point3D(eyeX!!, eyeY!!, eyeZ!!)
        set(eye) {
            eyeX = eye.x
            eyeY = eye.y
            eyeZ = eye.z
        }

    var lookAt: Point3D
        get() = Point3D(lookAtX!!, lookAtY!!, lookAtZ!!)
        set(lookAt) {
            lookAtX = lookAt.x
            lookAtY = lookAt.y
            lookAtZ = lookAt.z
        }

    val up: Vector3D
        get() = Vector3D(upX!!, upY!!, upZ!!)

    init {
        eyeX = 0.0
        eyeY = 0.0
        eyeZ = 0.0
        lookAtX = 0.0
        lookAtY = 0.0
        lookAtZ = 0.0
        upZ = 0.0
        upY = 0.0
        upX = 0.0
        d = 0.0
        numProcessors = Runtime.getRuntime().availableProcessors()
        hres = 480 * 9 / 16
        vres = 480
        size = 1.0
        gamma = 1.0
        numSamples = 0
        maxDepth = 5
        isShowOutOfGamut = false
        fileName = ""
        worldProgram = ""
        exposureTime = 1.0
    }

    fun setEye(x: Double?, y: Double?, z: Double?) {
        eyeX = x
        eyeY = y
        eyeZ = z
    }

    fun setLookAt(x: Double?, y: Double?, z: Double?) {
        lookAtX = x
        lookAtY = y
        lookAtZ = z
    }

    fun setUp(up: Point3D) {
        upX = up.x
        upY = up.y
        upZ = up.z
    }

    fun setUp(x: Double?, y: Double?, z: Double?) {
        upX = x
        upY = y
        upZ = z
    }
}

