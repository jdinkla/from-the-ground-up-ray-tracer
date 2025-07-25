package net.dinkla.raytracer.objects.acceleration.kdtree

import net.dinkla.raytracer.utilities.Histogram

class Statistics {
    internal var numInner: Int = 0
    internal var numLeafs: Int = 0
    internal var numObjectsInLeafs: Int = 0

    internal var depthsLeafs: Histogram
    internal var depthsNodes: Histogram
    internal var numChildrenInLeafs: Histogram

    init {
        numInner = 0
        numLeafs = 0
        numObjectsInLeafs = 0

        depthsNodes = Histogram()
        depthsLeafs = Histogram()
        numChildrenInLeafs = Histogram()
    }

    companion object {
        fun cs(
            node: Node?,
            s: Statistics,
            depth: Int,
        ) {
            if (node is Leaf) {
                val n = node as Leaf?
                s.numLeafs++
                s.depthsLeafs.add(depth)
                val sz = n!!.size()
                s.numChildrenInLeafs.add(sz)
                s.numObjectsInLeafs += sz
            } else if (node is InnerNode) {
                val n = node as InnerNode?
                s.numInner++
                s.depthsNodes.add(depth)
                cs(n!!.left, s, depth + 1)
                cs(n.right, s, depth + 1)
            }
        }

        fun print(tree: KDTree) {
            val s = Statistics()
            cs(tree.root, s, 0)

            println("num objects=" + tree.size())
            println("inner nodes=" + s.numInner)
            println("leaf nodes=" + s.numLeafs)
            println("objects in leafs=" + s.numObjectsInLeafs)

            println("depthsLeafs")
            s.depthsLeafs.println()

            println("depthsNodes")
            s.depthsNodes.println()

            println("numChildrenInLeafs")
            s.numChildrenInLeafs.println()
        }
    }
}
