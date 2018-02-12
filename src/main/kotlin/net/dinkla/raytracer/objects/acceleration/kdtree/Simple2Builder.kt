package net.dinkla.raytracer.objects.acceleration.kdtree

import net.dinkla.raytracer.math.Axis
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.objects.GeometricObject
import net.dinkla.raytracer.utilities.Counter
import org.apache.log4j.Logger

import java.util.ArrayList

class Simple2Builder : IKDTreeBuilder {

    override var maxDepth = 10

    var minChildren = 4

    override fun build(tree: KDTree, voxel: BBox): AbstractNode {
        return build(tree.objects, tree.boundingBox, 0)
    }

    /**
     *
     * @param objects
     * @param voxel
     * @param depth
     * @return
     */
    fun build(objects: List<GeometricObject>, voxel: BBox, depth: Int): AbstractNode {

        Counter.count("KDtree.build")

        var node: AbstractNode? = null //new Leaf(objects);
        var voxelL: BBox? = null
        var voxelR: BBox? = null

        if (objects.size < minChildren || depth >= maxDepth) {
            Counter.count("KDtree.build.leaf")
            node = Leaf(objects)
            return node
        }

        Counter.count("KDtree.build.node")

        val half = voxel.q!!.minus(voxel.p!!).times(0.5)
        val mid = voxel.p.plus(half)

        var split: Double? = null

        var objectsL: List<GeometricObject> = ArrayList()
        var objectsR: List<GeometricObject> = ArrayList()

        var voxelLx: BBox? = null
        var voxelRx: BBox? = null

        var voxelLy: BBox? = null
        var voxelRy: BBox? = null

        var voxelLz: BBox? = null
        var voxelRz: BBox? = null

        val objectsLx = ArrayList<GeometricObject>()
        val objectsRx = ArrayList<GeometricObject>()
        val objectsLy = ArrayList<GeometricObject>()
        val objectsRy = ArrayList<GeometricObject>()
        val objectsLz = ArrayList<GeometricObject>()
        val objectsRz = ArrayList<GeometricObject>()

        split = mid.x

        val q1 = Point3D(mid.x, voxel.q.y, voxel.q.z)
        voxelLx = BBox(voxel.p, q1)

        val p2 = Point3D(mid.x, voxel.p.y, voxel.p.z)
        voxelRx = BBox(p2, voxel.q)

        var bothX = 0
        var bothY = 0
        var bothZ = 0

        for (`object` in objects) {
            val bbox = `object`.boundingBox
            var isBoth = false
            if (bbox.p!!.x <= split) {
                objectsLx.add(`object`)
                isBoth = true
            }
            if (bbox.q!!.x >= split) {
                objectsRx.add(`object`)
                if (isBoth) {
                    bothX++
                }
            }
        }

        split = mid.y

        val q1y = Point3D(voxel.q.x, mid.y, voxel.q.z)
        voxelLy = BBox(voxel.p, q1y)

        val p2y = Point3D(voxel.p.x, mid.y, voxel.p.z)
        voxelRy = BBox(p2y, voxel.q)

        for (`object` in objects) {
            val bbox = `object`.boundingBox
            var isBoth = false
            if (bbox.p!!.y <= split) {
                objectsLy.add(`object`)
                isBoth = true
            }
            if (bbox.q!!.y >= split) {
                objectsRy.add(`object`)
                if (isBoth) {
                    bothY++
                }
            }
        }

        split = mid.z

        val q1z = Point3D(voxel.q.x, voxel.q.y, mid.z)
        voxelLz = BBox(voxel.p, q1z)

        val p2z = Point3D(voxel.p.x, voxel.p.y, mid.z)
        voxelRz = BBox(p2z, voxel.q)

        for (`object` in objects) {
            val bbox = `object`.boundingBox
            var isBoth = false
            if (bbox.p!!.z <= split) {
                objectsLz.add(`object`)
                isBoth = true
            }
            if (bbox.q!!.z >= split) {
                objectsRz.add(`object`)
                if (isBoth) {
                    bothZ++
                }
            }
        }

        val n = objects.size

        val diffX = Math.abs(objectsLx.size - objectsRx.size) + bothX * 3 + (objectsLx.size + objectsRx.size - n) * 5
        val diffY = Math.abs(objectsLy.size - objectsRy.size) + bothY * 3 + (objectsLy.size + objectsRy.size - n) * 5
        val diffZ = Math.abs(objectsLz.size - objectsRz.size) + bothZ * 3 + (objectsLz.size + objectsRz.size - n) * 5

        if (diffX < diffY) {
            if (diffX < diffZ) {
                objectsL = objectsLx
                objectsR = objectsRx
                voxelL = voxelLx
                voxelR = voxelRx
            } else {
                objectsL = objectsLz
                objectsR = objectsRz
                voxelL = voxelLz
                voxelR = voxelRz
            }
        } else {
            if (diffY < diffZ) {
                objectsL = objectsLy
                objectsR = objectsRy
                voxelL = voxelLy
                voxelR = voxelRy
            } else {
                objectsL = objectsLz
                objectsR = objectsRz
                voxelL = voxelLz
                voxelR = voxelRz
            }
        }

        if (objectsL.size + objectsR.size > n * 1.5) {
            node = Leaf(objects)
        } else {
            LOGGER.info("Splitting " + objects.size + " objects into " + objectsL.size + " and " + objectsR.size + " objects at " + split + " with depth " + depth)
            val left = build(objectsL, voxelL, depth + 1)
            val right = build(objectsR, voxelR, depth + 1)

            node = InnerNode(left, right, voxel, split, Axis.fromInt(depth % 3))
        }

        return node
    }

    companion object {

        internal val LOGGER = Logger.getLogger(SpatialMedianBuilder::class.java)
    }

}
