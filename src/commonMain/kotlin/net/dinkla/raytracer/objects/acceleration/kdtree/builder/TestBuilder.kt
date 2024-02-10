package net.dinkla.raytracer.objects.acceleration.kdtree.builder

import net.dinkla.raytracer.math.Axis
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.objects.IGeometricObject
import net.dinkla.raytracer.objects.acceleration.kdtree.InnerNode
import net.dinkla.raytracer.objects.acceleration.kdtree.KDTree
import net.dinkla.raytracer.objects.acceleration.kdtree.Leaf
import net.dinkla.raytracer.objects.acceleration.kdtree.Node
import net.dinkla.raytracer.utilities.Counter
import net.dinkla.raytracer.utilities.GeometricObjectUtilities
import net.dinkla.raytracer.utilities.ListUtilities
import net.dinkla.raytracer.utilities.Logger
import kotlin.math.abs

class TestBuilder : TreeBuilder {

    override var maxDepth = 30
    var minChildren = 4

    override fun build(tree: KDTree, voxel: BBox): Node {
        return build(tree.objects, tree.boundingBox, 0)
    }

    class Partitioner(objects: ArrayList<IGeometricObject>, voxel: BBox) {

        internal var root: Triple

        init {
            root = Triple()
            root.objects = objects
            root.bbox = voxel
            root.update()
        }

        class Triple {

            var bbox: BBox = BBox()
            internal var objects: ArrayList<IGeometricObject>? = null
            internal var volume: Double = 0.toDouble()

            init {
                objects = ArrayList()
            }

            fun update() {
                bbox = GeometricObjectUtilities.create(objects!!)
                volume = bbox.volume
            }
        }

        class Split(var parent: Triple?) {

            var axis: Axis? = null
            var split: Double = 0.toDouble()
            var left: Triple = Triple()
            var right: Triple = Triple()

            var sah: Double = 0.toDouble()

            val isOk: Boolean
                get() {
                    val b1 = parent?.objects!!.size <= left.objects!!.size
                    val b2 = parent?.objects!!.size <= right.objects!!.size
                    return !(b1 || b2)
                }

            fun update() {
                left.update()
                right.update()
                sah = calcSah()
            }

            fun calcSah(): Double {
                val vol = parent?.volume ?: 0.0
                return (
                    constF.toDouble() +
                        left.volume / vol * left.objects!!.size +
                        right.volume / vol * right.objects!!.size
                    )
            }

            companion object {
                const val constF = 0.333334f

                fun max(): Split {
                    val s = Split(null)
                    s.sah = Double.POSITIVE_INFINITY
                    return s
                }
            }
        }

        fun x(axis: Axis, num: Int): Split? {
            var min: Split? = null
            val width = root.bbox.q.ith(axis) - root.bbox.p.ith(axis)
            // divide interval in num parts
            val step = width / (num + 1)
            for (i in 1 until num) {
                val split = root.bbox.p.ith(axis) + i * step
                val s = calcSplit(axis, split, root)
                if (s.isOk && (null == min || s.sah < min.sah)) {
                    //                    Logger.info("Split: axis=" + axis + ", split=" + split + ", sah=" + s.sah + ", left=" + s.left.objects.size() + ", right=" + s.right.objects.size() + ", min=" + (null == min ? -1 : min.sah) );
                    min = s
                }
            }
            return min
        }

        companion object {

            fun calcSplit(axis: Axis, split: Double, parent: Triple): Split {
                val s = Split(parent)
                s.axis = axis
                s.split = split
                ListUtilities.splitByAxis(parent.objects!!, split, axis, s.left.objects!!.toMutableList(), s.right.objects!!.toMutableList())
                s.update()
                return s
            }
        }
    }

    fun build(objects: ArrayList<IGeometricObject>?, voxel: BBox, depth: Int): Node {
        Counter.count("KDtree.build")
        var node: Node?
        if (objects!!.size < minChildren || depth >= maxDepth) {
            Counter.count("KDtree.build.leaf")
            node = Leaf(objects)
            return node
        }

        Counter.count("KDtree.build.node")

        val par = Partitioner(objects, voxel)

        val sX = par.x(Axis.X, 3)
        val sY = par.x(Axis.Y, 3)
        val sZ = par.x(Axis.Z, 3)

        var split: Partitioner.Split?
        if (isLess(sX, sY)) {
            if (isLess(sX, sZ)) {
                split = sX
            } else {
                split = sZ
            }
        } else {
            if (isLess(sY, sZ)) {
                split = sY
            } else {
                split = sZ
            }
        }
        if (null == split) {
            Logger.info("Not splitting " + objects.size + " objects with depth " + depth)
            node = Leaf(objects)
        } else {
            split.left.objects!!
            split.right.objects!!

            Logger.info("Splitting " + split.axis + " " + objects.size + " objects into " + split.left.objects!!.size + " and " + split.right.objects!!.size + " objects at " + split.split + " with depth " + depth)
            val left = build(split.left.objects, split.left.bbox, depth + 1)
            val right = build(split.right.objects, split.right.bbox, depth + 1)
            node = InnerNode(left, right, voxel, split.split, split.axis!!)
        }

        return node
    }

    companion object {
        fun isLess(x: Partitioner.Split?, y: Partitioner.Split?): Boolean {
            return if (x != null && y != null) {
                x.sah < y.sah
            } else {
                false
            }
        }

        private fun weight(a: Int, b: Int, c: Int): Int {
            return abs(a - c / 2) + abs(b - c / 2)
        }
    }
}
