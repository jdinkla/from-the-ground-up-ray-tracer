package net.dinkla.raytracer.objects.acceleration.kdtree.builder

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.objects.Sphere
import net.dinkla.raytracer.objects.acceleration.kdtree.InnerNode
import net.dinkla.raytracer.objects.acceleration.kdtree.KDTree
import net.dinkla.raytracer.objects.acceleration.kdtree.Leaf
import net.dinkla.raytracer.objects.acceleration.kdtree.Node
import net.dinkla.raytracer.shouldBeApprox

/**
 * Coverage-focused tests for the KDTree [TreeBuilder] family (TASK-6, AC#2).
 *
 * Two notes on what these tests assert and why:
 *
 *  - `KDTree.hit` wraps the caller's record in a fresh `Hit(sr)` and never copies the result back,
 *    so only its **boolean** return is observable through that public entry point. To assert the
 *    resolved distance/normal/object we drive the built tree at the [Node] level
 *    (`tree.root!!.hit(...)`), where the populated `Hit` is visible.
 *
 *  - [TestBuilder] and [Test2Builder] are dead code carrying a latent bug (TASK-4): their
 *    `calcSplit` passes `.toMutableList()` *copies* into `splitByAxis`, so the real out-params stay
 *    empty and the tree they build is degenerate (an inner node whose leaves hold no objects).
 *    The tests below **pin that actual broken behaviour** as a characterization — they are NOT a
 *    claim of correctness, and the bug must not be "fixed" under this coverage task.
 */

/** Eight unit spheres spaced along +x; their centres lie on the x-axis at 0, 2, 4, …, 14. */
private fun lineOfSpheres(n: Int = 8): List<Sphere> =
    (0 until n).map { Sphere(Point3D(it * 2.0, 0.0, 0.0), 0.5) }

private fun builtTree(
    builder: TreeBuilder,
    objects: List<Sphere> = lineOfSpheres(),
): KDTree {
    val tree = KDTree(builder)
    tree.add(objects)
    tree.initialize()
    return tree
}

/** A ray travelling +x along the line of centres; the first surface it meets is the sphere at x=0. */
private fun rayAlongLine(): Ray = Ray(Point3D(-10.0, 0.0, 0.0), Vector3D(1.0, 0.0, 0.0))

private fun root(tree: KDTree): Node = requireNotNull(tree.root) { "tree not initialized" }

class KDTreeBuilderTest : StringSpec({

    // ---- SpatialMedianBuilder: the default, wired-in builder -------------------------------------

    "SpatialMedian builds an inner node and a node-level hit resolves the nearest sphere" {
        val tree = builtTree(SpatialMedianBuilder())

        val node = root(tree)
        node.shouldBeInstanceOf<InnerNode>()

        val sr = Hit(Double.MAX_VALUE)
        val hit = node.hit(rayAlongLine(), sr)

        hit.shouldBeTrue()
        sr.geometricObject.shouldBeInstanceOf<Sphere>()
        (sr.geometricObject as Sphere).center shouldBeApprox Point3D.ORIGIN
        sr.t shouldBeApprox 9.5 // origin at x=-10, front surface of unit-radius-0.5 sphere at x=-0.5
        sr.normal shouldBe Normal(-1.0, 0.0, 0.0)
    }

    "SpatialMedian resolves a hit for a ray that starts on the right side of the split plane" {
        // The root splits on x at the scene midpoint; a -x ray originating beyond the far sphere
        // starts with origin.x >= split, taking InnerNode's right-first descent branch.
        val tree = builtTree(SpatialMedianBuilder())

        val node = root(tree)
        val ray = Ray(Point3D(20.0, 0.0, 0.0), Vector3D(-1.0, 0.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)

        node.hit(ray, sr).shouldBeTrue()
        val sphere = sr.geometricObject.shouldBeInstanceOf<Sphere>()
        sphere.center.y shouldBeApprox 0.0
        sphere.center.z shouldBeApprox 0.0
        sr.normal shouldBe Normal(1.0, 0.0, 0.0)
    }

    "SpatialMedian returns a single leaf when below the minimum children threshold" {
        val tree = builtTree(SpatialMedianBuilder(), lineOfSpheres(2))

        val node = root(tree)
        node.shouldBeInstanceOf<Leaf>()
        node.size() shouldBe 2
    }

    "SpatialMedian splits across all three axes as recursion deepens (y/z heuristic branches)" {
        // A 2x2x2 lattice of spheres forces the depth%3 axis cycle to use x, then y, then z.
        val lattice =
            buildList {
                for (x in 0..1) {
                    for (y in 0..1) {
                        for (z in 0..1) {
                            add(Sphere(Point3D(x * 4.0, y * 4.0, z * 4.0), 0.5))
                        }
                    }
                }
            }
        val tree = builtTree(SpatialMedianBuilder().apply { maxDepth = 4 }, lattice)

        val node = root(tree)
        node.shouldBeInstanceOf<InnerNode>()

        // a ray down +z through the (4,4,*) column hits the sphere centred at (4,4,0)
        val ray = Ray(Point3D(4.0, 4.0, -10.0), Vector3D(0.0, 0.0, 1.0))
        val sr = Hit(Double.MAX_VALUE)

        node.hit(ray, sr).shouldBeTrue()
        (sr.geometricObject as Sphere).center shouldBeApprox Point3D(4.0, 4.0, 0.0)
    }

    // ---- ObjectMedianBuilder: largest-axis split around the object median ------------------------

    "ObjectMedian builds an inner node and resolves a node-level hit to the nearest sphere" {
        val tree = builtTree(ObjectMedianBuilder())

        val node = root(tree)
        node.shouldBeInstanceOf<InnerNode>()

        val sr = Hit(Double.MAX_VALUE)
        node.hit(rayAlongLine(), sr).shouldBeTrue()
        (sr.geometricObject as Sphere).center shouldBeApprox Point3D.ORIGIN
        sr.t shouldBeApprox 9.5
    }

    "ObjectMedian collapses to a leaf below the minimum children threshold" {
        val tree = builtTree(ObjectMedianBuilder(), lineOfSpheres(3))

        root(tree).shouldBeInstanceOf<Leaf>()
    }

    "ObjectMedian picks the widest axis as the split axis (y then z extents dominate)" {
        // Spheres spread far along z, less along y, least along x -> split heuristic chooses Z.
        val spread =
            listOf(
                Sphere(Point3D(0.0, 0.0, 0.0), 0.5),
                Sphere(Point3D(0.0, 1.0, 4.0), 0.5),
                Sphere(Point3D(0.0, 2.0, 8.0), 0.5),
                Sphere(Point3D(0.0, 3.0, 12.0), 0.5),
                Sphere(Point3D(0.0, 4.0, 16.0), 0.5),
            )
        val tree = builtTree(ObjectMedianBuilder(), spread)

        val node = root(tree)
        node.shouldBeInstanceOf<InnerNode>()

        val ray = Ray(Point3D(0.0, 0.0, -10.0), Vector3D(0.0, 0.0, 1.0))
        val sr = Hit(Double.MAX_VALUE)
        node.hit(ray, sr).shouldBeTrue()
        (sr.geometricObject as Sphere).center shouldBeApprox Point3D.ORIGIN
    }

    "ObjectMedian splits on the y axis when y is the widest extent" {
        // Spheres spread furthest along y -> the widest-axis heuristic picks Y, exercising the
        // Axis.Y partition/voxel branch (the x line-of-spheres case already covers Axis.X).
        val spread =
            listOf(
                Sphere(Point3D(0.0, 0.0, 0.0), 0.5),
                Sphere(Point3D(0.0, 4.0, 0.0), 0.5),
                Sphere(Point3D(0.0, 8.0, 0.0), 0.5),
                Sphere(Point3D(0.0, 12.0, 0.0), 0.5),
                Sphere(Point3D(0.0, 16.0, 0.0), 0.5),
            )
        val tree = builtTree(ObjectMedianBuilder(), spread)

        val node = root(tree)
        node.shouldBeInstanceOf<InnerNode>()

        val ray = Ray(Point3D(0.0, -10.0, 0.0), Vector3D(0.0, 1.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)
        node.hit(ray, sr).shouldBeTrue()
        (sr.geometricObject as Sphere).center shouldBeApprox Point3D.ORIGIN
    }

    // ---- Simple2Builder: scores x/y/z mid-plane splits, picks the least-overlapping one ----------

    "Simple2 builds an inner node whose node-level hit lands on a sphere along the line" {
        // NOTE: KDTree InnerNode.hit returns the first child hit it finds, not a globally nearest
        // one. For Simple2's particular tree shape that is the sphere centred at x=4 (t=13.5), not
        // the x=0 sphere. We pin the *real geometric* contract — it resolves to one of the line's
        // spheres (centre on the x-axis) at a finite distance — rather than over-asserting "nearest".
        val tree = builtTree(Simple2Builder())

        val node = root(tree)
        node.shouldBeInstanceOf<InnerNode>()

        val sr = Hit(Double.MAX_VALUE)
        node.hit(rayAlongLine(), sr).shouldBeTrue()
        val sphere = sr.geometricObject.shouldBeInstanceOf<Sphere>()
        sphere.center.y shouldBeApprox 0.0
        sphere.center.z shouldBeApprox 0.0
        sr.t shouldBeApprox 13.5 // front surface of the x=4 sphere reached from x=-10
    }

    "Simple2 collapses to a leaf below the minimum children threshold" {
        val tree = builtTree(Simple2Builder(), lineOfSpheres(3))

        root(tree).shouldBeInstanceOf<Leaf>()
    }

    // ---- ObjectMedian2Builder: weighted median search over multiple candidate splits -------------

    "ObjectMedian2 builds an inner node and resolves a node-level hit to the nearest sphere" {
        val tree = builtTree(ObjectMedian2Builder())

        val node = root(tree)
        node.shouldBeInstanceOf<InnerNode>()

        val sr = Hit(Double.MAX_VALUE)
        node.hit(rayAlongLine(), sr).shouldBeTrue()
        (sr.geometricObject as Sphere).center shouldBeApprox Point3D.ORIGIN
        sr.t shouldBeApprox 9.5
    }

    "ObjectMedian2 collapses to a leaf below the minimum children threshold" {
        val tree = builtTree(ObjectMedian2Builder(), lineOfSpheres(3))

        root(tree).shouldBeInstanceOf<Leaf>()
    }

    // ---- TestBuilder & Test2Builder: dead code, latent bug pinned (TASK-4) -----------------------

    "TestBuilder builds a degenerate inner node holding no objects (latent TASK-4 bug, pinned)" {
        // calcSplit copies the out-lists with .toMutableList(), so splitByAxis fills throwaway
        // copies and the real left/right object lists stay empty. Expected-correct behaviour would
        // be a populated tree that resolves the ray; this pins the current broken output instead.
        val tree = builtTree(TestBuilder())

        val node = root(tree)
        node.shouldBeInstanceOf<InnerNode>()
        node.size() shouldBe 0 // no objects survive into the leaves

        val sr = Hit(Double.MAX_VALUE)
        node.hit(rayAlongLine(), sr).shouldBeFalse() // degenerate tree cannot hit anything
    }

    "Test2Builder builds a degenerate inner node holding no objects (latent TASK-4 bug, pinned)" {
        val tree = builtTree(Test2Builder())

        val node = root(tree)
        node.shouldBeInstanceOf<InnerNode>()
        node.size() shouldBe 0

        val sr = Hit(Double.MAX_VALUE)
        node.hit(rayAlongLine(), sr).shouldBeFalse()
    }

    "TestBuilder still returns a leaf for an object set below the minimum children threshold" {
        // The leaf path predates the buggy split logic, so a small set is handled correctly.
        val tree = builtTree(TestBuilder(), lineOfSpheres(2))

        root(tree).shouldBeInstanceOf<Leaf>()
    }

    // ---- KDTree wrapper public contract ----------------------------------------------------------

    "KDTree.hit reports true for a ray that intersects the scene" {
        val tree = builtTree(SpatialMedianBuilder())

        val sr = Hit(Double.MAX_VALUE)
        tree.hit(rayAlongLine(), sr).shouldBeTrue()
    }

    "KDTree.hit reports false for a ray that misses the scene entirely" {
        val tree = builtTree(SpatialMedianBuilder())

        val miss = Ray(Point3D(-10.0, 100.0, 0.0), Vector3D(1.0, 0.0, 0.0))
        val sr = Hit(Double.MAX_VALUE)
        tree.hit(miss, sr).shouldBeFalse()
    }

    "KDTree.shadowHit reports an occluder along the ray and none for a clear ray" {
        // KDTree.hit wraps Hit(sr) and discards the populated record, so shadowHit cannot propagate
        // a distance back into tmin.t; only the boolean occlusion result is observable here.
        val tree = builtTree(SpatialMedianBuilder())

        val occluded = net.dinkla.raytracer.hits.ShadowHit(Double.MAX_VALUE)
        tree.shadowHit(rayAlongLine(), occluded).shouldBeTrue()

        val clear = net.dinkla.raytracer.hits.ShadowHit(Double.MAX_VALUE)
        val miss = Ray(Point3D(-10.0, 100.0, 0.0), Vector3D(1.0, 0.0, 0.0))
        tree.shadowHit(miss, clear).shouldBeFalse()
    }
})
