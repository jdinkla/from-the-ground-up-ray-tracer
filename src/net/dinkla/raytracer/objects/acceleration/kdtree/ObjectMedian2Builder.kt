package net.dinkla.raytracer.objects.acceleration.kdtree

import net.dinkla.raytracer.math.Axis
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.objects.GeometricObject
import net.dinkla.raytracer.objects.utilities.ListUtilities
import net.dinkla.raytracer.utilities.Counter
import org.apache.log4j.Logger

import java.util.ArrayList

class ObjectMedian2Builder : IKDTreeBuilder {

    override var maxDepth = 15
    var minChildren = 4

    override fun build(tree: KDTree, voxel: BBox): AbstractNode {
        return build(tree.objects, tree.boundingBox, 0)
    }

    class Partitioner(internal var objects: List<GeometricObject>) {

        internal var objectsL: List<GeometricObject>
        internal var objectsR: List<GeometricObject>

        internal var objectsLx: List<GeometricObject>
        internal var objectsRx: List<GeometricObject>

        internal var objectsLy: List<GeometricObject>
        internal var objectsRy: List<GeometricObject>

        internal var objectsLz: List<GeometricObject>
        internal var objectsRz: List<GeometricObject>

        internal var axis: Axis = Axis.X
        internal var size: Int = 0

        internal var split: Double? = null

        internal var splitX: Double? = null
        internal var splitY: Double? = null
        internal var splitZ: Double? = null

        internal var voxelL: BBox? = null
        internal var voxelR: BBox? = null

        val isFound: Boolean
            get() {
                val b1 = objects.size == objectsL.size || objects.size == objectsR.size
                return !b1
            }

        init {
            size = objects.size

            objectsL = ArrayList()
            objectsR = ArrayList()

            objectsLx = ArrayList()
            objectsRx = ArrayList()

            objectsLy = ArrayList()
            objectsRy = ArrayList()

            objectsLz = ArrayList()
            objectsRz = ArrayList()

        }

        // TODO die sortierten merken
        fun split(medianIndex: Int) {
            // --------------- X ---------------
            axis = Axis.X
            ListUtilities.sortByAxis(objects, axis)
            var median = objects[medianIndex]
            splitX = median.boundingBox.q!!.ith(axis)
            ListUtilities.splitByAxis(objects, splitX, axis, objectsLx, objectsRx)
            val weightX = weight(objectsLx.size, objectsRx.size, size)

            // --------------- Y ---------------
            axis = Axis.Y
            ListUtilities.sortByAxis(objects, axis)
            median = objects[medianIndex]
            splitY = median.boundingBox.q!!.ith(axis)
            ListUtilities.splitByAxis(objects, splitY, axis, objectsLy, objectsRy)
            val weightY = weight(objectsLy.size, objectsRy.size, size)

            // --------------- Z ---------------
            axis = Axis.Z
            ListUtilities.sortByAxis(objects, axis)
            median = objects[medianIndex]
            splitZ = median.boundingBox.q!!.ith(axis)
            ListUtilities.splitByAxis(objects, splitZ, axis, objectsLz, objectsRz)
            val weightZ = weight(objectsLz.size, objectsRz.size, size)

            LOGGER.info("weightX=" + weightX + " (" + objectsLx.size + ", " + objectsRx.size
                    + "), weightY=" + weightY + " (" + objectsLy.size + ", " + objectsRy.size
                    + "), weightZ=" + weightZ + " (" + objectsLz.size + ", " + objectsRz.size + ")"
            )

            if (weightX < weightY) {
                if (weightX < weightZ) {
                    axis = Axis.X
                } else {
                    axis = Axis.Z
                }
            } else {
                if (weightY < weightZ) {
                    axis = Axis.Y
                } else {
                    axis = Axis.Z
                }
            }
        }

        fun select() {
            if (axis === Axis.X) {
                // x
                val bL = BBox.create(objectsLx)
                val bR = BBox.create(objectsRx)

                val q1x = Point3D(splitX!!, bL.q!!.y, bL.q.z)
                val p2x = Point3D(splitX!!, bR.p!!.y, bR.p.z)

                voxelL = BBox(bL.p, q1x)
                voxelR = BBox(p2x, bR.q)

                objectsL = objectsLx
                objectsR = objectsRx

                split = splitX
            } else if (axis === Axis.Y) {
                // y
                val bL = BBox.create(objectsLy)
                val bR = BBox.create(objectsRy)

                val q1 = Point3D(bL.q!!.x, splitY!!, bL.q.z)
                val p2 = Point3D(bR.p!!.x, splitY!!, bR.p.z)

                voxelL = BBox(bL.p, q1)
                voxelR = BBox(p2, bR.q)

                objectsL = objectsLy
                objectsR = objectsRy

                split = splitY
            } else if (axis === Axis.Z) {
                // z
                val bL = BBox.create(objectsLz)
                val bR = BBox.create(objectsRz)

                val q1 = Point3D(bL.q!!.x, bL.q.y, splitZ!!)
                val p2 = Point3D(bR.p!!.x, bR.p.y, splitZ!!)

                voxelL = BBox(bL.p, q1)
                voxelR = BBox(p2, bR.q)

                objectsL = objectsLz
                objectsR = objectsRz

                split = splitZ
            }
        }
    }

    fun build(objects: List<GeometricObject>, voxel: BBox?, depth: Int): AbstractNode {

        Counter.count("KDtree.build")

        var node: AbstractNode? = null

        if (objects.size < minChildren || depth >= maxDepth) {
            Counter.count("KDtree.build.leaf")
            node = Leaf(objects)
            return node
        }

        Counter.count("KDtree.build.node")

        val size = objects.size

        val par = Partitioner(objects)
        par.split(size / 2)
        par.select()
        val p = 10
        if (!par.isFound) {
            var i = 0
            while (i < p - 1 && !par.isFound) {
                if (i != p / 2) {
                    par.split((size * (i * 1.0 / p)).toInt())
                    par.select()
                }
                i++
            }
            if (!par.isFound) {
                LOGGER.info("Not splitting " + objects.size + " objects into " + par.objectsL.size + " and " + par.objectsR.size + " objects at " + par.split + " with depth " + depth)
                node = Leaf(objects)
            }
        }
        if (null == node) {
            LOGGER.info("Splitting " + par.axis + " " + objects.size + " objects into " + par.objectsL.size + " and " + par.objectsR.size + " objects at " + par.split + " with depth " + depth)
            val left = build(par.objectsL, par.voxelL, depth + 1)
            val right = build(par.objectsR, par.voxelR, depth + 1)
            node = InnerNode(left, right, voxel!!, par.split!!, Axis.fromInt(depth % 3))
        }

        return node
    }

    companion object {

        internal val LOGGER = Logger.getLogger(ObjectMedian2Builder::class.java)

        private fun weight(a: Int, b: Int, c: Int): Int {
            return Math.abs(a - c / 2) + Math.abs(b - c / 2)
        }
    }

}
