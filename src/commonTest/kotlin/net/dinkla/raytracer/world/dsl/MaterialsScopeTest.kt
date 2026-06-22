package net.dinkla.raytracer.world.dsl

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import net.dinkla.raytracer.Fixture.Ex
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.materials.Dielectric
import net.dinkla.raytracer.materials.Emissive
import net.dinkla.raytracer.materials.GlossyReflector
import net.dinkla.raytracer.materials.Matte
import net.dinkla.raytracer.materials.Phong
import net.dinkla.raytracer.materials.Reflective
import net.dinkla.raytracer.materials.SvEmissive
import net.dinkla.raytracer.materials.SvMatte
import net.dinkla.raytracer.materials.SvPhong
import net.dinkla.raytracer.materials.Transparent
import net.dinkla.raytracer.textures.ConstantColor

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

        "should handle glossyReflector" {
            // given
            val scope = MaterialsScope()
            val glossy =
                GlossyReflector(Ex.cd, Ex.ka, Ex.kd).apply {
                    ks = Ex.ks
                    cs = Ex.cs
                    cr = Ex.cr
                    kr = Ex.kr
                    exp = Ex.exp
                }

            // when
            scope.glossyReflector(
                id = id,
                cd = Ex.cd,
                ka = Ex.ka,
                kd = Ex.kd,
                exp = Ex.exp,
                ks = Ex.ks,
                cs = Ex.cs,
                cr = Ex.cr,
                kr = Ex.kr,
            )

            // then
            scope.materials.size shouldBe 1
            scope.materials.containsKey(id) shouldBe true
            val material = scope.materials[id] as GlossyReflector
            material.kr shouldBe glossy.kr
            material.cr shouldBe glossy.cr
            material.ka shouldBe glossy.ka
            material.kd shouldBe glossy.kd
            material.exp shouldBe glossy.exp
            material shouldBe glossy
        }

        "should handle svMatte declared with a texture" {
            val scope = MaterialsScope()
            val texture = ConstantColor(Ex.cd)

            scope.svMatte(id = id, texture = texture, ka = Ex.ka, kd = Ex.kd)

            scope.materials.size shouldBe 1
            val material = scope.materials[id].shouldBeInstanceOf<SvMatte>()
            material.texture shouldBe texture
            material.ka shouldBe Ex.ka
            material.kd shouldBe Ex.kd
        }

        "should handle svPhong declared with a texture" {
            val scope = MaterialsScope()
            val texture = ConstantColor(Ex.cd)

            scope.svPhong(id = id, texture = texture, ka = Ex.ka, kd = Ex.kd, exp = Ex.exp, ks = Ex.ks, cs = Ex.cs)

            val material = scope.materials[id].shouldBeInstanceOf<SvPhong>()
            material.texture shouldBe texture
            material.exp shouldBe Ex.exp
            material.ks shouldBe Ex.ks
            material.cs shouldBe Ex.cs
        }

        "should handle svEmissive declared with a texture" {
            val scope = MaterialsScope()
            val texture = ConstantColor(Ex.cd)

            scope.svEmissive(id = id, texture = texture, ls = 1.0)

            val material = scope.materials[id].shouldBeInstanceOf<SvEmissive>()
            material.texture shouldBe texture
            material.ls shouldBe 1.0
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

        "should handle dielectric with in/out IORs and filter colours" {
            val scope = MaterialsScope()
            val cfIn = Color(0.85, 0.95, 0.9)
            val cfOut = Color.WHITE

            scope.dielectric(
                id = id,
                iorIn = 1.5,
                iorOut = 1.0,
                cfIn = cfIn,
                cfOut = cfOut,
                cd = Ex.cd,
                ka = Ex.ka,
                kd = Ex.kd,
                exp = Ex.exp,
                ks = Ex.ks,
                cs = Ex.cs,
            )

            scope.materials.size shouldBe 1
            val material = scope.materials[id].shouldBeInstanceOf<Dielectric>()
            material.iorIn shouldBe 1.5
            material.iorOut shouldBe 1.0
            material.cfIn shouldBe cfIn
            material.cfOut shouldBe cfOut
            material.ka shouldBe Ex.ka
            material.kd shouldBe Ex.kd
            material.exp shouldBe Ex.exp
            material.ks shouldBe Ex.ks
            material.cs shouldBe Ex.cs
        }
    })
