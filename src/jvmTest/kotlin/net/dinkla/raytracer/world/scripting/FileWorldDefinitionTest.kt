package net.dinkla.raytracer.world.scripting

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.lights.PointLight
import net.dinkla.raytracer.materials.Matte
import net.dinkla.raytracer.materials.Phong
import net.dinkla.raytracer.objects.Plane
import net.dinkla.raytracer.objects.Sphere
import io.kotest.core.spec.style.StringSpec
import java.io.File

// Loaded by relative path from the repo root (the test working directory), the same convention
// PlyReaderTest uses for `resources/TwoTriangles.ply`.
private const val SAMPLE_SCENE = "scenes/Sample.scene.kts"

class FileWorldDefinitionTest :
    StringSpec({
        "id is the scene file name" {
            FileWorldDefinition(SAMPLE_SCENE).id shouldBe "Sample.scene.kts"
        }

        "world() evaluates the external DSL file into the declared World" {
            val world = FileWorldDefinition(SAMPLE_SCENE).world()

            world.objects shouldHaveSize 4
            world.objects.count { it is Sphere } shouldBe 2
            world.objects.count { it is Plane } shouldBe 2

            world.materials.keys shouldBe setOf("m1", "m2", "m3")
            world.materials["m1"].shouldBeInstanceOf<Matte>()
            world.materials["m3"].shouldBeInstanceOf<Phong>()

            world.lights shouldHaveSize 1
            world.lights.first().shouldBeInstanceOf<PointLight>()

            world.ambientLight.color shouldBe Color.WHITE
            world.ambientLight.ls shouldBe 0.25
        }

        "world() builds a fresh World on each call (no shared mutable state across evaluations)" {
            val def = FileWorldDefinition(SAMPLE_SCENE)

            val first = def.world()
            val second = def.world()

            (first === second) shouldBe false
            first.objects shouldHaveSize 4
            second.objects shouldHaveSize 4
        }

        "a scene file with a compile error fails with a SceneScriptException naming the file and the error" {
            val broken = File.createTempFile("Broken", ".scene.kts")
            broken.writeText(
                """
                camera(d = 1500.0, eye = p(2.0, 0.5, 5.0), lookAt = p(1.5, 1.0, 0.0))
                thisFunctionDoesNotExist(42)
                """.trimIndent(),
            )

            try {
                val ex =
                    shouldThrow<SceneScriptException> {
                        FileWorldDefinition(broken).world()
                    }

                ex.message shouldContain broken.name
                ex.message shouldContain "thisFunctionDoesNotExist"
                ex.file shouldBe broken
            } finally {
                broken.delete()
            }
        }
    })
