package net.dinkla.raytracer.audit

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.materials.Matte
import net.dinkla.raytracer.materials.SvMatte
import net.dinkla.raytracer.textures.Checker3D

class TextureCollectorTest : StringSpec({
    "finds a texture nested inside a spatially-varying material's BRDF" {
        val found = TextureCollector.collect(listOf(SvMatte(Checker3D())))

        found shouldBe setOf(Checker3D::class.java.name)
    }

    "a material with no texture yields nothing" {
        TextureCollector.collect(listOf(Matte())).shouldBeEmpty()
    }

    "a non-material object with no reachable texture yields nothing" {
        TextureCollector.collect(listOf(Color.WHITE)).shouldBeEmpty()
    }

    "collects textures across several roots" {
        val found = TextureCollector.collect(listOf(Matte(), SvMatte(Checker3D())))

        found shouldContain Checker3D::class.java.name
    }
})
