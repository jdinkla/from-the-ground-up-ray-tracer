package net.dinkla.raytracer.world.dsl

import net.dinkla.raytracer.Fixture.ex
import net.dinkla.raytracer.materials.*
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

    @Test
    fun `should handle emissive`() {
        // given
        val scope = MaterialsScope()
        val emissive = Emissive(ex.cd, ex.ks)

        // when
        scope.emissive(id = id, ce = ex.cd, le = ex.ks)

        // then
        assertEquals(1, scope.materials.size)
        assertTrue(scope.materials.containsKey(id))
        val material = scope.materials[id] as Emissive
        assertEquals(emissive, material)
    }

    @Test
    fun `should handle transparent`() {
        // given
        val id = "m1"
        val scope = MaterialsScope()
        val transparent = Transparent().apply {
            cd = ex.cd
            ka = ex.ka
            kd = ex.kd
            exp = ex.exp
            ks = ex.ks
            cs = ex.cs
            kt = ex.kt
            ior = ex.ior
            cr = ex.cr
            kr = ex.kr
        }

        // when
        scope.transparent(id = id, cd = ex.cd, ka = ex.ka, kd = ex.kd,
                cr = ex.cr, kr = ex.kr,
                ks = ex.ks, cs = ex.cs, exp = ex.exp,
                kt = ex.kt, ior = ex.ior)

        // then
        assertEquals(1, scope.materials.size)
        assertTrue(scope.materials.containsKey(id))
        val material = scope.materials[id] as Transparent
        assertEquals(transparent.kr, material.kr)
        assertEquals(transparent.cr, material.cr)
        assertEquals(transparent.ka, material.ka)
        assertEquals(transparent.kd, material.kd)
        assertEquals(transparent.exp, material.exp)
        assertEquals(transparent.kt, material.kt)
        assertEquals(transparent.ior, material.ior)
        assertEquals(transparent.cr, material.cr)
        assertEquals(transparent.kr, material.kr)
        assertEquals(transparent, material)
    }

}