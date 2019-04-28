package net.dinkla.raytracer.materials

import net.dinkla.raytracer.Fixture.ex
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

internal class MatteTest {

    @Test
    fun `should detect equality`() {
        val matte1 = Matte(ex.cd, ex.ka, ex.kd)
        val matte2 = Matte(ex.cd, ex.ka, ex.kd)
        assertEquals(matte1, matte2)
    }

    @Test
    fun `should detect inequality in color`() {
        val matte1 = Matte(ex.cd, ex.ka, ex.kd)
        val matte2 = Matte(ex.cr, ex.ka, ex.kd)
        assertNotEquals(matte1, matte2)
    }

    @Test
    fun `should detect inequality in ka`() {
        val matte1 = Matte(ex.cd, ex.ka, ex.kd)
        val matte2 = Matte(ex.cd, ex.ka + 0.1, ex.kd)
        assertNotEquals(matte1, matte2)
    }

    @Test
    fun `should detect inequality in kd`() {
        val matte1 = Matte(ex.cd, ex.ka, ex.kd)
        val matte2 = Matte(ex.cd, ex.ka, ex.kd + 0.1)
        assertNotEquals(matte1, matte2)
    }
}