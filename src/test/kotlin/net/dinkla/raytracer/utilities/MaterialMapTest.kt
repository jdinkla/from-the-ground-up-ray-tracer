package net.dinkla.raytracer.utilities

import net.dinkla.raytracer.materials.Matte
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class MaterialMapTest {

    val matte = Matte()

    private var obj = mapOf("material" to "name")

    @Test
    fun `getting an unknown element yields an exception`() {
        val map = MaterialMap()
        assertThrows<RuntimeException> {
            map.get(obj, "name")
        }
    }

    @Test
    fun `geting a previously inserted element`() {
        val map = MaterialMap()
        map.insert(mapOf("id" to "name"),  matte)
        assertEquals(map.get(obj, "name"), matte)
    }

    @Test
    fun `inserting an unknown element`() {
        val map = MaterialMap()
        map.insert(mapOf("id" to "name"),  matte)
        assertEquals(1, 1) // TODO assert true?
    }

    @Test
    fun `inserting a known element yields an exception`() {
        val map = MaterialMap()
        map.insert(mapOf("id" to "name"),  matte)
        assertThrows<RuntimeException> {
            map.insert(mapOf("id" to "name"),  matte)
        }
    }

}