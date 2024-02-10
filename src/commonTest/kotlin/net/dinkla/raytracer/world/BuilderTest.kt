package net.dinkla.raytracer.world

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.examples.AmbientOccludedSphere
import net.dinkla.raytracer.examples.YellowAndRedSphere
import net.dinkla.raytracer.examples.World23
import net.dinkla.raytracer.examples.InstanceExample
import net.dinkla.raytracer.examples.TransparentSpheres
import net.dinkla.raytracer.examples.acceleration.SpheresInNestedGrids
import net.dinkla.raytracer.examples.World7
import net.dinkla.raytracer.examples.materials.reflective.World17
import net.dinkla.raytracer.lights.AmbientOccluder
import net.dinkla.raytracer.lights.PointLight
import net.dinkla.raytracer.materials.Matte
import net.dinkla.raytracer.math.Basis
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.objects.Sphere

class BuilderTest : StringSpec({
    "should set camera" {
        val d = 500.0
        val eye = Point3D(0.0, 100.0, 200.0)
        val lookAt = Point3D(1.0, 2.0, 3.0)
        val up = Vector3D.JITTER
        val world = Builder.build {
            camera(d = d, eye = p(0, 100, 200), lookAt = p(1, 2, 3), up = up)
        }
        world.camera shouldNotBe null
        world.camera.uvw shouldBe Basis.create(eye, lookAt, up)
    }

    "should set ambient light" {
        val ls = 123.45
        val color = Color.BLUE
        val world = Builder.build {
            ambientLight(color = color, ls = ls)
        }
        world.ambientLight shouldNotBe null
        world.ambientLight.color shouldBe color
        world.ambientLight.ls shouldBe ls
    }

    "should add point light" {
        val ls = 0.98
        val color = Color.BLUE
        val location = Point3D(0.0, 100.0, 200.0)
        val world = Builder.build {
            lights {
                pointLight(location = location, ls = ls, color = color)
            }
        }
        world.lights shouldNotBe null
        world.lights.shouldContainExactly(PointLight(location, ls, color))
    }

    "should store matte materials" {
        val id = "m1"
        val cd = Color(1.0, 0.5, 0.3)
        val world = Builder.build {
            materials {
                matte(id = id, cd = cd)
            }
        }
        world.materials.size shouldBe 1
        world.materials.containsKey(id) shouldBe true
        world.materials[id] shouldBe Matte(cd)
    }

    "should store sphere objects using materials" {
        val id = "m1"
        val cd = Color(1.0, 0.5, 0.3)
        val center = Point3D(0.0, 100.0, 200.0)
        val radius = 80.0
        val world = Builder.build {
            materials {
                matte(id = id, cd = cd)
            }
            objects {
                sphere(material = id, center = center, radius = radius)
            }
        }
        world.materials.size shouldBe 1
        world.materials.containsKey(id) shouldBe true
        world.materials[id] shouldBe Matte(cd)
        world.objects.shouldContainExactly(Sphere(center, radius, Matte(cd)))
        world.compound.size() shouldBe 1
    }

    "should build example world 20" {
        val w = YellowAndRedSphere.world()
        w.size() shouldBe 4
        w.lights.size shouldBe 1
        w.objects.size shouldBe 4
    }

    "should build example world 7" {
        val w = World7.world()
        w.size() shouldBe 6
        w.lights.size shouldBe 3
        w.objects.size shouldBe 6
    }

    "should build example world 14 - ambient occluder" {
        val w = AmbientOccludedSphere.world()
        w.size() shouldBe 2
        w.lights.size shouldBe 0
        w.ambientLight.shouldBeInstanceOf<AmbientOccluder>()
    }

    "should build example world 17" {
        val w = World17.world()
        w.size() shouldBe 10
        w.lights.size shouldBe 2
    }

    "should build example world 23 - area lighting" {
        val w = World23.world()
        w.size() shouldBe 3
        w.lights.size shouldBe 1
    }

    "should build example world 26 - instancing" {
        val w = InstanceExample.world()
        w.size() shouldBe 3
        w.lights.size shouldBe 1
        w.objects.size shouldBe 3
    }

    "should build example world 34 - transparent" {
        val w = TransparentSpheres.world()
        w.viewPlane shouldNotBe null
        w.camera shouldNotBe null
        w.size() shouldBe 5
        w.lights.size shouldBe 1
    }

    "should build example world 38 - Grid" {
        val w = SpheresInNestedGrids.world()
        w.size() shouldBe 6
    }
})
