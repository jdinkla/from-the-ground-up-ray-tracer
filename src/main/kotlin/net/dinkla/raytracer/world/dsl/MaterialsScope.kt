package net.dinkla.raytracer.world.dsl

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.materials.Matte
import net.dinkla.raytracer.materials.Reflective

class MaterialsScope() {

    private val mutableMaterials : MutableMap<String, IMaterial> = mutableMapOf()

    val materials : Map<String, IMaterial>
        get() = mutableMaterials.toMap()

    fun matte(id: String,
              cd: Color = Color.WHITE,
              ka: Double = 0.25,
              kd: Double = 0.75) {
        mutableMaterials[id] = Matte(cd, ka, kd)
    }

    fun reflective(id: String,
                   cd: Color = Color.WHITE,
                   ka: Double = 0.25,
                   kd: Double = 0.75,
                   cr: Color = Color.WHITE,
                   kr: Double = 1.0) {
        val c = Reflective(cd, ka, kd).apply {
            this.cr = cr
            this.kr = kr
        }
        mutableMaterials[id] = c
    }

}