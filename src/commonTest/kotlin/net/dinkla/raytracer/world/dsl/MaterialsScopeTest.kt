package net.dinkla.raytracer.world.dsl

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.Fixture.ex
import net.dinkla.raytracer.materials.*

class MaterialsScopeTest : StringSpec({
    val id = "m1"
    "should handle matte" {
        // given
        val scope = MaterialsScope()
        val matte = Matte(ex.cd)

        // when
        scope.matte(id = id, cd = ex.cd)

        // then
        scope.materials.size shouldBe 1
        scope.materials.containsKey(id) shouldBe true
        scope.materials[id] as Matte shouldBe matte
    }

    "should handle phong" {
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
        scope.materials.size shouldBe 1
        scope.materials.containsKey(id) shouldBe true
        scope.materials[id] as Phong shouldBe phong
    }

    "should handle reflective" {
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
        scope.reflective(
            id = id, cd = ex.cd, ka = ex.ka, kd = ex.kd,
            cr = ex.cr, kr = ex.kr,
            ks = ex.ks, cs = ex.cs, exp = ex.exp
        )

        // then
        scope.materials.size shouldBe 1
        scope.materials.containsKey(id) shouldBe true
        val material = scope.materials[id] as Reflective
        material.kr shouldBe reflective.kr
        material.cr shouldBe reflective.cr
        material.ka shouldBe reflective.ka
        material.kd shouldBe reflective.kd
        material.exp shouldBe reflective.exp
        material shouldBe reflective
    }

    "should handle emissive" {
        // given
        val scope = MaterialsScope()
        val emissive = Emissive(ex.cd, ex.ks)

        // when
        scope.emissive(id = id, ce = ex.cd, le = ex.ks)

        // then
        scope.materials.size shouldBe 1
        scope.materials.containsKey(id) shouldBe true
        scope.materials[id] as Emissive shouldBe emissive
    }

    "should handle transparent" {
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
        scope.transparent(
            id = id, cd = ex.cd, ka = ex.ka, kd = ex.kd,
            cr = ex.cr, kr = ex.kr,
            ks = ex.ks, cs = ex.cs, exp = ex.exp,
            kt = ex.kt, ior = ex.ior
        )

        // then
        scope.materials.size shouldBe 1
        scope.materials.containsKey(id) shouldBe true
        val material = scope.materials[id] as Transparent
        material.kr shouldBe transparent.kr
        material.cr shouldBe transparent.cr
        material.ka shouldBe transparent.ka
        material.kd shouldBe transparent.kd
        material.exp shouldBe transparent.exp
        material.kt shouldBe transparent.kt
        material.ior shouldBe transparent.ior
        material.cr shouldBe transparent.cr
        material shouldBe transparent
    }
})
