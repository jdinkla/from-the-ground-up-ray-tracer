package net.dinkla.raytracer.world.dsl

import net.dinkla.raytracer.Fixture.ex
import net.dinkla.raytracer.materials.Matte
import net.dinkla.raytracer.materials.Phong
import net.dinkla.raytracer.materials.Reflective
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MaterialsScopeTest {

    private val id = "m1"

    @Test
    fun `should handle matte`() {
        // given
        val scope = MaterialsScope()
        val matte = Matte(ex.cd)

        // when
        scope.matte(id = id, cd = ex.cd)

        // then
        assertEquals(1, scope.materials.size)
        assertTrue(scope.materials.containsKey(id))
        val material = scope.materials[id] as Matte
        assertEquals(matte, material)
    }

    @Test
    fun `should handle phong`() {
        // given
        val phong = Phong(ex.cd, ex.ka, ex.kd).apply {
            exp = ex.exp
            cs = ex.cs
            ks = ex.ks
        }

        println("phong $phong")
        val scope = MaterialsScope()

        // when
        scope.phong(id = id, cd = ex.cd, ka = ex.ka, kd = ex.kd, exp = ex.exp, cs = ex.cs, ks = ex.ks)

        // then
        assertEquals(1, scope.materials.size)
        assertTrue(scope.materials.containsKey(id))
        val material = scope.materials[id] as Phong
        assertEquals(phong, material)
    }

    @Test
    fun `should handle reflective`() {
        // given
        val id = "m1"
        val scope = MaterialsScope()
        val reflective = Reflective(ex.cd, ex.ka, ex.kd).apply {
            exp = ex.exp
            cs = ex.cs
            ks = ex.ks
            cr = ex.cr
            kr = ex.kr
        }

        // when
        scope.reflective(id = id, cd = ex.cd, ka = ex.ka, kd = ex.kd,
                cr = ex.cr, kr = ex.kr,
                ks = ex.ks, cs = ex.cs, exp = ex.exp)

        // then
        assertEquals(1, scope.materials.size)
        assertTrue(scope.materials.containsKey(id))
        val material = scope.materials[id] as Reflective
        assertEquals(reflective.kr, material.kr)
        assertEquals(reflective.cr, material.cr)
        assertEquals(reflective.ka, material.ka)
        assertEquals(reflective.kd, material.kd)
        assertEquals(reflective.exp, material.exp)
        assertEquals(reflective, material)
    }

}