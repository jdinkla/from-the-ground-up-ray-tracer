package net.dinkla.raytracer.objects.beveled

import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.objects.Instance
import net.dinkla.raytracer.objects.OpenCylinder
import net.dinkla.raytracer.objects.Rectangle
import net.dinkla.raytracer.objects.Sphere
import net.dinkla.raytracer.objects.compound.Compound

class BeveledBox(val p0: Point3D, val p1: Point3D, val rb: Double, isWiredFrame: Boolean) : Compound() {
    init {

        val top_front_edge = Instance(OpenCylinder(-(p1.x - p0.x - 2 * rb) / 2, (p1.x - p0.x - 2 * rb) / 2, rb))    // top front edge
        top_front_edge.rotateZ(90.0)
        top_front_edge.translate((p0.x + p1.x) / 2, p1.y - rb, p1.z - rb)
        //top_front_edge.transform_texture(false);
        objects.add(top_front_edge)

        // top back (-ve z)

        val top_back_edge = Instance(OpenCylinder(-(p1.x - p0.x - 2 * rb) / 2, (p1.x - p0.x - 2 * rb) / 2, rb))    // top back edge
        top_back_edge.rotateZ(90.0)
        top_back_edge.translate((p0.x + p1.x) / 2, p1.y - rb, p0.z + rb)
        //top_back_edge->transform_texture(false);
        objects.add(top_back_edge)


        // top right (+ve x)

        val top_right_edge = Instance(OpenCylinder(-(p1.z - p0.z - 2 * rb) / 2, (p1.z - p0.z - 2 * rb) / 2, rb)) // top right edge
        top_right_edge.rotateX(90.0)
        top_right_edge.translate(p1.x - rb, p1.y - rb, (p0.z + p1.z) / 2)
        //top_right_edge->transform_texture(false);
        objects.add(top_right_edge)


        // top left (-ve x)

        val top_left_edge = Instance(OpenCylinder(-(p1.z - p0.z - 2 * rb) / 2, (p1.z - p0.z - 2 * rb) / 2, rb)) // top left edge
        top_left_edge.rotateX(90.0)
        top_left_edge.translate(p0.x + rb, p1.y - rb, (p0.z + p1.z) / 2)
        //top_left_edge->transform_texture(false);
        objects.add(top_left_edge)

        // bottom edges  (-ve y)

        // bottom front  (+ve z)

        val bottom_front_edge = Instance(OpenCylinder(-(p1.x - p0.x - 2 * rb) / 2, (p1.x - p0.x - 2 * rb) / 2, rb))    // bottom fromt edge
        bottom_front_edge.rotateZ(90.0)
        bottom_front_edge.translate((p0.x + p1.x) / 2, p0.y + rb, p1.z - rb)
        //bottom_front_edge->transform_texture(false);
        objects.add(bottom_front_edge)


        // bottom back  (-ve z)

        val bottom_back_edge = Instance(OpenCylinder(-(p1.x - p0.x - 2 * rb) / 2, (p1.x - p0.x - 2 * rb) / 2, rb))    // bottom back edge
        bottom_back_edge.rotateZ(90.0)
        bottom_back_edge.translate((p0.x + p1.x) / 2, p0.y + rb, p0.z + rb)
        //bottom_back_edge->transform_texture(false);
        objects.add(bottom_back_edge)


        // bottom right (-ve x, -ve y)

        val bottom_right_edge = Instance(OpenCylinder(-(p1.z - p0.z - 2 * rb) / 2, (p1.z - p0.z - 2 * rb) / 2, rb)) // bottom right edge
        bottom_right_edge.rotateX(90.0)
        bottom_right_edge.translate(p1.x - rb, p0.y + rb, (p0.z + p1.z) / 2)
        //bottom_right_edge->transform_texture(false);
        objects.add(bottom_right_edge)

        // bottom left (-ve x, -ve y)

        val bottom_left_edge = Instance(OpenCylinder(-(p1.z - p0.z - 2 * rb) / 2, (p1.z - p0.z - 2 * rb) / 2, rb)) // bottom left edge
        bottom_left_edge.rotateX(90.0)
        bottom_left_edge.translate(p0.x + rb, p0.y + rb, (p0.z + p1.z) / 2)
        //bottom_left_edge->transform_texture(false);
        objects.add(bottom_left_edge)


        // vertical edges

        // vertical right front  (+ve x, +ve z)

        val vertical_right_front_edge = Instance(OpenCylinder(p0.y + rb, p1.y - rb, rb))
        vertical_right_front_edge.translate(p1.x - rb, 0.0, p1.z - rb)
        //vertical_right_front_edge->transform_texture(false);
        objects.add(vertical_right_front_edge)

        // vertical left front  (-ve x, +ve z)

        val vertical_left_front_edge = Instance(OpenCylinder(p0.y + rb, p1.y - rb, rb))
        vertical_left_front_edge.translate(p0.x + rb, 0.0, p1.z - rb)
        //vertical_left_front_edge->transform_texture(false);
        objects.add(vertical_left_front_edge)

        // vertical left back  (-ve x, -ve z)

        val vertical_left_back_edge = Instance(OpenCylinder(p0.y + rb, p1.y - rb, rb))
        vertical_left_back_edge.translate(p0.x + rb, 0.0, p0.z + rb)
        //vertical_left_back_edge->transform_texture(false);
        objects.add(vertical_left_back_edge)


        // vertical right back  (+ve x, -ve z)

        val vertical_right_back_edge = Instance(OpenCylinder(p0.y + rb, p1.y - rb, rb))
        vertical_right_back_edge.translate(p1.x - rb, 0.0, p0.z + rb)
        //vertical_right_back_edge->transform_texture(false);
        objects.add(vertical_right_back_edge)

        // corner spheres

        // top right front

        val top_right_front_corner = Sphere(Point3D(p1.x - rb, p1.y - rb, p1.z - rb), rb)
        objects.add(top_right_front_corner)

        // top left front  (-ve x)

        val top_left_front_corner = Sphere(Point3D(p0.x + rb, p1.y - rb, p1.z - rb), rb)
        objects.add(top_left_front_corner)

        // top left back

        val top_left_back_corner = Sphere(Point3D(p0.x + rb, p1.y - rb, p0.z + rb), rb)
        objects.add(top_left_back_corner)

        // top right back

        val top_right_back_corner = Sphere(Point3D(p1.x - rb, p1.y - rb, p0.z + rb), rb)
        objects.add(top_right_back_corner)

        // bottom right front

        val bottom_right_front_corner = Sphere(Point3D(p1.x - rb, p0.y + rb, p1.z - rb), rb)
        objects.add(bottom_right_front_corner)

        // bottom left front

        val bottom_left_front_corner = Sphere(Point3D(p0.x + rb, p0.y + rb, p1.z - rb), rb)
        objects.add(bottom_left_front_corner)

        // bottom left back

        val bottom_left_back_corner = Sphere(Point3D(p0.x + rb, p0.y + rb, p0.z + rb), rb)
        objects.add(bottom_left_back_corner)

        // bottom right back

        val bottom_right_back_corner = Sphere(Point3D(p1.x - rb, p0.y + rb, p0.z + rb), rb)
        objects.add(bottom_right_back_corner)


        // the faces

        // bottom face: -ve y

        if (!isWiredFrame) {

            val bottom_face = Rectangle(Point3D(p0.x + rb, p0.y, p0.z + rb),
                    Vector3D(0.0, 0.0, p1.z - rb - (p0.z + rb)),
                    Vector3D(p1.x - rb - (p0.x + rb), 0.0, 0.0),
                    Normal(0.0, -1.0, 0.0))
            objects.add(bottom_face)


            // bottom face: +ve y

            val top_face = Rectangle(Point3D(p0.x + rb, p1.y, p0.z + rb),
                    Vector3D(0.0, 0.0, p1.z - rb - (p0.z + rb)),
                    Vector3D(p1.x - rb - (p0.x + rb), 0.0, 0.0),
                    Normal(0.0, 1.0, 0.0))
            objects.add(top_face)


            // back face: -ve z

            val back_face = Rectangle(Point3D(p0.x + rb, p0.y + rb, p0.z),
                    Vector3D(p1.x - rb - (p0.x + rb), 0.0, 0.0),
                    Vector3D(0.0, p1.y - rb - (p0.y + rb), 0.0),
                    Normal(0.0, 0.0, -1.0))
            objects.add(back_face)


            // front face: +ve z

            val front_face = Rectangle(Point3D(p0.x + rb, p0.y + rb, p1.z),
                    Vector3D(p1.x - rb - (p0.x + rb), 0.0, 0.0),
                    Vector3D(0.0, p1.y - rb - (p0.y + rb), 0.0),
                    Normal(0.0, 0.0, 1.0))
            objects.add(front_face)


            // left face: -ve x

            val left_face = Rectangle(Point3D(p0.x, p0.y + rb, p0.z + rb),
                    Vector3D(0.0, 0.0, p1.z - rb - (p0.z + rb)),
                    Vector3D(0.0, p1.y - rb - (p0.y + rb), 0.0),
                    Normal(-1.0, 0.0, 0.0))
            objects.add(left_face)


            // right face: +ve x

            val right_face = Rectangle(Point3D(p1.x, p0.y + rb, p0.z + rb),
                    Vector3D(0.0, 0.0, p1.z - rb - (p0.z + rb)),
                    Vector3D(0.0, p1.y - rb - (p0.y + rb), 0.0),
                    Normal(1.0, 0.0, 0.0))
            objects.add(right_face)
        }
    }

}
