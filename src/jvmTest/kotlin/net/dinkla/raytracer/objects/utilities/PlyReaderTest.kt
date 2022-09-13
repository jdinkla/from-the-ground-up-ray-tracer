package net.dinkla.raytracer.objects.utilities

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import net.dinkla.raytracer.materials.Matte
import net.dinkla.raytracer.objects.mesh.FlatMeshTriangle
import net.dinkla.raytracer.objects.mesh.SmoothMeshTriangle

class PlyReaderTest : AnnotationSpec() {

    val PLY_EXAMPLE = "resources/TwoTriangles.ply"

    @Test
    fun `should detect element vertex spec`() {
        PlyReader.isElementVertex("element vertex 4") shouldBe true
        PlyReader.isElementVertex("element   vertex 4") shouldBe true
        PlyReader.isElementVertex("element    vertex 4") shouldBe true
        PlyReader.isElementVertex("element vertex    4    ") shouldBe true
    }

    @Test
    fun `should parse number of element vertices`() {
        PlyReader.parseNumVertices("element vertex 4") shouldBe 4
        PlyReader.parseNumVertices("element  vertex 4") shouldBe 4
        PlyReader.parseNumVertices("element vertex 4   ") shouldBe 4
        PlyReader.parseNumVertices("element vertex    4    ") shouldBe 4
    }

    @Test
    fun `should detect element face spec`() {
        PlyReader.isElementFace("element face 4") shouldBe true
        PlyReader.isElementFace("element   face 4") shouldBe true
        PlyReader.isElementFace("element    face 4") shouldBe true
        PlyReader.isElementFace("element face    4    ") shouldBe true
    }

    @Test
    fun `should parse number of element faces`() {
        PlyReader.parseNumFaces("element face 4") shouldBe 4
        PlyReader.parseNumFaces("element  face 4") shouldBe 4
        PlyReader.parseNumFaces("element face 4   ") shouldBe 4
        PlyReader.parseNumFaces("element face    4    ") shouldBe 4
    }


    @Test
    fun readFlat() {
        // given
        val material = Matte()

        // when
        val ply = Ply.fromFile(PLY_EXAMPLE, material = material)
        val grid = ply.compound

        // then
        ply.numVertices shouldBe 4
        ply.numFaces shouldBe 2
        grid.mesh.vertices.size shouldBe 4
        grid.size() shouldBe 2
        grid.material shouldBe material
        grid.objects.size shouldBe 2
        grid.objects[0].shouldBeInstanceOf<FlatMeshTriangle>()
        grid.objects[1].shouldBeInstanceOf<FlatMeshTriangle>()
        grid.objects[0].material shouldBe material
        grid.objects[1].material shouldBe material
    }

    @Test
    fun readSmooth() {
        // given
        val material = Matte()

        // when
        val ply = Ply.fromFile(PLY_EXAMPLE, material = material, isSmooth = true)
        val grid = ply.compound

        // then
        ply.numVertices shouldBe 4
        ply.numFaces shouldBe 2
        grid.mesh.vertices.size shouldBe 4
        grid.size() shouldBe 2
        grid.material shouldBe material
        grid.objects.size shouldBe 2
        grid.objects[0].shouldBeInstanceOf<SmoothMeshTriangle>()
        grid.objects[1].shouldBeInstanceOf<SmoothMeshTriangle>()
        grid.objects[0].material shouldBe material
        grid.objects[1].material shouldBe material
    }
}
