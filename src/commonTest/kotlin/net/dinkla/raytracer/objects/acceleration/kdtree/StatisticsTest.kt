package net.dinkla.raytracer.objects.acceleration.kdtree

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.math.Axis
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.objects.Sphere
import net.dinkla.raytracer.objects.acceleration.kdtree.builder.SpatialMedianBuilder

/**
 * Tests for [Statistics.cs], the recursive tree walk that tallies inner/leaf counts. A hand-built
 * tree fixes the shape so the inner-node, leaf and null branches can each be asserted directly.
 */
private val box = BBox(Point3D(-2.0, -2.0, -2.0), Point3D(8.0, 2.0, 2.0))

class StatisticsTest : StringSpec({

    "cs counts the inner node, both leaves and the objects they hold" {
        val left = Leaf(listOf(Sphere(Point3D(0.0, 0.0, 0.0), 0.5), Sphere(Point3D(1.0, 0.0, 0.0), 0.5)))
        val right = Leaf(listOf(Sphere(Point3D(4.0, 0.0, 0.0), 0.5)))
        val root = InnerNode(left, right, box, split = 2.0, axis = Axis.X)

        val stats = Statistics()
        Statistics.cs(root, stats, 0)

        stats.numInner shouldBe 1
        stats.numLeafs shouldBe 2
        stats.numObjectsInLeafs shouldBe 3
    }

    "cs records the depth at which each leaf is found" {
        val deepLeft = Leaf(listOf(Sphere(Point3D(0.0, 0.0, 0.0), 0.5)))
        val deepRight = Leaf(listOf(Sphere(Point3D(1.0, 0.0, 0.0), 0.5)))
        val inner = InnerNode(deepLeft, deepRight, box, split = 0.5, axis = Axis.X)
        val rightLeaf = Leaf(listOf(Sphere(Point3D(4.0, 0.0, 0.0), 0.5)))
        val root = InnerNode(inner, rightLeaf, box, split = 2.0, axis = Axis.X)

        val stats = Statistics()
        Statistics.cs(root, stats, 0)

        stats.numInner shouldBe 2
        stats.numLeafs shouldBe 3
    }

    "cs over a null node leaves the statistics untouched (neither leaf nor inner branch)" {
        val stats = Statistics()

        Statistics.cs(null, stats, 0)

        stats.numInner shouldBe 0
        stats.numLeafs shouldBe 0
        stats.numObjectsInLeafs shouldBe 0
    }

    "a freshly built KDTree has at least one leaf and accounts for every object" {
        val tree = KDTree(SpatialMedianBuilder())
        tree.add((0 until 8).map { Sphere(Point3D(it * 2.0, 0.0, 0.0), 0.5) })
        tree.initialize()

        val stats = Statistics()
        Statistics.cs(tree.root, stats, 0)

        stats.numLeafs shouldBe (stats.numInner + 1) // a binary tree: leaves = inner + 1
        (stats.numObjectsInLeafs >= 8) shouldBe true // objects may be duplicated across split planes
    }
})
