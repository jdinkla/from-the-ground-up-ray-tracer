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

class TestBuilder : TreeBuilder {
    override var maxDepth = 30
    private var minChildren = 4

    override fun build(
        tree: KDTree,
        voxel: BBox,
    ): Node = build(tree.objects, tree.boundingBox, 0)

    class Partitioner(
        objects: ArrayList<IGeometricObject>,
        voxel: BBox,
    ) {
        private var root: Triple

        init {
            root = Triple()
            root.objects = objects
            root.bbox = voxel
            root.update()
        }

        class Triple {
            var bbox: BBox = BBox()
            internal var objects: ArrayList<IGeometricObject>? = null
            internal var volume: Double = 0.0

            init {
                objects = ArrayList()
            }

            fun update() {
                val objs = requireNotNull(objects) { "Triple.objects not set before update()" }
                bbox = GeometricObjectUtilities.create(objs)
                volume = bbox.volume
            }
        }

        class Split(
            private var parent: Triple?,
        ) {
            var axis: Axis? = null
            var split: Double = 0.0
            var left: Triple = Triple()
            var right: Triple = Triple()

            var sah: Double = 0.0

            val isOk: Boolean
                get() {
                    val parentSize = requireNotNull(parent?.objects) { "Split.parent (or its objects) not set" }.size
                    val b1 = parentSize <= requireNotNull(left.objects) { "Split.left.objects not set" }.size
                    val b2 = parentSize <= requireNotNull(right.objects) { "Split.right.objects not set" }.size
                    return !(b1 || b2)
                }

            fun update() {
                left.update()
                right.update()
                sah = calcSah()
            }

            private fun calcSah(): Double {
                val vol = parent?.volume ?: 0.0
                val leftSize = requireNotNull(left.objects) { "Split.left.objects not set" }.size
                val rightSize = requireNotNull(right.objects) { "Split.right.objects not set" }.size
                return (constF + left.volume / vol * leftSize + right.volume / vol * rightSize)
            }

            companion object {
                const val constF = 0.333334

                fun max(): Split {
                    val s = Split(null)
                    s.sah = Double.POSITIVE_INFINITY
                    return s
                }
            }
        }

        fun x(
            axis: Axis,
            num: Int,
        ): Split? {
            var min: Split? = null
            val width = root.bbox.q.ith(axis) - root.bbox.p.ith(axis) // divide interval in num parts
            val step = width / (num + 1)
            for (i in 1 until num) {
                val split = root.bbox.p.ith(axis) + i * step
                val s = calcSplit(axis, split, root)
                if (s.isOk && (null == min || s.sah < min.sah)) {
                    min = s
                }
            }
            return min
        }

        companion object {
            fun calcSplit(
                axis: Axis,
                split: Double,
                parent: Triple,
            ): Split {
                val s = Split(parent)
                s.axis = axis
                s.split = split
                ListUtilities.splitByAxis(
                    requireNotNull(parent.objects) { "Triple.objects not set in calcSplit" },
                    split,
                    axis,
                    requireNotNull(s.left.objects) { "Split.left.objects not set in calcSplit" }.toMutableList(),
                    requireNotNull(s.right.objects) { "Split.right.objects not set in calcSplit" }.toMutableList(),
                )
                s.update()
                return s
            }
        }
    }

    fun build(
        objects: ArrayList<IGeometricObject>?,
        voxel: BBox,
        depth: Int,
    ): Node {
        Counter.count("KDtree.build")
        val node: Node?
        requireNotNull(objects) { "objects must be non-null in build()" }
        if (objects.size < minChildren || depth >= maxDepth) {
            Counter.count("KDtree.build.leaf")
            node = Leaf(objects)
            return node
        }

        Counter.count("KDtree.build.node")

        val par = Partitioner(objects, voxel)

        val sX = par.x(Axis.X, 3)
        val sY = par.x(Axis.Y, 3)
        val sZ = par.x(Axis.Z, 3)

        val split: Partitioner.Split?
        split =
            if (isLess(sX, sY)) {
                if (isLess(sX, sZ)) {
                    sX
                } else {
                    sZ
                }
            } else {
                if (isLess(sY, sZ)) {
                    sY
                } else {
                    sZ
                }
            }
        if (null == split) {
            Logger.info("Not splitting " + objects.size + " objects with depth " + depth)
            node = Leaf(objects)
        } else {
            val leftObjects = requireNotNull(split.left.objects) { "Split.left.objects not set" }
            val rightObjects = requireNotNull(split.right.objects) { "Split.right.objects not set" }

            Logger.info(
                "Splitting " + split.axis + " " + objects.size + " objects into " + leftObjects.size +
                    " and " + rightObjects.size + " objects at " + split.split + " with depth " + depth,
            )
            val left = build(split.left.objects, split.left.bbox, depth + 1)
            val right = build(split.right.objects, split.right.bbox, depth + 1)
            val axis = requireNotNull(split.axis) { "Split.axis not set" }
            node = InnerNode(left, right, voxel, split.split, axis)
        }

        return node
    }

    companion object {
        fun isLess(
            x: Partitioner.Split?,
            y: Partitioner.Split?,
        ): Boolean =
            if (x != null && y != null) {
                x.sah < y.sah
            } else {
                false
            }
    }
}
