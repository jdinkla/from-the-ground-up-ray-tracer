package net.dinkla.raytracer.utilities

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import net.dinkla.raytracer.materials.Matte
import net.dinkla.raytracer.objects.acceleration.Acceleration
import net.dinkla.raytracer.objects.mesh.FlatMeshTriangle
import net.dinkla.raytracer.objects.mesh.SmoothMeshTriangle

const val PLY_EXAMPLE = "resources/TwoTriangles.ply"

class PlyReaderTest :
    StringSpec({
        "should detect element vertex spec" {
            isElementVertex("element vertex 4") shouldBe true
            isElementVertex("element   vertex 4") shouldBe true
            isElementVertex("element    vertex 4") shouldBe true
            isElementVertex("element vertex    4    ") shouldBe true
        }

        "should parse number of element vertices" {
            parseNumVertices("element vertex 4") shouldBe 4
            parseNumVertices("element  vertex 4") shouldBe 4
            parseNumVertices("element vertex 4   ") shouldBe 4
            parseNumVertices("element vertex    4    ") shouldBe 4
        }

        "should detect element face spec" {
            isElementFace("element face 4") shouldBe true
            isElementFace("element   face 4") shouldBe true
            isElementFace("element    face 4") shouldBe true
            isElementFace("element face    4    ") shouldBe true
        }

        "should parse number of element faces" {
            parseNumFaces("element face 4") shouldBe 4
            parseNumFaces("element  face 4") shouldBe 4
            parseNumFaces("element face 4   ") shouldBe 4
            parseNumFaces("element face    4    ") shouldBe 4
        }

        "read flat" {
            // given
            val material = Matte()

            // when
            val ply = Ply.fromFile(PLY_EXAMPLE, material = material)

            // then
            ply.numVertices shouldBe 4
            ply.numFaces shouldBe 2
            val grid = ply.compound
            grid.mesh.vertices.size shouldBe 4
            grid.size() shouldBe 2
            grid.material shouldBe material
            grid.objects.size shouldBe 2
            grid.objects[0].shouldBeInstanceOf<FlatMeshTriangle>()
            grid.objects[1].shouldBeInstanceOf<FlatMeshTriangle>()
            grid.objects[0].material shouldBe material
            grid.objects[1].material shouldBe material
        }

        "read smooth" {
            // given
            val material = Matte()

            // when
            val ply = Ply.fromFile(PLY_EXAMPLE, material = material, isSmooth = true)

            // then
            ply.numVertices shouldBe 4
            ply.numFaces shouldBe 2
            val grid = ply.compound
            grid.mesh.vertices.size shouldBe 4
            grid.size() shouldBe 2
            grid.material shouldBe material
            grid.objects.size shouldBe 2
            grid.objects[0].shouldBeInstanceOf<SmoothMeshTriangle>()
            grid.objects[1].shouldBeInstanceOf<SmoothMeshTriangle>()
            grid.objects[0].material shouldBe material
            grid.objects[1].material shouldBe material
        }

        "rejects an unknown body line with IllegalArgumentException" {
            // given a reader past the header (no vertices/faces declared) with a junk body line
            val reader = PlyReader(Matte(), compound = Acceleration.GRID.build())
            val lines = listOf("end_header", "garbage line that is not whitespace")

            // when / then
            val ex = shouldThrow<IllegalArgumentException> { reader.read(lines) }
            ex.message shouldContain "Unknown file format"
        }

        "rejects a face line with too few elements with IllegalArgumentException" {
            // given a reader expecting one face but given a face line that is too short
            val reader = PlyReader(Matte(), compound = Acceleration.GRID.build())
            val lines = listOf("element face 1", "end_header", "3 0 1")

            // when / then
            val ex = shouldThrow<IllegalArgumentException> { reader.read(lines) }
            ex.message shouldContain "Not enough elements"
        }
    })
