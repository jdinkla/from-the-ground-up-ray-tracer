package net.dinkla.raytracer.utilities

import net.dinkla.raytracer.math.MathUtils.PI
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.objects.SmoothTriangle
import net.dinkla.raytracer.objects.Triangle
import kotlin.math.cos
import kotlin.math.sin

object GridUtilities {
    /**
     * A vertex on the unit sphere at horizontal index [j] (of [horizontalSteps]) and vertical
     * index [k] (of [verticalSteps]); the same parametrisation used by both tessellations.
     */
    private fun spherePoint(
        j: Int,
        k: Int,
        horizontalSteps: Int,
        verticalSteps: Int,
    ): Point3D =
        Point3D(
            sin(2.0 * PI * j / horizontalSteps) * sin(PI * k / verticalSteps),
            cos(PI * k / verticalSteps),
            cos(2.0 * PI * j / horizontalSteps) * sin(PI * k / verticalSteps),
        )

    fun tessellateFlatSphere(
        list: MutableList<Triangle>,
        horizontalSteps: Int,
        verticalSteps: Int,
    ) {
        tessellateFlatTopCap(list, horizontalSteps, verticalSteps)
        tessellateFlatBottomCap(list, horizontalSteps, verticalSteps)
        tessellateFlatMiddleRings(list, horizontalSteps, verticalSteps)
    }

    /** Top cap: triangles whose v0 is the north pole. */
    private fun tessellateFlatTopCap(
        list: MutableList<Triangle>,
        horizontalSteps: Int,
        verticalSteps: Int,
    ) {
        val k = 1
        for (j in 0 until horizontalSteps) {
            val v0 = Point3D(0.0, 1.0, 0.0) // top (north pole)
            val v1 = spherePoint(j, k, horizontalSteps, verticalSteps) // bottom left
            val v2 = spherePoint(j + 1, k, horizontalSteps, verticalSteps) // bottom right
            list.add(Triangle(v0, v1, v2))
        }
    }

    /** Bottom cap: triangles whose v1 is the south pole. */
    private fun tessellateFlatBottomCap(
        list: MutableList<Triangle>,
        horizontalSteps: Int,
        verticalSteps: Int,
    ) {
        val k = verticalSteps - 1
        for (j in 0 until horizontalSteps) {
            val v0 = spherePoint(j, k, horizontalSteps, verticalSteps) // top left
            val v1 = Point3D(0.0, -1.0, 0.0) // bottom (south pole)
            val v2 = spherePoint(j + 1, k, horizontalSteps, verticalSteps) // top right
            list.add(Triangle(v0, v1, v2))
        }
    }

    /** Middle rings: each cell becomes two triangles. */
    private fun tessellateFlatMiddleRings(
        list: MutableList<Triangle>,
        horizontalSteps: Int,
        verticalSteps: Int,
    ) {
        var k = 1
        while (k <= verticalSteps - 2) {
            for (j in 0 until horizontalSteps) {
                // first triangle: bottom left (k+1, j), bottom right (k+1, j+1), top left (k, j)
                list.add(
                    Triangle(
                        spherePoint(j, k + 1, horizontalSteps, verticalSteps),
                        spherePoint(j + 1, k + 1, horizontalSteps, verticalSteps),
                        spherePoint(j, k, horizontalSteps, verticalSteps),
                    ),
                )
                // second triangle: top right (k, j+1), top left (k, j), bottom right (k+1, j+1)
                list.add(
                    Triangle(
                        spherePoint(j + 1, k, horizontalSteps, verticalSteps),
                        spherePoint(j, k, horizontalSteps, verticalSteps),
                        spherePoint(j + 1, k + 1, horizontalSteps, verticalSteps),
                    ),
                )
            }
            k++
        }
    }

    fun tessellateSmoothSphere(
        list: MutableList<SmoothTriangle>,
        horizontalSteps: Int,
        verticalSteps: Int,
    ) {
        tessellateSmoothTopCap(list, horizontalSteps, verticalSteps)
        tessellateSmoothBottomCap(list, horizontalSteps, verticalSteps)
        tessellateSmoothMiddleRings(list, horizontalSteps, verticalSteps)
    }

    /**
     * A [SmoothTriangle] on the unit sphere whose per-vertex normals are the radial directions of
     * its vertices (on a unit sphere centred at the origin the outward normal equals the position).
     */
    private fun smoothTriangle(
        v0: Point3D,
        v1: Point3D,
        v2: Point3D,
    ): SmoothTriangle =
        SmoothTriangle(v0, v1, v2).apply {
            n0 = Normal.create(Vector3D(v0))
            n1 = Normal.create(Vector3D(v1))
            n2 = Normal.create(Vector3D(v2))
        }

    /** Top cap: triangles whose v0 is the north pole. */
    private fun tessellateSmoothTopCap(
        list: MutableList<SmoothTriangle>,
        horizontalSteps: Int,
        verticalSteps: Int,
    ) {
        val k = 1
        for (j in 0 until horizontalSteps) {
            val v0 = Point3D(0.0, 1.0, 0.0) // top (north pole)
            val v1 = spherePoint(j, k, horizontalSteps, verticalSteps) // bottom left
            val v2 = spherePoint(j + 1, k, horizontalSteps, verticalSteps) // bottom right
            list.add(smoothTriangle(v0, v1, v2))
        }
    }

    /** Bottom cap: triangles whose v1 is the south pole. */
    private fun tessellateSmoothBottomCap(
        list: MutableList<SmoothTriangle>,
        horizontalSteps: Int,
        verticalSteps: Int,
    ) {
        val k = verticalSteps - 1
        for (j in 0 until horizontalSteps) {
            val v0 = spherePoint(j, k, horizontalSteps, verticalSteps) // top left
            val v1 = Point3D(0.0, -1.0, 0.0) // bottom (south pole)
            val v2 = spherePoint(j + 1, k, horizontalSteps, verticalSteps) // top right
            list.add(smoothTriangle(v0, v1, v2))
        }
    }

    /** Middle rings: each cell becomes two triangles. */
    private fun tessellateSmoothMiddleRings(
        list: MutableList<SmoothTriangle>,
        horizontalSteps: Int,
        verticalSteps: Int,
    ) {
        var k = 1
        while (k <= verticalSteps - 2) {
            for (j in 0 until horizontalSteps) {
                // first triangle: bottom left (k+1, j), bottom right (k+1, j+1), top left (k, j)
                list.add(
                    smoothTriangle(
                        spherePoint(j, k + 1, horizontalSteps, verticalSteps),
                        spherePoint(j + 1, k + 1, horizontalSteps, verticalSteps),
                        spherePoint(j, k, horizontalSteps, verticalSteps),
                    ),
                )
                // second triangle: top right (k, j+1), top left (k, j), bottom right (k+1, j+1)
                list.add(
                    smoothTriangle(
                        spherePoint(j + 1, k, horizontalSteps, verticalSteps),
                        spherePoint(j, k, horizontalSteps, verticalSteps),
                        spherePoint(j + 1, k + 1, horizontalSteps, verticalSteps),
                    ),
                )
            }
            k++
        }
    }
}
