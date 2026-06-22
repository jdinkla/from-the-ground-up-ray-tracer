package net.dinkla.raytracer.world.dsl

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.materials.Emissive
import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.materials.Matte
import net.dinkla.raytracer.materials.Phong
import net.dinkla.raytracer.materials.Reflective
import net.dinkla.raytracer.materials.SvEmissive
import net.dinkla.raytracer.materials.SvMatte
import net.dinkla.raytracer.materials.SvPhong
import net.dinkla.raytracer.materials.Transparent
import net.dinkla.raytracer.textures.Texture

/**
 * DSL receiver for the `materials { ... }` block. Each call registers a material under a string
 * [id]; objects in the `objects { ... }` block reference materials by that same id. The accumulated
 * map is read back through [materials].
 *
 * Reflectance parameters follow Suffern's naming: `cd` diffuse colour, `ka` ambient coefficient,
 * `kd` diffuse coefficient, `ks`/`cs`/`exp` specular coefficient/colour/exponent, `kr`/`cr`
 * reflection coefficient/colour, `kt` transmission coefficient, `ior` index of refraction.
 */
class MaterialsScope {
    private val mutableMaterials: MutableMap<String, IMaterial> = mutableMapOf()

    /** The materials declared so far, keyed by id, as an immutable snapshot. */
    val materials: Map<String, IMaterial>
        get() = mutableMaterials.toMap()

    /** Registers a [Matte] (purely diffuse Lambertian) material under [id]. */
    fun matte(
        id: String,
        cd: Color = Color.WHITE,
        ka: Double = 0.25,
        kd: Double = 0.75,
    ) {
        mutableMaterials[id] = Matte(cd, ka, kd)
    }

    /** Registers a [Phong] (diffuse + specular highlight) material under [id]. */
    @SuppressWarnings("LongParameterList")
    fun phong(
        id: String,
        cd: Color = Color.WHITE,
        ka: Double = 0.25,
        kd: Double = 0.75,
        exp: Double = 5.0,
        ks: Double = 0.25,
        cs: Color = Color.WHITE,
    ) {
        mutableMaterials[id] =
            Phong(cd, ka, kd).apply {
                this.exp = exp
                this.ks = ks
                this.cs = cs
            }
    }

    /**
     * Registers a spatially-varying matte ([SvMatte]) under [id]: a purely diffuse material whose
     * diffuse colour is sampled from [texture] at each hit point instead of being a constant.
     */
    fun svMatte(
        id: String,
        texture: Texture,
        ka: Double = 0.25,
        kd: Double = 0.75,
    ) {
        mutableMaterials[id] = SvMatte(texture, ka, kd)
    }

    /**
     * Registers a spatially-varying Phong ([SvPhong]) under [id]: the diffuse colour is sampled from
     * [texture] while the specular highlight ([cs]/[ks]/[exp]) stays constant.
     */
    @SuppressWarnings("LongParameterList")
    fun svPhong(
        id: String,
        texture: Texture,
        ka: Double = 0.25,
        kd: Double = 0.75,
        exp: Double = 5.0,
        ks: Double = 0.25,
        cs: Color = Color.WHITE,
    ) {
        mutableMaterials[id] =
            SvPhong(texture, ka, kd).apply {
                this.exp = exp
                this.ks = ks
                this.cs = cs
            }
    }

    /** Registers a [Reflective] (Phong plus mirror reflection via [cr]/[kr]) material under [id]. */
    @SuppressWarnings("LongParameterList")
    fun reflective(
        id: String,
        cd: Color = Color.WHITE,
        ka: Double = 0.25,
        kd: Double = 0.75,
        exp: Double = 5.0,
        ks: Double = 0.25,
        cs: Color = Color.WHITE,
        cr: Color = Color.WHITE,
        kr: Double = 1.0,
    ) {
        mutableMaterials[id] =
            Reflective(cd, ka, kd).apply {
                this.exp = exp
                this.ks = ks
                this.cs = cs
                this.cr = cr
                this.kr = kr
            }
    }

    /** Registers an [Emissive] (self-luminous) material of colour [ce] and radiance [le], used for area lights. */
    fun emissive(
        id: String,
        ce: Color = Color.WHITE,
        le: Double = 0.25,
    ) {
        mutableMaterials[id] = Emissive(ce, le)
    }

    /**
     * Registers a spatially-varying emissive ([SvEmissive]) under [id]: self-emits the colour sampled
     * from [texture] scaled by [ls]. Used for a textured environment-map sphere.
     */
    fun svEmissive(
        id: String,
        texture: Texture,
        ls: Double = 1.0,
    ) {
        mutableMaterials[id] = SvEmissive(texture, ls)
    }

    /** Registers a [Transparent] (reflective + refractive via [kt]/[ior]) material under [id]. */
    @SuppressWarnings("LongParameterList")
    fun transparent(
        id: String,
        cd: Color = Color.WHITE,
        ka: Double = 0.25,
        kd: Double = 0.75,
        exp: Double = 5.0,
        ks: Double = 0.25,
        cs: Color = Color.WHITE,
        kt: Double = 0.25,
        ior: Double = 0.25,
        kr: Double = 0.25,
        cr: Color = Color.WHITE,
    ) {
        val transparent =
            Transparent().apply {
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
