package net.dinkla.raytracer.objects.acceleration.kdtree.builder

import net.dinkla.raytracer.math.Axis
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.objects.IGeometricObject
import net.dinkla.raytracer.objects.acceleration.kdtree.InnerNode
import net.dinkla.raytracer.objects.acceleration.kdtree.KDTree
import net.dinkla.raytracer.objects.acceleration.kdtree.Leaf
import net.dinkla.raytracer.objects.acceleration.kdtree.Node
import net.dinkla.raytracer.utilities.Counter
import net.dinkla.raytracer.utilities.GeometricObjectUtilities
import net.dinkla.raytracer.utilities.Logger

class ObjectMedianBuilder : TreeBuilder {
    override var maxDepth = 15
    private var minChildren = 4

    override fun build(
        tree: KDTree,
        voxel: BBox,
    ): Node = build(tree.objects, tree.boundingBox, 0)

    fun build(
        origObjects: List<IGeometricObject>,
        voxel: BBox,
        depth: Int,
    ): Node {
        var objects = origObjects
        Counter.count("KDtree.build")

        val node: Node?
        var voxelL: BBox? = null
        var voxelR: BBox? = null

        if (objects.size < minChildren || depth >= maxDepth) {
            Counter.count("KDtree.build.leaf")
            node = Leaf(objects)
            return node
        }

        Counter.count("KDtree.build.node")

        val split: Double?
        val width = voxel.q.minus(voxel.p)

        // Find the axis width the largest difference
        val axis: Axis =
            if (width.x > width.y) {
                if (width.x > width.z) {
                    Axis.X
                } else {
                    Axis.Z
                }
            } else {
                if (width.y > width.z) {
                    Axis.Y
                } else {
                    Axis.Z
                }
            }

        // Sort the objects by the current axis
        // final Axis axis = Axis.fromInt(depth % 3);
        objects = objects.sortedWith(compareBy { it.boundingBox.q.ith(axis) })

        val size = objects.size
        val minAxis = objects[0].boundingBox.p.ith(axis)
        val maxAxis = objects[objects.size - 1].boundingBox.p.ith(axis)
        val fwidth = maxAxis - minAxis

        val med = objects[size / 2]
        split = med.boundingBox.p.ith(axis)

        val objectsL = ArrayList<IGeometricObject>()
        val objectsR = ArrayList<IGeometricObject>()

        if (axis === Axis.X) { // x
            for (`object` in objects) {
                val bbox = `object`.boundingBox
                if (bbox.p.x <= split) {
                    objectsL.add(`object`)
                }
                if (bbox.q.x >= split) {
                    objectsR.add(`object`)
                }
            }

            val bL = GeometricObjectUtilities.create(objectsL)
            val bR = GeometricObjectUtilities.create(objectsR)

            val q1 = Point3D(split, bL.q.y, bL.q.z)
            val p2 = Point3D(split, bR.p.y, bR.p.z)

            voxelL = BBox(bL.p, q1)
            voxelR = BBox(p2, bR.q)
        } else if (axis === Axis.Y) { // y
            for (`object` in objects) {
                val bbox = `object`.boundingBox
                if (bbox.p.y <= split) {
                    objectsL.add(`object`)
                }
                if (bbox.q.y >= split) {
                    objectsR.add(`object`)
                }
            }
            val bL = GeometricObjectUtilities.create(objectsL)
            val bR = GeometricObjectUtilities.create(objectsR)

            val q1 = Point3D(bL.q.x, split, bL.q.z)
            val p2 = Point3D(bR.p.x, split, bR.p.z)

            voxelL = BBox(bL.p, q1)
            voxelR = BBox(p2, bR.q)
        } else if (axis === Axis.Z) { // z
            for (`object` in objects) {
                val bbox = `object`.boundingBox
                if (bbox.p.z <= split) {
                    objectsL.add(`object`)
                }
                if (bbox.q.z >= split) {
                    objectsR.add(`object`)
                }
            }

            val bL = GeometricObjectUtilities.create(objectsL)
            val bR = GeometricObjectUtilities.create(objectsR)

            val q1 = Point3D(bL.q.x, bL.q.y, split)
            val p2 = Point3D(bR.p.x, bR.p.y, split)

            voxelL = BBox(bL.p, q1)
            voxelR = BBox(p2, bR.q)
        }

        if (objects.size == objectsL.size || objects.size == objectsR.size) {
            Logger.info(
                "Not splitting " + objects.size + " objects into " + objectsL.size + " and " +
                    objectsR.size + " objects at " + split + " with depth " + depth,
            )
            node = Leaf(objects)
        } else {
            Logger.info(
                "Splitting " + axis + " " + objects.size + " objects into " + objectsL.size + " and " +
                    objectsR.size + " objects at " + split + " with depth " + depth + " and width " + width,
            )
            val left = build(objectsL, voxelL ?: BBox(), depth + 1)
            val right = build(objectsR, voxelR ?: BBox(), depth + 1)
            node = InnerNode(left, right, voxel, split, Axis.fromInt(depth))
        }

        return node
    }
}
