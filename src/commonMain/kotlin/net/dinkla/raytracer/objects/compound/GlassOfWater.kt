package net.dinkla.raytracer.objects.compound

import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.MathUtils
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.objects.Annulus
import net.dinkla.raytracer.objects.Disk
import net.dinkla.raytracer.objects.GeometricObject
import net.dinkla.raytracer.objects.Instance
import net.dinkla.raytracer.objects.PartCylinder
import net.dinkla.raytracer.objects.PartTorus
import net.dinkla.raytracer.utilities.equals
import java.util.Objects

/**
 * A glass of water, modelled the way Suffern does it (*Ray Tracing from the Ground Up*, §28.7,
 * Figures 28.35–28.38): **not** as a solid water cylinder dropped inside a glass ring, but as a
 * [Compound] of *boundary surfaces*, where every part is the single interface between exactly two
 * media. Each boundary carries the [net.dinkla.raytracer.materials.Dielectric] whose `iorIn`/`iorOut`
 * describe the two media it separates, so a ray crossing it refracts/reflects with the correct
 * relative index. Suffern stresses that a solid cylinder of water inside a ring would double up the
 * water↔glass interface (two coincident surfaces) and get the optics wrong; the boundary model has
 * one surface per interface.
 *
 * The glass stands on the +y axis, its base at `y = `[bottomY], its rim at `y = `[topY]. The glass
 * wall occupies the radii [innerRadius]..[outerRadius]; the cavity (radius [innerRadius]) is filled
 * with water up to `y = `[waterY] and the cavity floor sits at `y = `[innerBottomY] (the glass base is
 * solid between [bottomY] and [innerBottomY]). The parts, grouped by the boundary they form:
 *
 *  - **glass↔air** ([glassAir]): the **top rim** ([Annulus], normal up); the **outer wall**
 *    (convex [PartCylinder], radius [outerRadius]); the **upper inner wall** above the water line
 *    (concave [PartCylinder], radius [innerRadius], `waterY..topY` — the cavity wall in contact with
 *    air); and the **bottom** ([Disk], normal down).
 *  - **water↔glass** ([waterGlass]): the **lower inner wall** below the water line (convex
 *    [PartCylinder], radius [innerRadius], `innerBottomY..waterY` — the cavity wall the water touches)
 *    and the **cavity floor** ([Disk], radius [innerRadius] at `innerBottomY`).
 *  - **water↔air** ([waterAir]): the flat **water surface** ([Disk], radius `innerRadius - rMeniscus`
 *    at `waterY`, normal up) plus a quarter-[PartTorus] **meniscus** rounding where the water meets the
 *    glass wall.
 *
 * The book orients each boundary's convex/concave normal by hand, but that is unnecessary here: the
 * [net.dinkla.raytracer.materials.Dielectric] Fresnel terms re-derive the relative index from the
 * sign of `n·wo` per hit (flipping the normal and inverting eta for back-face hits), so the optics are
 * invariant to the stored normal's sign. Assigning the correct `iorIn`/`iorOut` to each boundary is
 * therefore what makes the optics correct, not the per-part normal orientation. The
 * three materials are assigned per part in the constructor and are *not* propagated through
 * [Compound.material] (which would overwrite all three with one) — keep this object out of the
 * single-material `add(material)` DSL path.
 */
class GlassOfWater(
    val glassAir: IMaterial,
    val waterGlass: IMaterial,
    val waterAir: IMaterial,
    val bottomY: Double = 0.0,
    val innerBottomY: Double = 0.2,
    val waterY: Double = 1.4,
    val topY: Double = 2.0,
    val innerRadius: Double = 0.9,
    val outerRadius: Double = 1.0,
    val meniscusRadius: Double = 0.06,
) : Compound() {
    init {
        addGlassAirParts()
        addWaterGlassParts()
        addWaterAirParts()

        val eps = MathUtils.K_EPSILON
        boundingBox =
            BBox(
                Point3D(-outerRadius - eps, bottomY - eps, -outerRadius - eps),
                Point3D(outerRadius + eps, topY + eps, outerRadius + eps),
            )
    }

    private fun addGlassAirParts() {
        // Top rim closing the glass wall, the outer wall, the inner wall above the water (in air),
        // and the bottom of the glass — every one a glass↔air interface.
        val topRim = Annulus(Point3D(0.0, topY, 0.0), innerRadius, outerRadius, Normal.UP)
        val outerWall = PartCylinder(bottomY, topY, outerRadius)
        val upperInnerWall = PartCylinder(waterY, topY, innerRadius)
        val bottom = Disk(Point3D(0.0, bottomY, 0.0), outerRadius, Normal.DOWN)

        addPart(topRim, glassAir)
        addPart(outerWall, glassAir)
        addPart(upperInnerWall, glassAir)
        addPart(bottom, glassAir)
    }

    private fun addWaterGlassParts() {
        // The cavity wall the water touches and the cavity floor — both water↔glass interfaces.
        val lowerInnerWall = PartCylinder(innerBottomY, waterY, innerRadius)
        val cavityFloor = Disk(Point3D(0.0, innerBottomY, 0.0), innerRadius, Normal.UP)

        addPart(lowerInnerWall, waterGlass)
        addPart(cavityFloor, waterGlass)
    }

    private fun addWaterAirParts() {
        // The flat water surface plus a quarter-torus meniscus rounding up to the glass wall.
        val surface = Disk(Point3D(0.0, waterY, 0.0), innerRadius - meniscusRadius, Normal.UP)
        addPart(surface, waterAir)

        val meniscusSweep = innerRadius - meniscusRadius
        val torus = Instance(PartTorus(meniscusSweep, meniscusRadius))
        torus.translate(Vector3D(0.0, waterY, 0.0))
        addPart(torus, waterAir)
    }

    /** Adds [part] with [material] set directly on the part, bypassing [Compound]'s single-material setter. */
    private fun addPart(
        part: GeometricObject,
        material: IMaterial,
    ) {
        part.material = material
        objects.add(part)
    }

    override fun hit(
        ray: Ray,
        sr: IHit,
    ): Boolean =
        if (boundingBox.isHit(ray)) {
            super.hit(ray, sr)
        } else {
            false
        }

    override fun equals(other: Any?): Boolean =
        this.equals<GlassOfWater>(other) { a, b ->
            a.glassAir == b.glassAir &&
                a.waterGlass == b.waterGlass &&
                a.waterAir == b.waterAir &&
                a.bottomY == b.bottomY &&
                a.innerBottomY == b.innerBottomY &&
                a.waterY == b.waterY &&
                a.topY == b.topY &&
                a.innerRadius == b.innerRadius &&
                a.outerRadius == b.outerRadius &&
                a.meniscusRadius == b.meniscusRadius
        }

    override fun hashCode(): Int =
        Objects.hash(
            glassAir,
            waterGlass,
            waterAir,
            bottomY,
            innerBottomY,
            waterY,
            topY,
            innerRadius,
            outerRadius,
            meniscusRadius,
        )

    override fun toString(): String =
        "GlassOfWater(bottomY=$bottomY, innerBottomY=$innerBottomY, waterY=$waterY, topY=$topY, " +
            "innerRadius=$innerRadius, outerRadius=$outerRadius, meniscusRadius=$meniscusRadius)"
}
