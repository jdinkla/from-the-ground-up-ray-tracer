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
    fun tessellateFlatSphere(
        list: MutableList<Triangle>,
        horizontalSteps: Int,
        verticalSteps: Int,
    ) {
        // define the top triangles which all touch the north pole
        var k = 1

        for (j in 0 until horizontalSteps) {
            // define vertices
            // top (north pole)
            val v0 = Point3D(0.0, 1.0, 0.0)
            // bottom left
            val v1 =
                Point3D(
                    sin(2.0 * PI * j / horizontalSteps) * sin(PI * k / verticalSteps),
                    cos(PI * k / verticalSteps),
                    cos(2.0 * PI * j / horizontalSteps) * sin(PI * k / verticalSteps),
                )
            // bottom  right
            val v2 =
                Point3D(
                    sin(2.0 * PI * (j + 1) / horizontalSteps) * sin(PI * k / verticalSteps),
                    cos(PI * k / verticalSteps),
                    cos(2.0 * PI * (j + 1) / horizontalSteps) * sin(PI * k / verticalSteps),
                )

            val triangle_ptr = Triangle(v0, v1, v2)
            list.add(triangle_ptr)
        }

        // define the bottom triangles which all touch the south pole
        k = verticalSteps - 1
        for (j in 0 until horizontalSteps) {
            // define vertices

            val v0 =
                Point3D(
                    sin(2.0 * PI * j / horizontalSteps) * sin(PI * k / verticalSteps), // top left
                    cos(PI * k / verticalSteps),
                    cos(2.0 * PI * j / horizontalSteps) * sin(PI * k / verticalSteps),
                )

            val v1 = Point3D(0.0, -1.0, 0.0) // bottom (south pole)

            val v2 =
                Point3D(
                    sin(2.0 * PI * (j + 1) / horizontalSteps) * sin(PI * k / verticalSteps), // top right
                    cos(PI * k / verticalSteps),
                    cos(2.0 * PI * (j + 1) / horizontalSteps) * sin(PI * k / verticalSteps),
                )

            val triangle_ptr = Triangle(v0, v1, v2)
            list.add(triangle_ptr)
        }

        //  define the other triangles
        k = 1
        while (k <= verticalSteps - 2) {
            for (j in 0 until horizontalSteps) {
                // define the first triangle

                // vertices
                // bottom left, use k + 1, j
                var v0 =
                    Point3D(
                        sin(2.0 * PI * j / horizontalSteps) * sin(PI * (k + 1) / verticalSteps),
                        cos(PI * (k + 1) / verticalSteps),
                        cos(2.0 * PI * j / horizontalSteps) * sin(PI * (k + 1) / verticalSteps),
                    )
                // bottom  right, use k + 1, j + 1
                var v1 =
                    Point3D(
                        sin(2.0 * PI * (j + 1) / horizontalSteps) * sin(PI * (k + 1) / verticalSteps),
                        cos(PI * (k + 1) / verticalSteps),
                        cos(2.0 * PI * (j + 1) / horizontalSteps) * sin(PI * (k + 1) / verticalSteps),
                    )
                // top left, 	use k, j
                var v2 =
                    Point3D(
                        sin(2.0 * PI * j / horizontalSteps) * sin(PI * k / verticalSteps),
                        cos(PI * k / verticalSteps),
                        cos(2.0 * PI * j / horizontalSteps) * sin(PI * k / verticalSteps),
                    )

                val triangle_ptr1 = Triangle(v0, v1, v2)
                list.add(triangle_ptr1)

                // define the second triangle

                // vertices
                // top right, use k, j + 1
                v0 =
                    Point3D(
                        sin(2.0 * PI * (j + 1) / horizontalSteps) * sin(PI * k / verticalSteps),
                        cos(PI * k / verticalSteps),
                        cos(2.0 * PI * (j + 1) / horizontalSteps) * sin(PI * k / verticalSteps),
                    )
                // top left, 	use k, j
                v1 =
                    Point3D(
                        sin(2.0 * PI * j / horizontalSteps) * sin(PI * k / verticalSteps),
                        cos(PI * k / verticalSteps),
                        cos(2.0 * PI * j / horizontalSteps) * sin(PI * k / verticalSteps),
                    )
                // bottom  right, use k + 1, j + 1
                v2 =
                    Point3D(
                        sin(2.0 * PI * (j + 1) / horizontalSteps) * sin(PI * (k + 1) / verticalSteps),
                        cos(PI * (k + 1) / verticalSteps),
                        cos(2.0 * PI * (j + 1) / horizontalSteps) * sin(PI * (k + 1) / verticalSteps),
                    )

                val triangle_ptr2 = Triangle(v0, v1, v2)
                list.add(triangle_ptr2)
            }
            k++
        }
    }

    fun tessellateSmoothSphere(
        list: MutableList<SmoothTriangle>,
        horizontalSteps: Int,
        verticalSteps: Int,
    ) {
        // define the top triangles which all touch the north pole
        var k = 1

        for (j in 0 until horizontalSteps) {
            // define vertices

            val v0 = Point3D(0.0, 1.0, 0.0) // top (north pole)

            val v1 =
                Point3D(
                    sin(2.0 * PI * j / horizontalSteps) * sin(PI * k / verticalSteps), // bottom left
                    cos(PI * k / verticalSteps),
                    cos(2.0 * PI * j / horizontalSteps) * sin(PI * k / verticalSteps),
                )

            val v2 =
                Point3D(
                    sin(2.0 * PI * (j + 1) / horizontalSteps) * sin(PI * k / verticalSteps), // bottom  right
                    cos(PI * k / verticalSteps),
                    cos(2.0 * PI * (j + 1) / horizontalSteps) * sin(PI * k / verticalSteps),
                )

            val triangle = SmoothTriangle(v0, v1, v2)
            triangle.n0 = Normal.create(Vector3D(v0))
            triangle.n1 = Normal.create(Vector3D(v1))
            triangle.n2 = Normal.create(Vector3D(v2))
            list.add(triangle)
        }

        // define the bottom triangles which all touch the south pole
        k = verticalSteps - 1
        for (j in 0 until horizontalSteps) {
            // define vertices

            val v0 =
                Point3D(
                    sin(2.0 * PI * j / horizontalSteps) * sin(PI * k / verticalSteps), // top left
                    cos(PI * k / verticalSteps),
                    cos(2.0 * PI * j / horizontalSteps) * sin(PI * k / verticalSteps),
                )

            val v1 = Point3D(0.0, -1.0, 0.0) // bottom (south pole)
            // top right
            val v2 =
                Point3D(
                    sin(2.0 * PI * (j + 1) / horizontalSteps) * sin(PI * k / verticalSteps),
                    cos(PI * k / verticalSteps),
                    cos(2.0 * PI * (j + 1) / horizontalSteps) * sin(PI * k / verticalSteps),
                )

            val triangle = SmoothTriangle(v0, v1, v2)
            triangle.n0 = Normal.create(Vector3D(v0))
            triangle.n1 = Normal.create(Vector3D(v1))
            triangle.n2 = Normal.create(Vector3D(v2))
            list.add(triangle)
        }

        //  define the other triangles
        k = 1
        while (k <= verticalSteps - 2) {
            for (j in 0 until horizontalSteps) {
                // define the first triangle
                // vertices
                // bottom left, use k + 1, j
                var v0 =
                    Point3D(
                        sin(2.0 * PI * j / horizontalSteps) * sin(PI * (k + 1) / verticalSteps),
                        cos(PI * (k + 1) / verticalSteps),
                        cos(2.0 * PI * j / horizontalSteps) * sin(PI * (k + 1) / verticalSteps),
                    )
                // bottom  right, use k + 1, j + 1
                var v1 =
                    Point3D(
                        sin(2.0 * PI * (j + 1) / horizontalSteps) * sin(PI * (k + 1) / verticalSteps),
                        cos(PI * (k + 1) / verticalSteps),
                        cos(2.0 * PI * (j + 1) / horizontalSteps) * sin(PI * (k + 1) / verticalSteps),
                    )
                // top left, 	use k, j
                var v2 =
                    Point3D(
                        sin(2.0 * PI * j / horizontalSteps) * sin(PI * k / verticalSteps),
                        cos(PI * k / verticalSteps),
                        cos(2.0 * PI * j / horizontalSteps) * sin(PI * k / verticalSteps),
                    )

                val triangle = SmoothTriangle(v0, v1, v2)
                triangle.n0 = Normal.create(Vector3D(v0))
                triangle.n1 = Normal.create(Vector3D(v1))
                triangle.n2 = Normal.create(Vector3D(v2))
                list.add(triangle)

                // define the second triangle

                // vertices
                // top right, use k, j + 1
                v0 =
                    Point3D(
                        sin(2.0 * PI * (j + 1) / horizontalSteps) * sin(PI * k / verticalSteps),
                        cos(PI * k / verticalSteps),
                        cos(2.0 * PI * (j + 1) / horizontalSteps) * sin(PI * k / verticalSteps),
                    )
                // top left, 	use k, j
                v1 =
                    Point3D(
                        sin(2.0 * PI * j / horizontalSteps) * sin(PI * k / verticalSteps),
                        cos(PI * k / verticalSteps),
                        cos(2.0 * PI * j / horizontalSteps) * sin(PI * k / verticalSteps),
                    )
                // bottom  right, use k + 1, j + 1
                v2 =
                    Point3D(
                        sin(2.0 * PI * (j + 1) / horizontalSteps) * sin(PI * (k + 1) / verticalSteps),
                        cos(PI * (k + 1) / verticalSteps),
                        cos(2.0 * PI * (j + 1) / horizontalSteps) * sin(PI * (k + 1) / verticalSteps),
                    )

                val triangle2 = SmoothTriangle(v0, v1, v2)
                triangle2.n0 = Normal.create(Vector3D(v0))
                triangle2.n1 = Normal.create(Vector3D(v1))
                triangle2.n2 = Normal.create(Vector3D(v2))
                list.add(triangle2)
            }
            k++
        }
    }
}
