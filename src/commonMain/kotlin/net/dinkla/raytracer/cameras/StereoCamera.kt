package net.dinkla.raytracer.cameras

import net.dinkla.raytracer.ViewPlane
import net.dinkla.raytracer.cameras.lenses.Pinhole
import net.dinkla.raytracer.math.Basis
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D

/**
 * How the two eye views converge (Suffern ch. 9, *stereo viewing*).
 *
 * - [PARALLEL]: both eyes look straight ahead — the left and right view directions stay parallel to
 *   the base camera's direction. Comfortable for off-screen viewing of side-by-side pairs.
 * - [TRANSVERSE]: the eyes are "toed in" so both view rays converge on the shared [StereoCamera.lookAt]
 *   point (the zero-parallax plane).
 */
enum class StereoMode {
    PARALLEL,
    TRANSVERSE,
}

/**
 * How the left/right eye images are composited into a single output image.
 *
 * - [SIDE_BY_SIDE]: the two images are placed next to each other, doubling the output width.
 * - [ANAGLYPH]: the images are merged into one (output keeps the single-eye width) — the left eye
 *   feeds the red channel, the right eye the green/blue (cyan) channels, for red/cyan glasses.
 */
enum class StereoViewing {
    SIDE_BY_SIDE,
    ANAGLYPH,
}

/**
 * Suffern's stereo camera: rather than a single [ILens][net.dinkla.raytracer.cameras.lenses.ILens],
 * a stereo camera renders the scene from two eye positions offset along the camera's right axis and
 * composites the two views (see [StereoViewing]).
 *
 * This type is a pure geometry/configuration holder. It derives a [leftCamera] and [rightCamera] —
 * each an ordinary [Pinhole] [Camera] — whose eyes are offset by `±separation/2` along the base
 * camera basis' right axis `u`. [StereoMode] decides whether the two cameras look parallel
 * ([StereoMode.PARALLEL]) or converge on [lookAt] ([StereoMode.TRANSVERSE]).
 *
 * The actual two-pass render and compositing live outside this class (in the render glue), so the
 * normal single-camera pipeline is untouched.
 */
class StereoCamera(
    val eye: Point3D,
    val lookAt: Point3D,
    val up: Vector3D = Vector3D.UP,
    val separation: Double,
    val mode: StereoMode = StereoMode.PARALLEL,
    val viewing: StereoViewing = StereoViewing.SIDE_BY_SIDE,
    val d: Double = 1.0,
) {
    /** The base (centre) camera basis, used to find the right axis along which the eyes are offset. */
    val baseBasis: Basis = Basis.create(eye, lookAt, up)

    /** Half the inter-eye separation: the magnitude of each eye's shift along the right axis. */
    private val halfSeparation: Double = separation / 2.0

    /** The right-axis shift applied to the right eye (and negated for the left eye). */
    private val shift: Vector3D = baseBasis.u * halfSeparation

    /** The left eye position: the base eye shifted left along the camera's right axis. */
    val leftEye: Point3D = eye - shift

    /** The right eye position: the base eye shifted right along the camera's right axis. */
    val rightEye: Point3D = eye + shift

    /**
     * The point the left eye looks at. In [StereoMode.PARALLEL] it is the base look-at shifted by the
     * same amount as the eye (so the view direction stays parallel to the base direction); in
     * [StereoMode.TRANSVERSE] both eyes share the base [lookAt] (toed-in convergence).
     */
    val leftLookAt: Point3D =
        when (mode) {
            StereoMode.PARALLEL -> lookAt - shift
            StereoMode.TRANSVERSE -> lookAt
        }

    /** The point the right eye looks at; mirror of [leftLookAt]. */
    val rightLookAt: Point3D =
        when (mode) {
            StereoMode.PARALLEL -> lookAt + shift
            StereoMode.TRANSVERSE -> lookAt
        }

    /** A [Pinhole] [Camera] for the left eye, sharing the view-plane distance [d]. */
    fun leftCamera(viewPlane: ViewPlane): Camera = pinholeCamera(leftEye, leftLookAt, viewPlane)

    /** A [Pinhole] [Camera] for the right eye, sharing the view-plane distance [d]. */
    fun rightCamera(viewPlane: ViewPlane): Camera = pinholeCamera(rightEye, rightLookAt, viewPlane)

    private fun pinholeCamera(
        cameraEye: Point3D,
        cameraLookAt: Point3D,
        viewPlane: ViewPlane,
    ): Camera =
        Camera({ e, uvw ->
            Pinhole(viewPlane, e, uvw).apply { d = this@StereoCamera.d }
        }, cameraEye, cameraLookAt, up)
}
