package net.dinkla.raytracer.world.dsl

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.Fixture.Ex
import net.dinkla.raytracer.materials.Emissive
import net.dinkla.raytracer.materials.Matte
import net.dinkla.raytracer.materials.Phong
import net.dinkla.raytracer.materials.Reflective
import net.dinkla.raytracer.materials.Transparent

class MaterialsScopeTest :
    StringSpec({
        val id = "m1"
        "should handle matte" {
            // given
            val scope = MaterialsScope()
            val matte = Matte(Ex.cd)

            // when
            scope.matte(id = id, cd = Ex.cd)

            // then
            scope.materials.size shouldBe 1
            scope.materials.containsKey(id) shouldBe true
            scope.materials[id] as Matte shouldBe matte
        }

        "should handle phong" {
            // given
            val phong =
                Phong(Ex.cd, Ex.ka, Ex.kd).apply {
                    exp = Ex.exp
                    cs = Ex.cs
                    ks = Ex.ks
                }

            println("phong $phong")
            val scope = MaterialsScope()

            // when
            scope.phong(id = id, cd = Ex.cd, ka = Ex.ka, kd = Ex.kd, exp = Ex.exp, cs = Ex.cs, ks = Ex.ks)

            // then
            scope.materials.size shouldBe 1
            scope.materials.containsKey(id) shouldBe true
            scope.materials[id] as Phong shouldBe phong
        }

        "should handle reflective" {
            // given
            val scope = MaterialsScope()
            val reflective =
                Reflective(Ex.cd, Ex.ka, Ex.kd).apply {
                    exp = Ex.exp
                    cs = Ex.cs
                    ks = Ex.ks
                    cr = Ex.cr
                    kr = Ex.kr
                }

            // when
            scope.reflective(
                id = id,
                cd = Ex.cd,
                ka = Ex.ka,
                kd = Ex.kd,
                cr = Ex.cr,
                kr = Ex.kr,
                ks = Ex.ks,
                cs = Ex.cs,
                exp = Ex.exp,
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
            val emissive = Emissive(Ex.cd, Ex.ks)

            // when
            scope.emissive(id = id, ce = Ex.cd, le = Ex.ks)

            // then
            scope.materials.size shouldBe 1
            scope.materials.containsKey(id) shouldBe true
            scope.materials[id] as Emissive shouldBe emissive
        }

        "should handle transparent" {
            // given
            val scope = MaterialsScope()
            val transparent =
                Transparent().apply {
                    cd = Ex.cd
                    ka = Ex.ka
                    kd = Ex.kd
                    exp = Ex.exp
                    ks = Ex.ks
                    cs = Ex.cs
                    kt = Ex.kt
                    ior = Ex.ior
                    cr = Ex.cr
                    kr = Ex.kr
                }

            // when
            scope.transparent(
                id = id,
                cd = Ex.cd,
                ka = Ex.ka,
                kd = Ex.kd,
                cr = Ex.cr,
                kr = Ex.kr,
                ks = Ex.ks,
                cs = Ex.cs,
                exp = Ex.exp,
                kt = Ex.kt,
                ior = Ex.ior,
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
