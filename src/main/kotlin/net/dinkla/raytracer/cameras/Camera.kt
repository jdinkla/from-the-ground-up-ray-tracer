package net.dinkla.raytracer.cameras

import net.dinkla.raytracer.cameras.lenses.AbstractLens
import net.dinkla.raytracer.cameras.render.IRenderer
import net.dinkla.raytracer.films.Film
import net.dinkla.raytracer.math.*
import net.dinkla.raytracer.utilities.Timer
import org.slf4j.LoggerFactory

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 12.04.2010
 * Time: 12:11:32
 * To change this template use File | Settings | File Templates.
 *
 * TODO: Kamera konfigurierbarer und modularer
 *
 * 1. Render: Single | Sample
 * 2. Sequential | Parallel
 * 3. Lens: Pinhole, FishEye, Spherical, ThinLens, Orthographic
 * 4. Iterative ?
 */
open class Camera(val lens: AbstractLens, val render2: IRenderer) {

    var eye: Point3D = DEFAULT_CAMERA
    var lookAt: Point3D = Point3D.ORIGIN
    var up: Vector3D = Vector3D.UP
    var uvw: Basis = Basis(eye, lookAt, up)

    init {
        lens.eye = eye
        lens.uvw = uvw
    }

    fun setup(eye: Point3D, lookAt: Point3D, up: Vector3D) {
        this.eye = eye
        this.lookAt = lookAt
        this.up = up
        computeUVW()
    }

    fun computeUVW() {
        uvw = Basis(eye, lookAt, up)
        lens.eye = eye
        lens.uvw = uvw
    }

    open fun render(film: Film, frame: Int) {
        LOGGER.info("rendering: eye=$eye, lookAt=$lookAt, up=$up")
        val t = Timer()
        t.start()
        render2.render(film)
        t.stop()
        LOGGER.info("rendering took " + t.duration + " ms")
    }

//    fun getLens(): ILens {
//        return lens
//    }
//
//    fun setLens(lens: ILens) {
//        this.lens = lens
//        this.lens.setEye(eye)
//        this.lens.setUvw(uvw)
//    }

    companion object {
        internal val LOGGER = LoggerFactory.getLogger(this::class.java)
        val DEFAULT_CAMERA = Point3D(0, 10, 0)
    }

}
