package net.dinkla.raytracer.worlds.dsl

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.materials.Matte

class MaterialsScope() {

    private val mutableMaterials : MutableMap<String, IMaterial> = mutableMapOf()

    val materials : Map<String, IMaterial>
        get() = mutableMaterials.toMap()

    fun matte(id: String, cd: Color = Color.WHITE, ka: Double = 0.25, kd: Double = 0.75) {
        mutableMaterials[id] = Matte(cd, ka, kd)
    }

}