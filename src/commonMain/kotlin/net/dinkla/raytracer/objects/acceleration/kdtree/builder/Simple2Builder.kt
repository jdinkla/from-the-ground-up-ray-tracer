package net.dinkla.raytracer.objects.acceleration.kdtree.builder

import net.dinkla.raytracer.math.Axis
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.objects.IGeometricObject
import net.dinkla.raytracer.objects.acceleration.kdtree.InnerNode
import net.dinkla.raytracer.objects.acceleration.kdtree.KDTree
import net.dinkla.raytracer.objects.acceleration.kdtree.Leaf
import net.dinkla.raytracer.objects.acceleration.kdtree.Node
import net.dinkla.raytracer.utilities.Counter
import net.dinkla.raytracer.utilities.Logger
import kotlin.math.abs

class Simple2Builder : TreeBuilder {
    override var maxDepth = 10

    private var minChildren = 4

    override fun build(
        tree: KDTree,
        voxel: BBox,
    ): Node = build(tree.objects, tree.boundingBox, 0)

    fun build(
        objects: List<IGeometricObject>,
        voxel: BBox,
        depth: Int,
    ): Node {
        Counter.count("KDtree.build")

        if (objects.size < minChildren || depth >= maxDepth) {
            Counter.count("KDtree.build.leaf")
            return Leaf(objects)
        }

        Counter.count("KDtree.build.node")

        val half = voxel.q.minus(voxel.p).times(0.5)
        val mid = voxel.p.plus(half)

        val candidateX = scanAxis(objects, voxel, Axis.X, mid.x)
        val candidateY = scanAxis(objects, voxel, Axis.Y, mid.y)
        val candidateZ = scanAxis(objects, voxel, Axis.Z, mid.z)

        // The original scanned x, y, z in sequence reusing one `split` variable, so the value used
        // for the node and the log is always the last axis scanned (z), regardless of which wins.
        val split = mid.z
        val n = objects.size

        val best = selectBestCandidate(candidateX, candidateY, candidateZ, n)
        val objectsL = best.objectsL
        val objectsR = best.objectsR

        return if (objectsL.size + objectsR.size > n * 1.5) {
            Leaf(objects)
        } else {
            Logger.info(
                "Splitting " + objects.size + " objects into " + objectsL.size + " and " +
                    objectsR.size + " objects at " + split + " with depth " + depth,
            )
            val left = build(objectsL, best.voxelL, depth + 1)
            val right = build(objectsR, best.voxelR, depth + 1)
            InnerNode(left, right, voxel, split, Axis.fromInt(depth))
        }
    }

    /**
     * Scores the mid-plane split of [voxel] on [axis] at [split]: partitions [objects] into the
     * left (lower bound <= split) and right (upper bound >= split) halves, counts how many straddle
     * both, and builds the two child voxels clamped to the split plane.
     */
    private fun scanAxis(
        objects: List<IGeometricObject>,
        voxel: BBox,
        axis: Axis,
        split: Double,
    ): Candidate {
        val objectsL = ArrayList<IGeometricObject>()
        val objectsR = ArrayList<IGeometricObject>()
        var both = 0

        for (`object` in objects) {
            val bbox = `object`.boundingBox
            var isBoth = false
            if (bbox.p.ith(axis) <= split) {
                objectsL.add(`object`)
                isBoth = true
            }
            if (bbox.q.ith(axis) >= split) {
                objectsR.add(`object`)
                if (isBoth) {
                    both++
                }
            }
        }

        val voxelL = BBox(voxel.p, withComponent(voxel.q, axis, split))
        val voxelR = BBox(withComponent(voxel.p, axis, split), voxel.q)
        return Candidate(objectsL, objectsR, both, voxelL, voxelR)
    }

    /** The split cost: balance penalty plus weighted straddle and duplication penalties (lower is better). */
    private fun cost(
        candidate: Candidate,
        n: Int,
    ): Int {
        val balance = abs(candidate.objectsL.size - candidate.objectsR.size)
        val straddlePenalty = candidate.both * STRADDLE_WEIGHT
        val duplicationPenalty = (candidate.objectsL.size + candidate.objectsR.size - n) * DUPLICATION_WEIGHT
        return balance + straddlePenalty + duplicationPenalty
    }

    /** Picks the candidate with the lowest [cost], resolving ties exactly as the original cascade did. */
    private fun selectBestCandidate(
        candidateX: Candidate,
        candidateY: Candidate,
        candidateZ: Candidate,
        n: Int,
    ): Candidate {
        val diffX = cost(candidateX, n)
        val diffY = cost(candidateY, n)
        val diffZ = cost(candidateZ, n)

        return if (diffX < diffY) {
            if (diffX < diffZ) candidateX else candidateZ
        } else {
            if (diffY < diffZ) candidateY else candidateZ
        }
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

    private data class Candidate(
        val objectsL: List<IGeometricObject>,
        val objectsR: List<IGeometricObject>,
        val both: Int,
        val voxelL: BBox,
        val voxelR: BBox,
    )

    private companion object {
        // Cost weights from the original inline heuristic (bothX * 3, duplication * 5).
        private const val STRADDLE_WEIGHT = 3
        private const val DUPLICATION_WEIGHT = 5
    }
}
