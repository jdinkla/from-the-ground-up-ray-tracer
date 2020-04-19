package net.dinkla.raytracer.objects.beveled

import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.objects.Instance
import net.dinkla.raytracer.objects.OpenCylinder
import net.dinkla.raytracer.objects.Rectangle
import net.dinkla.raytracer.objects.Sphere
import net.dinkla.raytracer.objects.compound.Compound
import net.dinkla.raytracer.utilities.equals
import net.dinkla.raytracer.interfaces.hash

class BeveledBox(val p0: Point3D,
                 val p1: Point3D,
                 private val rb: Double,
                 private val isWiredFrame: Boolean = false) : Compound() {
    init {

        val topFrontEdge = Instance(OpenCylinder(-(p1.x - p0.x - 2 * rb) / 2, (p1.x - p0.x - 2 * rb) / 2, rb))    // top front edge
        topFrontEdge.rotateZ(90.0)
        topFrontEdge.translate((p0.x + p1.x) / 2, p1.y - rb, p1.z - rb)
        //top_front_edge.transform_texture(false);
        objects.add(topFrontEdge)

        // top back (-ve z)
        val topBackEdge = Instance(OpenCylinder(-(p1.x - p0.x - 2 * rb) / 2, (p1.x - p0.x - 2 * rb) / 2, rb))    // top back edge
        topBackEdge.rotateZ(90.0)
        topBackEdge.translate((p0.x + p1.x) / 2, p1.y - rb, p0.z + rb)
        //top_back_edge->transform_texture(false);
        objects.add(topBackEdge)


        // top right (+ve x)
        val topRightEdge = Instance(OpenCylinder(-(p1.z - p0.z - 2 * rb) / 2, (p1.z - p0.z - 2 * rb) / 2, rb)) // top right edge
        topRightEdge.rotateX(90.0)
        topRightEdge.translate(p1.x - rb, p1.y - rb, (p0.z + p1.z) / 2)
        //top_right_edge->transform_texture(false);
        objects.add(topRightEdge)


        // top left (-ve x)
        val topLeftEdge = Instance(OpenCylinder(-(p1.z - p0.z - 2 * rb) / 2, (p1.z - p0.z - 2 * rb) / 2, rb)) // top left edge
        topLeftEdge.rotateX(90.0)
        topLeftEdge.translate(p0.x + rb, p1.y - rb, (p0.z + p1.z) / 2)
        //top_left_edge->transform_texture(false);
        objects.add(topLeftEdge)

        // bottom edges  (-ve y)

        // bottom front  (+ve z)
        val bottomFrontEdge = Instance(OpenCylinder(-(p1.x - p0.x - 2 * rb) / 2, (p1.x - p0.x - 2 * rb) / 2, rb))    // bottom fromt edge
        bottomFrontEdge.rotateZ(90.0)
        bottomFrontEdge.translate((p0.x + p1.x) / 2, p0.y + rb, p1.z - rb)
        //bottom_front_edge->transform_texture(false);
        objects.add(bottomFrontEdge)


        // bottom back  (-ve z)
        val bottomBackEdge = Instance(OpenCylinder(-(p1.x - p0.x - 2 * rb) / 2, (p1.x - p0.x - 2 * rb) / 2, rb))    // bottom back edge
        bottomBackEdge.rotateZ(90.0)
        bottomBackEdge.translate((p0.x + p1.x) / 2, p0.y + rb, p0.z + rb)
        //bottom_back_edge->transform_texture(false);
        objects.add(bottomBackEdge)


        // bottom right (-ve x, -ve y)

        val bottomRightEdge = Instance(OpenCylinder(-(p1.z - p0.z - 2 * rb) / 2, (p1.z - p0.z - 2 * rb) / 2, rb)) // bottom right edge
        bottomRightEdge.rotateX(90.0)
        bottomRightEdge.translate(p1.x - rb, p0.y + rb, (p0.z + p1.z) / 2)
        //bottom_right_edge->transform_texture(false);
        objects.add(bottomRightEdge)

        // bottom left (-ve x, -ve y)

        val bottomLeftEdge = Instance(OpenCylinder(-(p1.z - p0.z - 2 * rb) / 2, (p1.z - p0.z - 2 * rb) / 2, rb)) // bottom left edge
        bottomLeftEdge.rotateX(90.0)
        bottomLeftEdge.translate(p0.x + rb, p0.y + rb, (p0.z + p1.z) / 2)
        //bottom_left_edge->transform_texture(false);
        objects.add(bottomLeftEdge)


        // vertical edges

        // vertical right front  (+ve x, +ve z)

        val verticalRightFrontEdge = Instance(OpenCylinder(p0.y + rb, p1.y - rb, rb))
        verticalRightFrontEdge.translate(p1.x - rb, 0.0, p1.z - rb)
        //vertical_right_front_edge->transform_texture(false);
        objects.add(verticalRightFrontEdge)

        // vertical left front  (-ve x, +ve z)

        val verticalLeftFrontEdge = Instance(OpenCylinder(p0.y + rb, p1.y - rb, rb))
        verticalLeftFrontEdge.translate(p0.x + rb, 0.0, p1.z - rb)
        //vertical_left_front_edge->transform_texture(false);
        objects.add(verticalLeftFrontEdge)

        // vertical left back  (-ve x, -ve z)

        val verticalLeftBackEdge = Instance(OpenCylinder(p0.y + rb, p1.y - rb, rb))
        verticalLeftBackEdge.translate(p0.x + rb, 0.0, p0.z + rb)
        //vertical_left_back_edge->transform_texture(false);
        objects.add(verticalLeftBackEdge)


        // vertical right back  (+ve x, -ve z)

        val verticalRightBackEdge = Instance(OpenCylinder(p0.y + rb, p1.y - rb, rb))
        verticalRightBackEdge.translate(p1.x - rb, 0.0, p0.z + rb)
        //vertical_right_back_edge->transform_texture(false);
        objects.add(verticalRightBackEdge)

        // corner spheres

        // top right front

        val topRightFrontCorner = Sphere(Point3D(p1.x - rb, p1.y - rb, p1.z - rb), rb)
        objects.add(topRightFrontCorner)

        // top left front  (-ve x)

        val topLeftFrontCorner = Sphere(Point3D(p0.x + rb, p1.y - rb, p1.z - rb), rb)
        objects.add(topLeftFrontCorner)

        // top left back

        val topLeftBackCorner = Sphere(Point3D(p0.x + rb, p1.y - rb, p0.z + rb), rb)
        objects.add(topLeftBackCorner)

        // top right back

        val topRightBackCorner = Sphere(Point3D(p1.x - rb, p1.y - rb, p0.z + rb), rb)
        objects.add(topRightBackCorner)

        // bottom right front

        val bottomRightFrontCorner = Sphere(Point3D(p1.x - rb, p0.y + rb, p1.z - rb), rb)
        objects.add(bottomRightFrontCorner)

        // bottom left front

        val bottomLeftFrontCorner = Sphere(Point3D(p0.x + rb, p0.y + rb, p1.z - rb), rb)
        objects.add(bottomLeftFrontCorner)

        // bottom left back

        val bottomLeftBackCorner = Sphere(Point3D(p0.x + rb, p0.y + rb, p0.z + rb), rb)
        objects.add(bottomLeftBackCorner)

        // bottom right back

        val bottomRightBackCorner = Sphere(Point3D(p1.x - rb, p0.y + rb, p0.z + rb), rb)
        objects.add(bottomRightBackCorner)

        // the faces
        if (!isWiredFrame) {
            // bottom face: -ve y
            val bottomFace = Rectangle(Point3D(p0.x + rb, p0.y, p0.z + rb),
                    Vector3D(0.0, 0.0, p1.z - rb - (p0.z + rb)),
                    Vector3D(p1.x - rb - (p0.x + rb), 0.0, 0.0),
                    Normal.DOWN)
            objects.add(bottomFace)

            // bottom face: +ve y
            val topFace = Rectangle(Point3D(p0.x + rb, p1.y, p0.z + rb),
                    Vector3D(0.0, 0.0, p1.z - rb - (p0.z + rb)),
                    Vector3D(p1.x - rb - (p0.x + rb), 0.0, 0.0),
                    Normal.UP)
            objects.add(topFace)

            // back face: -ve z
            val backFace = Rectangle(Point3D(p0.x + rb, p0.y + rb, p0.z),
                    Vector3D(p1.x - rb - (p0.x + rb), 0.0, 0.0),
                    Vector3D(0.0, p1.y - rb - (p0.y + rb), 0.0),
                    Normal.BACKWARD)
            objects.add(backFace)

            // front face: +ve z
            val frontFace = Rectangle(Point3D(p0.x + rb, p0.y + rb, p1.z),
                    Vector3D(p1.x - rb - (p0.x + rb), 0.0, 0.0),
                    Vector3D(0.0, p1.y - rb - (p0.y + rb), 0.0),
                    Normal.FORWARD)
            objects.add(frontFace)

            // left face: -ve x
            val leftFace = Rectangle(Point3D(p0.x, p0.y + rb, p0.z + rb),
                    Vector3D(0.0, 0.0, p1.z - rb - (p0.z + rb)),
                    Vector3D(0.0, p1.y - rb - (p0.y + rb), 0.0),
                    Normal.LEFT)
            objects.add(leftFace)

            // right face: +ve x
            val rightFace = Rectangle(Point3D(p1.x, p0.y + rb, p0.z + rb),
                    Vector3D(0.0, 0.0, p1.z - rb - (p0.z + rb)),
                    Vector3D(0.0, p1.y - rb - (p0.y + rb), 0.0),
                    Normal.RIGHT)
            objects.add(rightFace)
        }
    }

    override fun equals(other: Any?): Boolean = this.equals<BeveledBox>(other) { a, b ->
        a.p0 == b.p0 && a.p1 == b.p1 && a.rb == b.rb && a.isWiredFrame == b.isWiredFrame
    }

    override fun hashCode(): Int = hash(p0, p1, rb, isWiredFrame)

    override fun toString(): String = "BeveledBox($p0, $p1, $rb, $isWiredFrame)"
}
