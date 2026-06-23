package net.dinkla.raytracer.audit

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.maps.shouldContainKey
import net.dinkla.raytracer.cameras.Camera
import net.dinkla.raytracer.cameras.lenses.Pinhole
import net.dinkla.raytracer.lights.Ambient
import net.dinkla.raytracer.lights.PointLight
import net.dinkla.raytracer.materials.Matte
import net.dinkla.raytracer.materials.SvMatte
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.objects.AlignedBox
import net.dinkla.raytracer.objects.Instance
import net.dinkla.raytracer.objects.Plane
import net.dinkla.raytracer.objects.Sphere
import net.dinkla.raytracer.objects.acceleration.Grid
import net.dinkla.raytracer.objects.arealights.DiskLight
import net.dinkla.raytracer.objects.beveled.BeveledBox
import net.dinkla.raytracer.samplers.PureRandom
import net.dinkla.raytracer.samplers.Sampler
import net.dinkla.raytracer.textures.Checker3D
import net.dinkla.raytracer.world.Builder

/**
 * Integration-level: exercises the inspector against a world assembled through the real scene DSL, so
 * it must use [Builder.build] (per specs/testing.md §8). The fixture is laid out so the leaf primitives
 * are reachable *only* through containers — `Sphere` only inside the `grid`, `AlignedBox` only inside
 * the `instance` — so finding them proves the tree walk recurses, not that they were named at the top.
 */
class SceneInspectorTest : StringSpec({
    val areaSource =
        DiskLight(Sampler(PureRandom, 1, 1), Point3D(0.0, 8.0, 0.0), 1.0, Normal.DOWN)
            .apply { material = Matte() }
    val world =
        Builder.build {
            camera(d = 1.0, eye = p(0, 0, 5), lookAt = p(0, 0, 0))
            ambientLight(ls = 0.5)
            lights {
                pointLight(location = p(0, 5, 5), ls = 1.0)
                areaLight(of = areaSource, numSamples = 1)
            }
            materials {
                matte(id = "m", cd = c(1.0), ka = 0.5, kd = 0.5)
                svMatte(id = "tex", texture = Checker3D(), ka = 0.5, kd = 0.5)
            }
            objects {
                plane(material = "m", point = p(0, 0, 0), normal = Normal.UP)
                beveledBox(material = "m", p0 = p(0, 0, 0), p1 = p(1, 1, 1), rb = 0.1)
                grid {
                    sphere(material = "tex", center = p(2, 0, 0), radius = 0.5)
                }
                instance(material = "m", of = AlignedBox(p(-2, 0, 0), p(-1, 1, 1))) { }
            }
        }
    val used = SceneInspector.inspect(world)

    "counts a directly declared leaf primitive as geometry" {
        used.getValue(Category.GEOMETRY) shouldContain Plane::class.java.name
    }

    "counts a composite (beveled box) and an instance node as geometry" {
        used.getValue(Category.GEOMETRY) shouldContain BeveledBox::class.java.name
        used.getValue(Category.GEOMETRY) shouldContain Instance::class.java.name
    }

    "recurses into a grid to reach the primitive inside it" {
        used.getValue(Category.GEOMETRY) shouldContain Sphere::class.java.name
    }

    "recurses into an instance to reach its wrapped primitive" {
        used.getValue(Category.GEOMETRY) shouldContain AlignedBox::class.java.name
    }

    "classifies a grid as an acceleration structure, not plain geometry" {
        used.getValue(Category.ACCELERATION) shouldContain Grid::class.java.name
    }

    "credits the geometric shape of an area light, which lives outside the object tree" {
        used.getValue(Category.GEOMETRY) shouldContain DiskLight::class.java.name
    }

    "collects every declared material" {
        used.getValue(Category.MATERIALS) shouldContain Matte::class.java.name
        used.getValue(Category.MATERIALS) shouldContain SvMatte::class.java.name
    }

    "finds the texture carried by a spatially-varying material" {
        used.getValue(Category.TEXTURES) shouldContain Checker3D::class.java.name
    }

    "collects the scene lights including the ambient term" {
        used.getValue(Category.LIGHTS) shouldContain PointLight::class.java.name
        used.getValue(Category.LIGHTS) shouldContain Ambient::class.java.name
    }

    "records the camera and its lens" {
        used.getValue(Category.CAMERAS) shouldContain Camera::class.java.name
        used.getValue(Category.LENSES) shouldContain Pinhole::class.java.name
    }

    "reports a set for every category" {
        Category.entries.forEach { used shouldContainKey it }
    }
})
