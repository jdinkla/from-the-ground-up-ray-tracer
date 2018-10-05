package net.dinkla.raytracer.objects.acceleration.kdtree

import net.dinkla.raytracer.math.Axis
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.objects.GeometricObject
import net.dinkla.raytracer.utilities.Counter
import org.slf4j.LoggerFactory
import java.util.*

class ObjectMedianBuilder : IKDTreeBuilder {

    override var maxDepth = 15
    var minChildren = 4

    override fun build(tree: KDTree, voxel: BBox): AbstractNode {
        return build(tree.objects, tree.boundingBox, 0)
    }

    fun build(origObjects: List<GeometricObject>, voxel: BBox, depth: Int): AbstractNode {

        var objects = origObjects
        Counter.count("KDtree.build")

        var node: AbstractNode? = null //new Leaf(objects);
        var voxelL: BBox? = null
        var voxelR: BBox? = null

        if (objects.size < minChildren || depth >= maxDepth) {
            Counter.count("KDtree.build.leaf")
            node = Leaf(objects)
            return node
        }

        Counter.count("KDtree.build.node")

        var split: Double? = null
        val width = voxel.q!!.minus(voxel.p!!)

        // Find the axis width the largest difference
        var axis: Axis? = null
        if (width.x > width.y) {
            if (width.x > width.z) {
                axis = Axis.X
            } else {
                axis = Axis.Z
            }
        } else {
            if (width.y > width.z) {
                axis = Axis.Y
            } else {
                axis = Axis.Z
            }
        }

        val axis2 = axis
        // Sort the objects by the current axis
        // final Axis axis = Axis.fromInt(depth % 3);

        objects = objects.sortedWith(compareBy { it.boundingBox.q?.ith(axis2) })

//        objects.sortedWith { o1, o2 ->
//            val oP = o1 as GeometricObject
//            val oQ = o2 as GeometricObject
//            val bboxP = oP.boundingBox
//            val bboxQ = oQ.boundingBox
//            val p = bboxP.q
//            val q = bboxQ.q
//            java.lang.Double.compare(p!!.ith(axis2), q!!.ith(axis2))
//        }

        val size = objects.size
        val minAxis = objects[0].boundingBox.p!!.ith(axis)
        val maxAxis = objects[objects.size - 1].boundingBox.p!!.ith(axis)
        val fwidth = maxAxis - minAxis

        val med = objects[size / 2]
        split = med.boundingBox.p!!.ith(axis)

        val objectsL = ArrayList<GeometricObject>()
        val objectsR = ArrayList<GeometricObject>()

        if (axis2 === Axis.X) {
            // x
            for (`object` in objects) {
                val bbox = `object`.boundingBox
                if (bbox.p!!.x <= split) {
                    objectsL.add(`object`)
                }
                if (bbox.q!!.x >= split) {
                    objectsR.add(`object`)
                }
            }

            val bL = BBox.create(objectsL)
            val bR = BBox.create(objectsR)

            val q1 = Point3D(split, bL.q!!.y, bL.q.z)
            val p2 = Point3D(split, bR.p!!.y, bR.p.z)

            voxelL = BBox(bL.p, q1)
            voxelR = BBox(p2, bR.q)
        } else if (axis2 === Axis.Y) {
            // y
            for (`object` in objects) {
                val bbox = `object`.boundingBox
                if (bbox.p!!.y <= split) {
                    objectsL.add(`object`)
                }
                if (bbox.q!!.y >= split) {
                    objectsR.add(`object`)
                }
            }
            val bL = BBox.create(objectsL)
            val bR = BBox.create(objectsR)

            val q1 = Point3D(bL.q!!.x, split, bL.q.z)
            val p2 = Point3D(bR.p!!.x, split, bR.p.z)

            voxelL = BBox(bL.p, q1)
            voxelR = BBox(p2, bR.q)
        } else if (axis2 === Axis.Z) {
            // z
            for (`object` in objects) {
                val bbox = `object`.boundingBox
                if (bbox.p!!.z <= split) {
                    objectsL.add(`object`)
                }
                if (bbox.q!!.z >= split) {
                    objectsR.add(`object`)
                }
            }

            val bL = BBox.create(objectsL)
            val bR = BBox.create(objectsR)

            val q1 = Point3D(bL.q!!.x, bL.q.y, split)
            val p2 = Point3D(bR.p!!.x, bR.p.y, split)

            voxelL = BBox(bL.p, q1)
            voxelR = BBox(p2, bR.q)
        }

        if (objects.size == objectsL.size || objects.size == objectsR.size) {
            LOGGER.info("Not splitting " + objects.size + " objects into " + objectsL.size + " and " + objectsR.size + " objects at " + split + " with depth " + depth)
            node = Leaf(objects)
        } else {
            LOGGER.info("Splitting " + axis2 + " " + objects.size + " objects into " + objectsL.size + " and " + objectsR.size + " objects at " + split + " with depth " + depth + " and width " + width)
            val left = build(objectsL, voxelL ?: BBox(), depth + 1)
            val right = build(objectsR, voxelR?: BBox(), depth + 1)

            node = InnerNode(left, right, voxel, split, Axis.fromInt(depth % 3))
        }

        return node
    }

    companion object {
        internal val LOGGER = LoggerFactory.getLogger(this.javaClass)
    }
}
