package net.dinkla.raytracer.objects.acceleration.kdtree

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.objects.Sphere
import net.dinkla.raytracer.objects.acceleration.kdtree.builder.SpatialMedianBuilder

/**
 * Tests for the [KDTree] wrapper itself: building over a line of spheres, the public hit contract and
 * the [KDTree.initialize] heuristic-depth branch (the `n != builder.maxDepth` warning path).
 *
 * The node-level write-back of [KDTree.hit] / [KDTree.shadowHit] is pinned in KDTreeBuilderTest; here
 * the focus is the wrapper's own structure (size, root type) and the initialize depth heuristic.
 */
private fun lineOfSpheres(n: Int): List<Sphere> =
    (0 until n).map { Sphere(Point3D(it * 2.0, 0.0, 0.0), 0.5) }

private fun built(objects: List<Sphere>): KDTree {
    val tree = KDTree(SpatialMedianBuilder())
    tree.add(objects)
    tree.initialize()
    return tree
}

class KDTreeTest : StringSpec({

    "a freshly built tree reports the number of objects it was given" {
        val tree = built(lineOfSpheres(8))

        tree.size() shouldBe 8
    }

    "the root is an inner node once there are enough objects to split" {
        val tree = built(lineOfSpheres(8))

        tree.root.shouldBeInstanceOf<InnerNode>()
    }

    "the root is a single leaf when the object set is below the split threshold" {
        val tree = built(lineOfSpheres(2))

        tree.root.shouldBeInstanceOf<Leaf>()
    }

    "initialize with a small object set whose ideal depth differs from the builder's still builds a usable tree" {
        // n = 8 + floor(1.3*log2(8)) = 8 + 3 = 11, which differs from the default maxDepth (15),
        // exercising the "ideal maxDepth != configured" diagnostic (debug-log) branch in initialize().
        val tree = built(lineOfSpheres(8))

        val ray = Ray(Point3D(-10.0, 0.0, 0.0), Vector3D(1.0, 0.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)
        tree.hit(ray, sr).shouldBeTrue()
    }

    "initialize with an object count whose ideal depth equals the builder's maxDepth builds a usable tree" {
        // 50 spheres: n = 8 + floor(1.3*log2(50)) = 8 + 7 = 15 == default maxDepth, taking the
        // complementary branch where nothing is logged.
        val tree = built(lineOfSpheres(50))

        val ray = Ray(Point3D(-10.0, 0.0, 0.0), Vector3D(1.0, 0.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)
        tree.hit(ray, sr).shouldBeTrue()
    }

    "hit reports false for a ray that misses every sphere" {
        val tree = built(lineOfSpheres(8))

        val ray = Ray(Point3D(-10.0, 100.0, 0.0), Vector3D(1.0, 0.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        tree.hit(ray, sr).shouldBeFalse()
    }

    "hit before initialize fails fast because the root has not been built" {
        val tree = KDTree(SpatialMedianBuilder())
        tree.add(lineOfSpheres(8))
        // intentionally not initialized

        shouldThrowAny {
            tree.hit(Ray(Point3D(-10.0, 0.0, 0.0), Vector3D(1.0, 0.0, 0.0)), Hit(Double.MAX_VALUE))
        }
    }
})
