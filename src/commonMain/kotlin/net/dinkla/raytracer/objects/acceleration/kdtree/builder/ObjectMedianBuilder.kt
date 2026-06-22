package net.dinkla.raytracer.objects.acceleration.kdtree.builder

import net.dinkla.raytracer.math.Axis
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D
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
        Counter.count("KDtree.build")

        if (origObjects.size < minChildren || depth >= maxDepth) {
            Counter.count("KDtree.build.leaf")
            return Leaf(origObjects)
        }

        Counter.count("KDtree.build.node")

        val width = voxel.q.minus(voxel.p)
        val axis = widestAxis(width)

        // Sort the objects by the current axis
        // final Axis axis = Axis.fromInt(depth % 3);
        val objects = origObjects.sortedWith(compareBy { it.boundingBox.q.ith(axis) })

        val med = objects[objects.size / 2]
        val split = med.boundingBox.p.ith(axis)

        val partition = partition(objects, axis, split)
        val objectsL = partition.objectsL
        val objectsR = partition.objectsR

        return if (objects.size == objectsL.size || objects.size == objectsR.size) {
            Logger.info(
                "Not splitting " + objects.size + " objects into " + objectsL.size + " and " +
                    objectsR.size + " objects at " + split + " with depth " + depth,
            )
            Leaf(objects)
        } else {
            Logger.info(
                "Splitting " + axis + " " + objects.size + " objects into " + objectsL.size + " and " +
                    objectsR.size + " objects at " + split + " with depth " + depth + " and width " + width,
            )
            val left = build(objectsL, partition.voxelL, depth + 1)
            val right = build(objectsR, partition.voxelR, depth + 1)
            InnerNode(left, right, voxel, split, Axis.fromInt(depth))
        }
    }

    /** The axis along which [width] is largest (ties resolve to Z, matching the original cascade). */
    private fun widestAxis(width: Vector3D): Axis =
        if (width.x > width.y) {
            if (width.x > width.z) Axis.X else Axis.Z
        } else {
            if (width.y > width.z) Axis.Y else Axis.Z
        }

    /**
     * Splits [objects] (sorted by [axis]) at [split]: an object goes left when its lower bound is
     * <= split and right when its upper bound is >= split (so straddling objects land in both).
     * The child voxels are the partitions' bounding boxes clamped to the split plane on [axis].
     */
    private fun partition(
        objects: List<IGeometricObject>,
        axis: Axis,
        split: Double,
    ): Partition {
        val objectsL = ArrayList<IGeometricObject>()
        val objectsR = ArrayList<IGeometricObject>()
        for (`object` in objects) {
            val bbox = `object`.boundingBox
            if (bbox.p.ith(axis) <= split) {
                objectsL.add(`object`)
            }
            if (bbox.q.ith(axis) >= split) {
                objectsR.add(`object`)
            }
        }

        val bL = GeometricObjectUtilities.create(objectsL)
        val bR = GeometricObjectUtilities.create(objectsR)

        val voxelL = BBox(bL.p, withComponent(bL.q, axis, split))
        val voxelR = BBox(withComponent(bR.p, axis, split), bR.q)

        return Partition(objectsL, objectsR, voxelL, voxelR)
    }

    /** Copy of [point] with its [axis] component replaced by [value]. */
    private fun withComponent(
        point: Point3D,
        axis: Axis,
        value: Double,
    ): Point3D =
        when (axis) {
            Axis.X -> Point3D(value, point.y, point.z)
            Axis.Y -> Point3D(point.x, value, point.z)
            Axis.Z -> Point3D(point.x, point.y, value)
        }

    private data class Partition(
        val objectsL: List<IGeometricObject>,
        val objectsR: List<IGeometricObject>,
        val voxelL: BBox,
        val voxelR: BBox,
    )
}
