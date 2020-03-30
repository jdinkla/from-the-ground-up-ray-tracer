package net.dinkla.raytracer.objects.utilities

import net.dinkla.raytracer.PLY_BINARY_EXAMPLE
import net.dinkla.raytracer.PLY_EXAMPLE
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

@Disabled
class PlyFileTest {

    @Test
    fun readHeaderAscii() {
        val plyFile = PlyFile(PLY_EXAMPLE)
        assertEquals(plyFile.numVertices, 4)
        assertEquals(plyFile.numFaces, 2)
        assertEquals(plyFile.vertexProperties.size, 3)
        assertEquals(plyFile.facesProperties.size, 1)
        assertEquals(plyFile.format, "ascii")
        assertEquals(plyFile.formatVersion, "1.0")
        assertEquals(plyFile.vertexDataLength, 12)
    }

    @Test
    fun readHeaderBinary() {
        val plyFile = PlyFile(PLY_BINARY_EXAMPLE)
        assertEquals(plyFile.numVertices, 46912)
        assertEquals(plyFile.numFaces, 93820)
        assertEquals(plyFile.vertexProperties.size, 4)
        assertEquals(plyFile.facesProperties.size, 1)
        assertEquals(plyFile.format, "binary_big_endian")
        assertEquals(plyFile.formatVersion, "1.0")
        assertEquals(plyFile.vertexDataLength, 16)
    }
}
