package net.dinkla.raytracer.objects.acceleration.kdtree.builder

import net.dinkla.raytracer.math.Axis
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.objects.IGeometricObject
import net.dinkla.raytracer.objects.acceleration.kdtree.InnerNode
import net.dinkla.raytracer.objects.acceleration.kdtree.KDTree
import net.dinkla.raytracer.objects.acceleration.kdtree.Leaf
import net.dinkla.raytracer.objects.acceleration.kdtree.Node
import net.dinkla.raytracer.objects.utilities.ListUtilities
import net.dinkla.raytracer.utilities.Counter
import net.dinkla.raytracer.utilities.GeometricObjectUtilities
import net.dinkla.raytracer.utilities.Logger
import kotlin.math.abs

class ObjectMedian2Builder : TreeBuilder {

    override var maxDepth = 15
    private var minChildren = 4

    override fun build(tree: KDTree, voxel: BBox): Node {
        return build(tree.objects, tree.boundingBox, 0)
    }

    class Partitioner(internal var objects: List<IGeometricObject>) {

        internal var objectsL: ArrayList<IGeometricObject>
        internal var objectsR: ArrayList<IGeometricObject>

        private var objectsLx: ArrayList<IGeometricObject>
        private var objectsRx: ArrayList<IGeometricObject>

        private var objectsLy: ArrayList<IGeometricObject>
        private var objectsRy: ArrayList<IGeometricObject>

        private var objectsLz: ArrayList<IGeometricObject>
        private var objectsRz: ArrayList<IGeometricObject>

        internal var axis: Axis = Axis.X
        internal var size: Int = 0

        internal var split: Double? = null

        private var splitX: Double? = null
        private var splitY: Double? = null
        private var splitZ: Double? = null

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
            val splitXtmp = median.boundingBox.q.ith(axis)
            splitX = splitXtmp
            ListUtilities.splitByAxis(objects, splitXtmp, axis, objectsLx, objectsRx)
            val weightX = weight(objectsLx.size, objectsRx.size, size)

            // --------------- Y ---------------
            axis = Axis.Y
            ListUtilities.sortByAxis(objects, axis)
            median = objects[medianIndex]
            val splitYtmp = median.boundingBox.q.ith(axis)
            splitY = splitYtmp
            ListUtilities.splitByAxis(objects, splitYtmp, axis, objectsLy, objectsRy)
            val weightY = weight(objectsLy.size, objectsRy.size, size)

            // --------------- Z ---------------
            axis = Axis.Z
            ListUtilities.sortByAxis(objects, axis)
            median = objects[medianIndex]
            val splitZtmp = median.boundingBox.q.ith(axis)
            splitZ = splitZtmp
            ListUtilities.splitByAxis(objects, splitZtmp, axis, objectsLz, objectsRz)
            val weightZ = weight(objectsLz.size, objectsRz.size, size)

            Logger.info("weightX=" + weightX + " (" + objectsLx.size + ", " + objectsRx.size
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
            when {
                axis === Axis.X -> {
                    // x
                    val bL = GeometricObjectUtilities.create(objectsLx)
                    val bR = GeometricObjectUtilities.create(objectsRx)

                    val q1x = Point3D(splitX!!, bL.q.y, bL.q.z)
                    val p2x = Point3D(splitX!!, bR.p.y, bR.p.z)

                    voxelL = BBox(bL.p, q1x)
                    voxelR = BBox(p2x, bR.q)

                    objectsL = objectsLx
                    objectsR = objectsRx

                    split = splitX
                }
                axis === Axis.Y -> {
                    // y
                    val bL = GeometricObjectUtilities.create(objectsLy)
                    val bR = GeometricObjectUtilities.create(objectsRy)

                    val q1 = Point3D(bL.q.x, splitY!!, bL.q.z)
                    val p2 = Point3D(bR.p.x, splitY!!, bR.p.z)

                    voxelL = BBox(bL.p, q1)
                    voxelR = BBox(p2, bR.q)

                    objectsL = objectsLy
                    objectsR = objectsRy

                    split = splitY
                }
                axis === Axis.Z -> {
                    // z
                    val bL = GeometricObjectUtilities.create(objectsLz)
                    val bR = GeometricObjectUtilities.create(objectsRz)

                    val q1 = Point3D(bL.q.x, bL.q.y, splitZ!!)
                    val p2 = Point3D(bR.p.x, bR.p.y, splitZ!!)

                    voxelL = BBox(bL.p, q1)
                    voxelR = BBox(p2, bR.q)

                    objectsL = objectsLz
                    objectsR = objectsRz

                    split = splitZ
                }
            }
        }
    }

    fun build(objects: List<IGeometricObject>, voxel: BBox?, depth: Int): Node {

        Counter.count("KDtree.build")

        var node: Node? = null

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
                Logger.info("Not splitting " + objects.size + " objects into " + par.objectsL.size + " and " + par.objectsR.size + " objects at " + par.split + " with depth " + depth)
                node = Leaf(objects)
            }
        }
        if (null == node) {
            Logger.info("Splitting " + par.axis + " " + objects.size + " objects into " + par.objectsL.size + " and " + par.objectsR.size + " objects at " + par.split + " with depth " + depth)
            val left = build(par.objectsL, par.voxelL, depth + 1)
            val right = build(par.objectsR, par.voxelR, depth + 1)
            node = InnerNode(left, right, voxel!!, par.split!!, Axis.fromInt(depth % 3))
        }

        return node
    }

    companion object {
        private fun weight(a: Int, b: Int, c: Int): Int {
            return abs(a - c / 2) + abs(b - c / 2)
        }
    }

}
