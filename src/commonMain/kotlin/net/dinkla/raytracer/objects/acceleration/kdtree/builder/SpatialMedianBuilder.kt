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

class SpatialMedianBuilder : TreeBuilder {
    override var maxDepth = 15
    private var minChildren = 4

    override fun build(
        tree: KDTree,
        voxel: BBox,
    ): Node = build(tree.objects, tree.boundingBox, 0)

    fun build(
        objects: List<IGeometricObject>,
        voxel: BBox?,
        depth: Int,
    ): Node {
        Counter.count("KDtree.build")

        var node: Node? = null // new Leaf(objects);
        var voxelL: BBox? = null
        var voxelR: BBox? = null

        if (objects.size < minChildren || depth >= maxDepth) {
            Counter.count("KDtree.build.leaf")
            node = Leaf(objects)
            return node
        }

        Counter.count("KDtree.build.node")

        val half = voxel!!.q.minus(voxel.p).times(0.5)
        val mid = voxel.p.plus(half)

        var split: Double? = null

        val objectsL = ArrayList<IGeometricObject>()
        val objectsR = ArrayList<IGeometricObject>()

        if (depth % 3 == 0) { // x
            split = mid.x

            val q1 = Point3D(mid.x, voxel.q.y, voxel.q.z)
            voxelL = BBox(voxel.p, q1)

            val p2 = Point3D(mid.x, voxel.p.y, voxel.p.z)
            voxelR = BBox(p2, voxel.q)

            for (geometricObject in objects) {
                val bbox = geometricObject.boundingBox
                if (bbox.p.x <= split) {
                    objectsL.add(geometricObject)
                }
                if (bbox.q.x >= split) {
                    objectsR.add(geometricObject)
                }
            }
        } else if (depth % 3 == 1) { // y
            split = mid.y

            val q1 = Point3D(voxel.q.x, mid.y, voxel.q.z)
            voxelL = BBox(voxel.p, q1)

            val p2 = Point3D(voxel.p.x, mid.y, voxel.p.z)
            voxelR = BBox(p2, voxel.q)

            for (geometricObject in objects) {
                val bbox = geometricObject.boundingBox
                if (bbox.p.y <= split) {
                    objectsL.add(geometricObject)
                }
                if (bbox.q.y >= split) {
                    objectsR.add(geometricObject)
                }
            }
        } else if (depth % 3 == 2) { // z
            split = mid.z

            val q1 = Point3D(voxel.q.x, voxel.q.y, mid.z)
            voxelL = BBox(voxel.p, q1)

            val p2 = Point3D(voxel.p.x, voxel.p.y, mid.z)
            voxelR = BBox(p2, voxel.q)

            for (geometricObject in objects) {
                val bbox = geometricObject.boundingBox
                if (bbox.p.z <= split) {
                    objectsL.add(geometricObject)
                }
                if (bbox.q.z >= split) {
                    objectsR.add(geometricObject)
                }
            }
        }

        Logger.info(
            "Splitting " + objects.size + " objects into " + objectsL.size + " and " +
                objectsR.size + " objects at " + split + " with depth " + depth,
        )
        val left = build(objectsL, voxelL, depth + 1)
        val right = build(objectsR, voxelR, depth + 1)
        return InnerNode(left, right, voxel, split!!, Axis.fromInt(depth))
    }
}
