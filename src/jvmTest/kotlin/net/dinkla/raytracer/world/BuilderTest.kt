package net.dinkla.raytracer.world

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.examples.*
import net.dinkla.raytracer.examples.reflective.World17
import net.dinkla.raytracer.lights.AmbientOccluder
import net.dinkla.raytracer.lights.PointLight
import net.dinkla.raytracer.materials.Matte
import net.dinkla.raytracer.math.Basis
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.objects.Sphere
import net.dinkla.raytracer.tracers.AreaLighting

class BuilderTest : AnnotationSpec() {

    @Test
    fun `should store name of world`() {
        val id = "idOfWorld"
        val world = Builder.build(id) {}
        world.id shouldBe id
    }

    @Test
    fun `should set camera`() {
        val d = 500.0
        val eye = Point3D(0.0, 100.0, 200.0)
        val lookAt = Point3D(1.0, 2.0, 3.0)
        val up = Vector3D.JITTER
        val world = Builder.build("id") {
            camera(d = d, eye = p(0, 100, 200), lookAt = p(1, 2, 3), up = up)
        }
        world.camera shouldNotBe null
        world.camera?.uvw shouldBe Basis(eye, lookAt, up)
    }

    @Test
    fun `should set ambient light`() {
        val ls = 123.45
        val color = Color.BLUE
        val world = Builder.build("id") {
            ambientLight(color = color, ls = ls)
        }
        world.ambientLight shouldNotBe null
        world.ambientLight.color shouldBe color
        world.ambientLight.ls shouldBe ls
    }

    @Test
    fun `should add point light`() {
        val ls = 0.98
        val color = Color.BLUE
        val location = Point3D(0.0, 100.0, 200.0)
        val world = Builder.build("id") {
            lights {
                pointLight(location = location, ls = ls, color = color)
            }
        }
        world.lights shouldNotBe null
        world.lights.shouldContainExactly(PointLight(location, ls, color))
    }

    @Test
    fun `should store matte materials`() {
        val id = "m1"
        val cd = Color(1.0, 0.5, 0.3)
        val world = Builder.build("id") {
            materials {
                matte(id = id, cd = cd)
            }
        }
        world.materials.size shouldBe 1
        world.materials.containsKey(id) shouldBe true
        world.materials[id] shouldBe Matte(cd)
    }

    @Test
    fun `should store sphere objects using materials`() {
        val id = "m1"
        val cd = Color(1.0, 0.5, 0.3)
        val center = Point3D(0.0, 100.0, 200.0)
        val radius = 80.0
        val world = Builder.build("id") {
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

    @Test
    fun `should build example world 20`() {
        val w = World20.world()
        w.size() shouldBe 4
        w.lights.size shouldBe 1
        w.objects.size shouldBe 4
    }

    @Test
    fun `should build example world 7`() {
        val w = World7.world()
        w.size() shouldBe 6
        w.lights.size shouldBe 3
        w.objects.size shouldBe 6
    }

    @Test
    fun `should build example world 14 - ambient occluder`() {
        val w = World14.world()
        w.size() shouldBe 2
        w.lights.size shouldBe 0
        w.ambientLight.shouldBeInstanceOf<AmbientOccluder>()
    }

    @Test
    fun `should build example world 17`() {
        val w = World17.world()
        w.size() shouldBe 10
        w.lights.size shouldBe 2
    }

    @Test
    fun `should build example world 23 - area lighting`() {
        val w = World23.world()
        w.size() shouldBe 3
        w.lights.size shouldBe 1
        w.tracer.shouldBeInstanceOf<AreaLighting>()
    }

    @Test
    fun `should build example world 26 - instancing`() {
        val w = World26.world()
        w.size() shouldBe 3
        w.lights.size shouldBe 1
        w.objects.size shouldBe 3
    }

    @Test
    fun `should build example world 34 - transparent`() {
        val w = World34.world()
        w.viewPlane shouldNotBe null
        w.camera shouldNotBe null
        w.tracer shouldNotBe null
        w.size() shouldBe 6
        w.lights.size shouldBe 1
    }

    @Test
    fun `should build example world 38 - Grid`() {
        val w = World38.world()
        w.size() shouldBe 6
    }
}