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

    var minChildren = 4

    override fun build(tree: KDTree, voxel: BBox): Node {
        return build(tree.objects, tree.boundingBox, 0)
    }

    /**
     *
     * @param objects
     * @param voxel
     * @param depth
     * @return
     */
    fun build(objects: List<IGeometricObject>, voxel: BBox, depth: Int): Node {

        Counter.count("KDtree.build")

        val node: Node?
        val voxelL: BBox?
        val voxelR: BBox?

        if (objects.size < minChildren || depth >= maxDepth) {
            Counter.count("KDtree.build.leaf")
            node = Leaf(objects)
            return node
        }

        Counter.count("KDtree.build.node")

        val half = voxel.q.minus(voxel.p).times(0.5)
        val mid = voxel.p.plus(half)

        val objectsL: List<IGeometricObject>
        val objectsR: List<IGeometricObject>

        val voxelLx: BBox?
        val voxelRx: BBox?

        val voxelLy: BBox?
        val voxelRy: BBox?

        val voxelLz: BBox?
        val voxelRz: BBox?

        val objectsLx = ArrayList<IGeometricObject>()
        val objectsRx = ArrayList<IGeometricObject>()
        val objectsLy = ArrayList<IGeometricObject>()
        val objectsRy = ArrayList<IGeometricObject>()
        val objectsLz = ArrayList<IGeometricObject>()
        val objectsRz = ArrayList<IGeometricObject>()

        var split = mid.x

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
            if (bbox.p.x <= split) {
                objectsLx.add(`object`)
                isBoth = true
            }
            if (bbox.q.x >= split) {
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
            if (bbox.p.y <= split) {
                objectsLy.add(`object`)
                isBoth = true
            }
            if (bbox.q.y >= split) {
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
            if (bbox.p.z <= split) {
                objectsLz.add(`object`)
                isBoth = true
            }
            if (bbox.q.z >= split) {
                objectsRz.add(`object`)
                if (isBoth) {
                    bothZ++
                }
            }
        }

        val n = objects.size

        val diffX = abs(objectsLx.size - objectsRx.size) + bothX * 3 + (objectsLx.size + objectsRx.size - n) * 5
        val diffY = abs(objectsLy.size - objectsRy.size) + bothY * 3 + (objectsLy.size + objectsRy.size - n) * 5
        val diffZ = abs(objectsLz.size - objectsRz.size) + bothZ * 3 + (objectsLz.size + objectsRz.size - n) * 5

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
            Logger.info("Splitting " + objects.size + " objects into " + objectsL.size + " and " + objectsR.size + " objects at " + split + " with depth " + depth)
            val left = build(objectsL, voxelL, depth + 1)
            val right = build(objectsR, voxelR, depth + 1)
            node = InnerNode(left, right, voxel, split, Axis.fromInt(depth))
        }

        return node
    }
}
