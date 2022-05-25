package net.dinkla.raytracer.materials

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import net.dinkla.raytracer.Fixture.ex

internal class MatteTest : AnnotationSpec() {

    @Test
    fun `should detect equality`() {
        val matte1 = Matte(ex.cd, ex.ka, ex.kd)
        val matte2 = Matte(ex.cd, ex.ka, ex.kd)
        matte1 shouldBe matte2
    }

    @Test
    fun `should detect inequality in color`() {
        val matte1 = Matte(ex.cd, ex.ka, ex.kd)
        val matte2 = Matte(ex.cr, ex.ka, ex.kd)
        matte1 shouldNotBe matte2
    }

    @Test
    fun `should detect inequality in ka`() {
        val matte1 = Matte(ex.cd, ex.ka, ex.kd)
        val matte2 = Matte(ex.cd, ex.ka + 0.1, ex.kd)
        matte1 shouldNotBe matte2
    }

    @Test
    fun `should detect inequality in kd`() {
        val matte1 = Matte(ex.cd, ex.ka, ex.kd)
        val matte2 = Matte(ex.cd, ex.ka, ex.kd + 0.1)
        matte1 shouldNotBe matte2
    }
}