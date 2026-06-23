package net.dinkla.raytracer.audit

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import net.dinkla.raytracer.cameras.Camera
import net.dinkla.raytracer.cameras.StereoCamera
import net.dinkla.raytracer.cameras.lenses.Pinhole
import net.dinkla.raytracer.cameras.lenses.ThinLens
import net.dinkla.raytracer.lights.AmbientOccluder
import net.dinkla.raytracer.lights.PointLight
import net.dinkla.raytracer.materials.Dielectric
import net.dinkla.raytracer.materials.Matte
import net.dinkla.raytracer.objects.NullObject
import net.dinkla.raytracer.objects.Sphere
import net.dinkla.raytracer.objects.acceleration.Grid
import net.dinkla.raytracer.objects.acceleration.SparseGrid
import net.dinkla.raytracer.objects.acceleration.kdtree.KDTree
import net.dinkla.raytracer.objects.beveled.BeveledBox
import net.dinkla.raytracer.objects.compound.Compound
import net.dinkla.raytracer.textures.Checker3D
import net.dinkla.raytracer.world.World

class ClassCatalogTest : StringSpec({
    val catalog = ClassCatalog.scan()

    "the geometry denominator includes concrete primitives and composites" {
        val geometry = catalog.denominator(Category.GEOMETRY)
        geometry shouldContain Sphere::class.java.name
        geometry shouldContain BeveledBox::class.java.name
    }

    "infrastructure classes are excluded from the geometry denominator" {
        val geometry = catalog.denominator(Category.GEOMETRY)
        geometry shouldNotContain Compound::class.java.name // blocklisted root container
        geometry shouldNotContain NullObject::class.java.name // blocklisted empty-cell sentinel
        geometry shouldNotContain Grid::class.java.name // belongs to the acceleration category
    }

    "acceleration structures form their own category" {
        val acceleration = catalog.denominator(Category.ACCELERATION)
        acceleration shouldContain Grid::class.java.name
        acceleration shouldContain SparseGrid::class.java.name
        acceleration shouldContain KDTree::class.java.name
    }

    "materials, lights, lenses, cameras and textures are catalogued" {
        catalog.denominator(Category.MATERIALS) shouldContain Matte::class.java.name
        catalog.denominator(Category.MATERIALS) shouldContain Dielectric::class.java.name
        catalog.denominator(Category.LIGHTS) shouldContain PointLight::class.java.name
        catalog.denominator(Category.LIGHTS) shouldContain AmbientOccluder::class.java.name
        catalog.denominator(Category.LENSES) shouldContain Pinhole::class.java.name
        catalog.denominator(Category.LENSES) shouldContain ThinLens::class.java.name
        catalog.denominator(Category.CAMERAS) shouldContain Camera::class.java.name
        catalog.denominator(Category.CAMERAS) shouldContain StereoCamera::class.java.name
        catalog.denominator(Category.TEXTURES) shouldContain Checker3D::class.java.name
    }

    "no example scene class leaks into the denominator" {
        catalog.allClasses().filter { it.contains(".examples.") }.shouldBeEmpty()
        // World is core infrastructure, not a tracked, instantiable feature class.
        catalog.allClasses() shouldNotContain World::class.java.name
    }
})
