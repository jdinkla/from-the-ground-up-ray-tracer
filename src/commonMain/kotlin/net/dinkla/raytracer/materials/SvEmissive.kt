package net.dinkla.raytracer.materials

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.textures.Texture
import net.dinkla.raytracer.utilities.equals
import net.dinkla.raytracer.world.IWorld
import java.util.Objects

/**
 * A spatially-varying [Emissive] material: it self-emits the colour sampled from a [Texture] at the
 * hit point, scaled by [ls]. Two uses:
 *
 *  - as the material of a large enclosing sphere textured with a [net.dinkla.raytracer.textures
 *    .ImageTexture] + [net.dinkla.raytracer.mappings.SphericalMap], it acts as a spherical
 *    environment map (the "textured object" route to environment lighting);
 *  - as the `material` of an [net.dinkla.raytracer.lights.EnvironmentLight], whose `l(...)` calls
 *    [getLe] to read the emitted radiance.
 *
 * Mirrors the role of Suffern's `SV_Emissive` (Ray Tracing from the Ground Up, ch. 29/18).
 */
class SvEmissive(
    val texture: Texture,
    val ls: Double = 1.0,
) : IMaterial {
    override fun shade(
        world: IWorld,
        sr: IShade,
    ): Color = texture.getColor(sr) * ls

    override fun areaLightShade(
        world: IWorld,
        sr: IShade,
    ): Color =
        if ((-(sr.normal)) dot sr.ray.direction > 0) {
            texture.getColor(sr) * ls
        } else {
            Color.BLACK
        }

    override fun getLe(sr: IShade): Color = texture.getColor(sr) * ls

    override fun equals(other: Any?): Boolean =
        this.equals<SvEmissive>(other) { a, b ->
            a.texture == b.texture && a.ls == b.ls
        }

    override fun hashCode(): Int = Objects.hash(texture, ls)

    override fun toString(): String = "SvEmissive($texture, $ls)"
}
