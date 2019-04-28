package net.dinkla.raytracer.world.dsl

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.materials.Matte
import net.dinkla.raytracer.materials.Reflective
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MaterialsScopeTest {

    @Test
    fun `should handle matte`() {
        // given
        val id = "m1"
        val cd = Color(1.0, 0.5, 0.3)
        val scope = MaterialsScope()

        // when
        scope.matte(id = id, cd = cd)

        // then
        assertEquals(1, scope.materials.size)
        assertTrue(scope.materials.containsKey(id))
        val material = scope.materials[id]
        assertTrue(material is Matte)
        assertEquals(Matte(cd), scope.materials[id])
    }

    @Test
    fun `should handle reflective`() {
        // given
        val id = "m1"
        val ka = 0.3
        val kd = 0.8
        val cd = Color(1.0, 0.5, 0.3)
        val theKr = 0.78
        val theCr = Color(0.1, 0.2, 0.3)
        val scope = MaterialsScope()

        // when
        scope.reflective(id = id, cd = cd, ka = ka, kd = kd, cr = theCr, kr = theKr)

        // then
        assertEquals(1, scope.materials.size)
        assertTrue(scope.materials.containsKey(id))
        val reflective = Reflective(cd, ka, kd).apply {
            cr = theCr
            kr = theKr
        }
        val material = scope.materials[id] as Reflective
        assertEquals(reflective.kr, material.kr)
        assertEquals(reflective.cr, material.cr)
        assertEquals(reflective.ka, material.ka)
        assertEquals(reflective.kd, material.kd)
        // TODO later assertEquals(reflective, material)
    }

}