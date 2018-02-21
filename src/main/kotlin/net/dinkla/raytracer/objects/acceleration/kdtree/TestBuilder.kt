package net.dinkla.raytracer.objects.acceleration.kdtree

import net.dinkla.raytracer.math.Axis
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.objects.GeometricObject
import net.dinkla.raytracer.objects.utilities.ListUtilities
import net.dinkla.raytracer.utilities.Counter
import org.apache.log4j.Logger

import java.util.ArrayList

class TestBuilder : IKDTreeBuilder {

    override var maxDepth = 30
    var minChildren = 4

    override fun build(tree: KDTree, voxel: BBox): AbstractNode {
        return build(tree.objects, tree.boundingBox, 0)
    }

    class Partitioner(objects: ArrayList<GeometricObject>, voxel: BBox) {

        internal var root: Triple

        val isFound: Boolean
            get() = true

        init {
            root = Triple()
            root.objects = objects
            root.bbox = voxel
            root.update()
        }

        class Triple {

            var bbox: BBox = BBox()
            internal var objects: ArrayList<GeometricObject>? = null
            internal var volume: Double = 0.toDouble()

            init {
                objects = ArrayList()
            }

            fun update() {
                bbox = BBox.create(objects!!)
                volume = bbox.volume
            }
        }

        class Split(var parent: Triple?) {

            var axis: Axis? = null
            var split: Double = 0.toDouble()
            var left: Triple
            var right: Triple

            var sah: Double = 0.toDouble()

            val isOk: Boolean
                get() {
                    val b1 = parent?.objects!!.size <= left.objects!!.size
                    val b2 = parent?.objects!!.size <= right.objects!!.size
                    return !(b1 || b2)
                }

            init {
                left = Triple()
                right = Triple()
            }

            fun update() {
                left.update()
                right.update()
                sah = calcSah()
            }

            fun calcSah(): Double {
                val vol = parent?.volume ?: 0.0
                return (constF.toDouble()
                        + left.volume / vol * left.objects!!.size
                        + right.volume / vol * right.objects!!.size)
            }

            companion object {
                val constF = 0.333334f

                fun max(): Split {
                    val s = Split(null)
                    s.sah = java.lang.Float.POSITIVE_INFINITY.toDouble()
                    return s
                }
            }
        }

        fun x(axis: Axis, num: Int): Split? {
            var min: Split? = null
            val width = root.bbox.q!!.ith(axis) - root.bbox.p!!.ith(axis)
            // divide interval in num parts
            val step = width / (num + 1)
            for (i in 1 until num) {
                val split = root.bbox.p!!.ith(axis) + i * step
                val s = calcSplit(axis, split, root)
                if (s.isOk && (null == min || s.sah < min.sah)) {
                    //                    LOGGER.info("Split: axis=" + axis + ", split=" + split + ", sah=" + s.sah + ", left=" + s.left.objects.size() + ", right=" + s.right.objects.size() + ", min=" + (null == min ? -1 : min.sah) );
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

    fun build(objects: ArrayList<GeometricObject>?, voxel: BBox, depth: Int): AbstractNode {

        Counter.count("KDtree.build")

        var node: AbstractNode? = null

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

        var split: Partitioner.Split? = null

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
            LOGGER.info("Not splitting " + objects.size + " objects with depth " + depth)
            node = Leaf(objects)
        } else {
            assert(null != split)
            assert(null != objects)
            assert(null != split.left.objects)
            assert(null != split.right.objects)

            LOGGER.info("Splitting " + split.axis + " " + objects.size + " objects into " + split.left.objects!!.size + " and " + split.right.objects!!.size + " objects at " + split.split + " with depth " + depth)
            val left = build(split.left.objects, split.left.bbox, depth + 1)
            val right = build(split.right.objects, split.right.bbox, depth + 1)
            node = InnerNode(left, right, voxel, split.split, split.axis!!)
        }

        return node
    }

    companion object {

        internal val LOGGER = Logger.getLogger(TestBuilder::class.java)

        fun isLess(x: Partitioner.Split?, y: Partitioner.Split?): Boolean {
            return if (x != null && y != null) {
                x.sah < y.sah
            } else {
                false
            }
        }

        private fun weight(a: Int, b: Int, c: Int): Int {
            return Math.abs(a - c / 2) + Math.abs(b - c / 2)
        }
    }

}

