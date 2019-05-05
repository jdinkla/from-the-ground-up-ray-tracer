package net.dinkla.raytracer.world.dsl

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.materials.*

class MaterialsScope() {

    private val mutableMaterials: MutableMap<String, IMaterial> = mutableMapOf()

    val materials: Map<String, IMaterial>
        get() = mutableMaterials.toMap()

    fun matte(id: String,
              cd: Color = Color.WHITE,
              ka: Double = 0.25,
              kd: Double = 0.75) {
        mutableMaterials[id] = Matte(cd, ka, kd)
    }

    fun phong(id: String,
              cd: Color = Color.WHITE,
              ka: Double = 0.25,
              kd: Double = 0.75,
              exp: Double = 5.0,
              ks: Double = 0.25,
              cs: Color = Color.WHITE) {
        mutableMaterials[id] = Phong(cd, ka, kd).apply {
            this.exp = exp
            this.ks = ks
            this.cs = cs
        }
    }

    fun reflective(id: String,
                   cd: Color = Color.WHITE,
                   ka: Double = 0.25,
                   kd: Double = 0.75,
                   exp: Double = 5.0,
                   ks: Double = 0.25,
                   cs: Color = Color.WHITE,
                   cr: Color = Color.WHITE,
                   kr: Double = 1.0) {
        mutableMaterials[id] = Reflective(cd, ka, kd).apply {
            this.exp = exp
            this.ks = ks
            this.cs = cs
            this.cr = cr
            this.kr = kr
        }
    }

    fun emissive(id: String,
                 ce: Color = Color.WHITE,
                 ls: Double = 0.25) {
        mutableMaterials[id] = Emissive(ce, ls)
    }

    fun transparent(id: String,
                    cd: Color = Color.WHITE,
                    ka: Double = 0.25,
                    kd: Double = 0.75,
                    exp: Double = 5.0,
                    ks: Double = 0.25,
                    cs: Color = Color.WHITE,
                    kt: Double = 0.25,
                    ior: Double = 0.25,
                    kr: Double = 0.25,
                    cr: Color = Color.WHITE) {
        val transparent = Transparent().apply {
            this.cd = cd
            this.ka = ka
            this.kd = kd
            this.exp = exp
            this.ks = ks
            this.cs = cs
            this.kt = kt
            this.ior = ior
            this.cr = cr
            this.kr = kr
        }
        mutableMaterials[id] = transparent
    }

}