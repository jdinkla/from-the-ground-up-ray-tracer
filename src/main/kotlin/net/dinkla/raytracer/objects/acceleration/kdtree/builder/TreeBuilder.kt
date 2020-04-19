package net.dinkla.raytracer.objects.acceleration.kdtree.builder

import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.objects.acceleration.kdtree.Node
import net.dinkla.raytracer.objects.acceleration.kdtree.KDTree

interface TreeBuilder {

    var maxDepth: Int

    fun build(tree: KDTree, voxel: BBox): Node

}
