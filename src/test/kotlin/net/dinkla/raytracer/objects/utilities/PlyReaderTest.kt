package net.dinkla.raytracer.objects.utilities

import net.dinkla.raytracer.TestUtils
import net.dinkla.raytracer.assertType
import net.dinkla.raytracer.materials.Matte
import net.dinkla.raytracer.objects.GeometricObject
import net.dinkla.raytracer.objects.acceleration.Grid
import net.dinkla.raytracer.objects.mesh.FlatMeshTriangle
import net.dinkla.raytracer.objects.mesh.Mesh
import net.dinkla.raytracer.objects.mesh.MeshTriangle
import net.dinkla.raytracer.objects.mesh.SmoothMeshTriangle
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled

class PlyReaderTest {

    @Test
    fun `should detect element vertex spec`() {
        assertTrue(PlyReader.isElementVertex("element vertex 4"))
        assertTrue(PlyReader.isElementVertex("element   vertex 4"))
        assertTrue(PlyReader.isElementVertex("element    vertex 4"))
        assertTrue(PlyReader.isElementVertex("element vertex    4    "))
    }

    @Test
    fun `should parse number of element vertices`() {
        assertEquals(4, PlyReader.parseNumVertices("element vertex 4"))
        assertEquals(4, PlyReader.parseNumVertices("element  vertex 4"))
        assertEquals(4, PlyReader.parseNumVertices("element vertex 4   "))
        assertEquals(4, PlyReader.parseNumVertices("element vertex    4    "))
    }

    @Test
    fun `should detect element face spec`() {
        assertTrue(PlyReader.isElementFace("element face 4"))
        assertTrue(PlyReader.isElementFace("element   face 4"))
        assertTrue(PlyReader.isElementFace("element    face 4"))
        assertTrue(PlyReader.isElementFace("element face    4    "))
    }

    @Test
    fun `should parse number of element faces`() {
        assertEquals(4, PlyReader.parseNumFaces("element face 4"))
        assertEquals(4, PlyReader.parseNumFaces("element  face 4"))
        assertEquals(4, PlyReader.parseNumFaces("element face 4   "))
        assertEquals(4, PlyReader.parseNumFaces("element face    4    "))
    }


    @Test
    fun readFlat() {
        // given
        val material = Matte()

        // when
        val plyReader = PlyReader(material)
        val ply = plyReader.read(TestUtils.PLY_EXAMPLE)
        val grid = ply.grid

        // then
        assertEquals(4, ply.numVertices)
        assertEquals(2, ply.numFaces)
        assertEquals(grid.mesh.vertices.size, 4)
        assertEquals(grid.size(), 2)
        assertEquals(material, grid.material)
        assertType<GeometricObject, FlatMeshTriangle>(grid.objects, 0)
        assertType<GeometricObject, FlatMeshTriangle>(grid.objects, 1)
        assertEquals(material, grid.objects[0].material)
        assertEquals(material, grid.objects[1].material)
    }

    @Test
    fun readSmooth() {
        // given
        val material = Matte()

        // when
        val plyReader = PlyReader(material, isSmooth = true)
        val ply = plyReader.read(TestUtils.PLY_EXAMPLE)
        val grid = ply.grid

        // then
        assertEquals(4, ply.numVertices)
        assertEquals(2, ply.numFaces)
        assertEquals(grid.mesh.vertices.size, 4)
        assertEquals(grid.size(), 2)
        assertEquals(material, grid.material)
        assertType<GeometricObject, SmoothMeshTriangle>(grid.objects, 0)
        assertType<GeometricObject, SmoothMeshTriangle>(grid.objects, 1)
        assertEquals(material, grid.objects[0].material)
        assertEquals(material, grid.objects[1].material)
    }

}
