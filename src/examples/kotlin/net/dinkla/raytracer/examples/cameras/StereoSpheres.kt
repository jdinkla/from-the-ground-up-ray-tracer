package net.dinkla.raytracer.examples.cameras

import net.dinkla.raytracer.cameras.StereoMode
import net.dinkla.raytracer.cameras.StereoViewing
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

/**
 * A stereo-camera scene (Suffern ch. 9): three spheres at different depths rendered as a
 * side-by-side parallel stereo pair. The output image is double width — the left half is the
 * left-eye view and the right half the right-eye view, with horizontal parallax between them.
 *
 * Switch [StereoViewing.SIDE_BY_SIDE] to [StereoViewing.ANAGLYPH] (and view with red/cyan glasses)
 * or [StereoMode.PARALLEL] to [StereoMode.TRANSVERSE] to try the other stereo variants.
 */
object StereoSpheres : WorldDefinition {
    override val id: String = "StereoSpheres.kt"

    override fun world(): World =
        Builder.build {
            stereoCamera(
                eye = p(0, 0, 60),
                lookAt = Point3D.ORIGIN,
                separation = 1.0,
                mode = StereoMode.PARALLEL,
                viewing = StereoViewing.SIDE_BY_SIDE,
                d = 1500.0,
            )

            ambientLight(ls = 0.4)

            lights {
                pointLight(location = p(30, 40, 50), ls = 3.0)
            }

            materials {
                phong(id = "red", ks = 0.5, cd = c(1.0, 0.2, 0.2), ka = 0.3, kd = 0.7, exp = 20.0)
                phong(id = "green", ks = 0.5, cd = c(0.2, 1.0, 0.2), ka = 0.3, kd = 0.7, exp = 20.0)
                phong(id = "blue", ks = 0.5, cd = c(0.2, 0.4, 1.0), ka = 0.3, kd = 0.7, exp = 20.0)
                matte(id = "floor", cd = c(0.6, 0.6, 0.6))
            }

            objects {
                sphere(material = "red", center = p(-8, 0, 10), radius = 4.0)
                sphere(material = "green", center = p(0, 0, 0), radius = 5.0)
                sphere(material = "blue", center = p(9, 0, -12), radius = 4.0)
                plane(material = "floor", point = p(0, -6, 0), normal = Normal.UP)
            }
        }
}
