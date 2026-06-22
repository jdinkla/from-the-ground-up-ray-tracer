package net.dinkla.raytracer.utilities

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.objects.SmoothTriangle
import net.dinkla.raytracer.objects.Triangle
import net.dinkla.raytracer.shouldBeApprox

// The tessellation builds a unit sphere as three bands of triangles:
//   - a top cap of `horizontalSteps` triangles meeting the north pole (0, 1, 0)
//   - a bottom cap of `horizontalSteps` triangles meeting the south pole (0, -1, 0)
//   - `verticalSteps - 2` middle rings, each split into 2 * horizontalSteps triangles
// so the total triangle count is 2 * horizontalSteps * (verticalSteps - 1).
private fun expectedTriangleCount(
    horizontalSteps: Int,
    verticalSteps: Int,
): Int = 2 * horizontalSteps * (verticalSteps - 1)

// Sample of (horizontalSteps, verticalSteps) pairs. Each varies the step counts so the
// top cap, bottom cap, and the middle-ring loop are all exercised (verticalSteps >= 3 is
// needed to enter the middle loop at least once).
private val stepCases =
    listOf(
        3 to 3,
        4 to 4,
        5 to 3,
        6 to 5,
    )

class GridUtilitiesTest : StringSpec({

    "flat sphere tessellation produces 2*h*(v-1) triangles across varying step counts" {
        for ((h, v) in stepCases) {
            val triangles = mutableListOf<Triangle>()

            GridUtilities.tessellateFlatSphere(triangles, h, v)

            triangles shouldHaveSize expectedTriangleCount(h, v)
        }
    }

    "smooth sphere tessellation produces 2*h*(v-1) triangles across varying step counts" {
        for ((h, v) in stepCases) {
            val triangles = mutableListOf<SmoothTriangle>()

            GridUtilities.tessellateSmoothSphere(triangles, h, v)

            triangles shouldHaveSize expectedTriangleCount(h, v)
        }
    }

    "flat sphere top-cap triangles all touch the north pole and bottom-cap the south pole" {
        val h = 6
        val v = 5
        val triangles = mutableListOf<Triangle>()

        GridUtilities.tessellateFlatSphere(triangles, h, v)

        // The first h triangles are the top cap: v0 is the north pole.
        for (j in 0 until h) {
            triangles[j].v0 shouldBeApprox Point3D(0.0, 1.0, 0.0)
        }
        // The next h triangles are the bottom cap: v1 is the south pole.
        for (j in 0 until h) {
            triangles[h + j].v1 shouldBeApprox Point3D(0.0, -1.0, 0.0)
        }
    }

    "smooth sphere top-cap triangles all touch the north pole and bottom-cap the south pole" {
        val h = 6
        val v = 5
        val triangles = mutableListOf<SmoothTriangle>()

        GridUtilities.tessellateSmoothSphere(triangles, h, v)

        for (j in 0 until h) {
            triangles[j].v0 shouldBeApprox Point3D(0.0, 1.0, 0.0)
        }
        for (j in 0 until h) {
            triangles[h + j].v1 shouldBeApprox Point3D(0.0, -1.0, 0.0)
        }
    }

    "every tessellated vertex lies on the unit sphere" {
        val h = 6
        val v = 5
        val triangles = mutableListOf<Triangle>()

        GridUtilities.tessellateFlatSphere(triangles, h, v)

        for (t in triangles) {
            for (vertex in listOf(t.v0, t.v1, t.v2)) {
                Vector3D(vertex).length shouldBeApprox 1.0
            }
        }
    }

    "smooth tessellation assigns each vertex a radial per-vertex normal equal to its position" {
        val h = 6
        val v = 5
        val triangles = mutableListOf<SmoothTriangle>()

        GridUtilities.tessellateSmoothSphere(triangles, h, v)

        // On a unit sphere centred at the origin the outward normal at a point equals the
        // (normalised) position vector, so each per-vertex normal must match its vertex.
        for (t in triangles) {
            t.n0 shouldBeApprox Normal.create(Vector3D(t.v0))
            t.n1 shouldBeApprox Normal.create(Vector3D(t.v1))
            t.n2 shouldBeApprox Normal.create(Vector3D(t.v2))
        }
    }

    "smooth tessellation gives the north-pole vertex an up-pointing normal" {
        val triangles = mutableListOf<SmoothTriangle>()

        GridUtilities.tessellateSmoothSphere(triangles, 4, 4)

        // v0 of every top-cap triangle is the north pole, whose outward normal is (0, 1, 0).
        triangles[0].n0 shouldBeApprox Normal.UP
    }

    "flat tessellation gives each triangle a single unit face normal rather than per-vertex normals" {
        val triangles = mutableListOf<Triangle>()

        GridUtilities.tessellateFlatSphere(triangles, 6, 5)

        // A flat Triangle exposes one face normal (no n0/n1/n2 seams), and it is a unit vector.
        for (t in triangles) {
            t.normal.length() shouldBeApprox 1.0
        }
    }
})
