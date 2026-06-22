package net.dinkla.raytracer.objects.acceleration.kdtree.builder

import net.dinkla.raytracer.math.Axis
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.objects.IGeometricObject
import net.dinkla.raytracer.objects.acceleration.kdtree.InnerNode
import net.dinkla.raytracer.objects.acceleration.kdtree.KDTree
import net.dinkla.raytracer.objects.acceleration.kdtree.Leaf
import net.dinkla.raytracer.objects.acceleration.kdtree.Node
import net.dinkla.raytracer.utilities.Counter
import net.dinkla.raytracer.utilities.ListUtilities
import net.dinkla.raytracer.utilities.Logger

class Test2Builder : TreeBuilder {
    override var maxDepth = 15
    private var minChildren = 4

    override fun build(
        tree: KDTree,
        voxel: BBox,
    ): Node = build(tree.objects, tree.boundingBox, 0)

    class Partitioner(
        objects: List<IGeometricObject>,
        voxel: BBox,
    ) {
        private var root: Triple

        var candidatesX: MutableSet<Double>
        var candidatesY: MutableSet<Double>
        var candidatesZ: Set<Double>

        init {
            root = Triple()
            root.objects = objects
            root.bbox = voxel
            root.update()

            candidatesX = mutableSetOf()
            candidatesY = mutableSetOf()
            candidatesZ = mutableSetOf()

            for (`object` in objects) {
                val bbox = `object`.boundingBox
                val clipped = bbox.clipTo(voxel)
                candidatesX.add(clipped.p.x)
                candidatesY.add(clipped.p.y)
                candidatesX.add(clipped.p.z)
                candidatesX.add(clipped.q.x)
                candidatesY.add(clipped.q.y)
                candidatesX.add(clipped.q.z)
            }
        }

        class Triple {
            var bbox: BBox? = null
            internal var objects: List<IGeometricObject>? = null
            internal var volume: Double = 0.0

            init {
                objects = ArrayList()
            }

            fun update() {
                // bbox = BBox.create(objects);
                volume = requireNotNull(bbox) { "Triple.bbox not set before update()" }.volume
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

            private fun parentObjects(): List<IGeometricObject> =
                requireNotNull(parent?.objects) { "Split.parent (or its objects) not set" }

            val isOk: Boolean
                get() {
                    val parentSize = parentObjects().size
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
                val fL = left.volume / vol
                val fR = right.volume / vol
                val sL = requireNotNull(left.objects) { "Split.left.objects not set" }.size.toDouble()
                val sR = requireNotNull(right.objects) { "Split.right.objects not set" }.size.toDouble()
                //                return (constF + fL * sL + fR * sR);
                return (constF + fL * sL + fR * sR) * (5 * (sL + sR) / parentObjects().size)
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
            cs: Set<Double>,
        ): Split? {
            var min: Split? = null
            val rootBBox = requireNotNull(root.bbox) { "Partitioner.root.bbox not set" }
            for (split in cs) {
                if (rootBBox.p.ith(axis) <= split && split <= rootBBox.q.ith(axis)) {
                    val s = calcSplit(axis, split, root)
                    if (s.isOk && (null == min || s.sah < min.sah)) {
                        min = s
                    }
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
                val parentBBox = requireNotNull(parent.bbox) { "Triple.bbox not set in calcSplit" }
                ListUtilities.splitByAxis(
                    requireNotNull(parent.objects) { "Triple.objects not set in calcSplit" },
                    split,
                    axis,
                    requireNotNull(s.left.objects) { "Split.left.objects not set in calcSplit" }.toMutableList(),
                    requireNotNull(s.right.objects) { "Split.right.objects not set in calcSplit" }.toMutableList(),
                )

                s.left.bbox = parentBBox.splitLeft(axis, split)
                s.right.bbox = parentBBox.splitRight(axis, split)
                s.update()
                return s
            }
        }
    }

    fun build(
        objects: List<IGeometricObject>?,
        voxel: BBox?,
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

        val par =
            Partitioner(
                objects,
                voxel
                    ?: BBox(),
            )

        val sX = par.x(Axis.X, par.candidatesX)
        val sY = par.x(Axis.Y, par.candidatesY)
        val sZ = par.x(Axis.Z, par.candidatesZ)

        val split: Partitioner.Split? =
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
            val voxelBBox = requireNotNull(voxel) { "voxel must be non-null for an inner node at depth $depth" }
            val axis = requireNotNull(split.axis) { "Split.axis not set" }
            node = InnerNode(left, right, voxelBBox, split.split, axis)
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
            } else if (x != null && y == null) {
                true
            } else {
                false
            }
    }
}
