package net.dinkla.raytracer.objects.acceleration.kdtree.builder

import net.dinkla.raytracer.math.Axis
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.objects.GeometricObject
import net.dinkla.raytracer.objects.acceleration.kdtree.Node
import net.dinkla.raytracer.objects.acceleration.kdtree.InnerNode
import net.dinkla.raytracer.objects.acceleration.kdtree.KDTree
import net.dinkla.raytracer.objects.acceleration.kdtree.Leaf
import net.dinkla.raytracer.objects.utilities.ListUtilities
import net.dinkla.raytracer.interfaces.Counter
import net.dinkla.raytracer.interfaces.jvm.getLogger
import java.util.TreeSet

class Test2Builder : TreeBuilder {

    override var maxDepth = 15
    var minChildren = 4

    override fun build(tree: KDTree, voxel: BBox): Node {
        return build(tree.objects, tree.boundingBox, 0)
    }

    class Partitioner(objects: List<GeometricObject>, voxel: BBox) {

        internal var root: Triple

        var candidatesX: MutableSet<Double>
        var candidatesY: MutableSet<Double>
        var candidatesZ: Set<Double>

        val isFound: Boolean
            get() = true

        init {
            root = Triple()
            root.objects = objects
            root.bbox = voxel
            root.update()

            candidatesX = TreeSet()
            candidatesY = TreeSet()
            candidatesZ = TreeSet()

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
            internal var objects: List<GeometricObject>? = null
            internal var volume: Double = 0.toDouble()

            init {
                objects = ArrayList()
            }

            fun update() {
                //bbox = BBox.create(objects);
                volume = bbox!!.volume
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
                val fL = left.volume / vol
                val fR = right.volume / vol
                val sL = left.objects!!.size.toDouble()
                val sR = right.objects!!.size.toDouble()
                //                return (constF + fL * sL + fR * sR);
                return (constF + fL * sL + fR * sR) * (5 * (sL + sR) / parent?.objects!!.size)
            }

            companion object {
                const val constF = 0.333334

                fun max(): Split {
                    val s = Split(null)
                    s.sah = java.lang.Float.POSITIVE_INFINITY.toDouble()
                    return s
                }
            }
        }

        fun x(axis: Axis, cs: Set<Double>): Split? {
            var min: Split? = null
            for (split in cs) {
                if (root.bbox!!.p.ith(axis) <= split && split <= root.bbox!!.q.ith(axis)) {
                    val s = calcSplit(axis, split, root)
                    if (s.isOk && (null == min || s.sah < min.sah)) {
                        //                    LOGGER.info("Split: axis=" + axis + ", split=" + split + ", sah=" + s.sah + ", left=" + s.left.objects.size() + ", right=" + s.right.objects.size() + ", min=" + (null == min ? -1 : min.sah) );
                        min = s
                    }
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

                s.left.bbox = parent.bbox!!.splitLeft(axis, split)
                s.right.bbox = parent.bbox!!.splitRight(axis, split)
                s.update()
                return s
            }
        }
    }

    fun build(objects: List<GeometricObject>?, voxel: BBox?, depth: Int): Node {

        Counter.count("KDtree.build")

        val node: Node?

        if (objects!!.size < minChildren || depth >= maxDepth) {
            Counter.count("KDtree.build.leaf")
            node = Leaf(objects)
            return node
        }

        Counter.count("KDtree.build.node")

        val par = Partitioner(objects, voxel
                ?: BBox())

        val sX = par.x(Axis.X, par.candidatesX)
        val sY = par.x(Axis.Y, par.candidatesY)
        val sZ = par.x(Axis.Z, par.candidatesZ)

        val split: Partitioner.Split?

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
            node = InnerNode(left, right, voxel!!, split.split, split.axis!!)
        }

        return node
    }

    companion object {
        internal val LOGGER = getLogger(this::class.java)

        fun isLess(x: Partitioner.Split?, y: Partitioner.Split?): Boolean {
            return if (x != null && y != null) {
                x.sah < y.sah
            } else if (x != null && y == null) {
                true
            } else {
                false
            }
        }
    }
}


