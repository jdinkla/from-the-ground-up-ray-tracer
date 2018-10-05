package net.dinkla.raytracer.cameras

import ch.qos.logback.classic.Logger
import net.dinkla.raytracer.cameras.lenses.AbstractLens
import net.dinkla.raytracer.cameras.render.IRenderer
import net.dinkla.raytracer.films.IFilm
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
open class Camera(var lens: AbstractLens, var render2: IRenderer) {

    var eye: Point3D
    var lookAt: Point3D
    var up: Vector3D
    var uvw: Basis

    init {
        // setup(Point3D.DEFAULT_CAMERA, Point3D.ORIGIN, Vector3D.UP)
        val eye = DEFAULT_CAMERA
        val lookAt = Point3D.ORIGIN
        val up = Vector3D.UP
        uvw = Basis(eye, lookAt, up)

        this.eye = eye
        this.lookAt = lookAt
        this.up = up
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

    open fun render(film: IFilm, frame: Int) {
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
        internal val LOGGER = LoggerFactory.getLogger(this.javaClass)
        val DEFAULT_CAMERA = Point3D(0, 10, 0)
    }

}
