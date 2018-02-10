package net.dinkla.raytracer.objects.acceleration.kdtree

import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.objects.GeometricObject

interface IKDTreeBuilder {

    var maxDepth: Int

    fun build(tree: KDTree, voxel: BBox): AbstractNode

}
