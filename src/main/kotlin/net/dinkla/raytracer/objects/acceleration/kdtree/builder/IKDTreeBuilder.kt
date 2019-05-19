package net.dinkla.raytracer.objects.acceleration.kdtree.builder

import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.objects.acceleration.kdtree.AbstractNode
import net.dinkla.raytracer.objects.acceleration.kdtree.KDTree

interface IKDTreeBuilder {

    var maxDepth: Int

    fun build(tree: KDTree, voxel: BBox): AbstractNode

}
