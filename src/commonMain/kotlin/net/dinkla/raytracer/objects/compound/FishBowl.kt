package net.dinkla.raytracer.objects.compound

import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.MathUtils
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.objects.ConcavePartSphere
import net.dinkla.raytracer.objects.Disk
import net.dinkla.raytracer.objects.GeometricObject
import net.dinkla.raytracer.objects.Instance
import net.dinkla.raytracer.objects.PartSphere
import net.dinkla.raytracer.objects.PartTorus
import net.dinkla.raytracer.utilities.equals
import java.util.Objects
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * A spherical fishbowl, modelled the way Suffern does it (*Ray Tracing from the Ground Up*, §28.8,
 * Figures 28.39 and 28.41): a glass sphere centred at the origin with a circular **opening cut out of
 * the top** (where the fish goes in), partly filled with water. Like [GlassOfWater] it is a [Compound]
 * of *boundary surfaces*, where every part is the single interface between exactly two media and carries
 * the [net.dinkla.raytracer.materials.Dielectric] whose `iorIn`/`iorOut` describe those two media.
 *
 * The glass shell occupies the radii [innerRadius]..[outerRadius]. The top opening removes the polar
 * cap `theta ∈ [0, `[openingAngle]`]` (theta from +y) from both sphere walls, so the bowl is open at the
 * top. Water fills the cavity up to `y = `[waterY]; the inner sphere crosses that level at the polar
 * angle `thetaWater = acos(`[waterY]`/`[innerRadius]`)`. The parts, grouped by the boundary they form:
 *
 *  - **glass↔air** ([glassAir]): the **outer glass** (convex [PartSphere], radius [outerRadius],
 *    `theta ∈ [openingAngle, π]`); the **inner glass above the water** (concave [ConcavePartSphere],
 *    radius [innerRadius], `theta ∈ [openingAngle, thetaWater]` — the dry cavity wall in air); and the
 *    **rim** (a [PartTorus] ring rounding the lip of the opening, connecting the inner and outer
 *    walls).
 *  - **water↔glass** ([waterGlass]): the **inner glass below the water** ([PartSphere], radius
 *    [innerRadius], `theta ∈ [thetaWater, π]` — the submerged cavity wall the water touches).
 *  - **water↔air** ([waterAir]): the flat **water surface** ([Disk], radius `sqrt(innerRadius² −
 *    waterY²)` at `y = waterY`, normal up).
 *
 * The book orients each boundary's convex/concave normal by hand, but that is unnecessary here: the
 * [net.dinkla.raytracer.materials.Dielectric] Fresnel terms re-derive the relative index from the sign
 * of `n·wo` per hit (flipping the normal and inverting eta for back-face hits), so the optics are
 * invariant to the stored normal's sign. Assigning the correct `iorIn`/`iorOut` to each boundary is what
 * makes the optics correct, not the per-part normal orientation. The three materials are assigned per
 * part in the constructor and are *not* propagated through [Compound.material] (which would overwrite all
 * three with one) — keep this object out of the single-material `add(material)` DSL path.
 */
@Suppress("LongParameterList")
class FishBowl(
    val glassAir: IMaterial,
    val waterAir: IMaterial,
    val waterGlass: IMaterial,
    val innerRadius: Double = 1.85,
    val outerRadius: Double = 2.0,
    val waterY: Double = 0.8,
    val openingAngle: Double = DEFAULT_OPENING_ANGLE,
) : Compound() {
    /** Polar angle (from +y) at which the inner sphere crosses the water surface `y = waterY`. */
    private val thetaWater: Double = acos(waterY / innerRadius)

    init {
        addGlassAirParts()
        addWaterGlassParts()
        addWaterAirParts()

        val eps = MathUtils.K_EPSILON
        boundingBox =
            BBox(
                Point3D(-outerRadius - eps, -outerRadius - eps, -outerRadius - eps),
                Point3D(outerRadius + eps, outerRadius + eps, outerRadius + eps),
            )
    }

    private fun addGlassAirParts() {
        // The outer glass (everything below the opening), the dry inner wall above the water, and a
        // torus rim rounding the lip of the opening — every one a glass↔air interface.
        val outerGlass =
            PartSphere(
                center = Point3D.ORIGIN,
                radius = outerRadius,
                thetaMin = openingAngle,
                thetaMax = MathUtils.PI,
            )
        val innerUpperGlass =
            ConcavePartSphere(
                center = Point3D.ORIGIN,
                radius = innerRadius,
                thetaMin = openingAngle,
                thetaMax = thetaWater,
            )

        addPart(outerGlass, glassAir)
        addPart(innerUpperGlass, glassAir)
        addPart(rim(), glassAir)
    }

    private fun addWaterGlassParts() {
        // The submerged inner cavity wall the water touches — a water↔glass interface.
        val innerLowerGlass =
            PartSphere(
                center = Point3D.ORIGIN,
                radius = innerRadius,
                thetaMin = thetaWater,
                thetaMax = MathUtils.PI,
            )
        addPart(innerLowerGlass, waterGlass)
    }

    private fun addWaterAirParts() {
        // The flat water surface, a disk whose radius is the inner sphere's cross-section at y = waterY.
        val surfaceRadius = sqrt(innerRadius * innerRadius - waterY * waterY)
        val surface = Disk(Point3D(0.0, waterY, 0.0), surfaceRadius, Normal.UP)
        addPart(surface, waterAir)
    }

    /**
     * A torus rounding the rim of the opening: its tube radius is half the shell thickness and its sweep
     * radius is the mid-shell horizontal radius at the opening angle, translated up to the opening height
     * so it bridges the inner and outer sphere walls at the lip.
     */
    private fun rim(): GeometricObject {
        val midRadius = (innerRadius + outerRadius) / 2.0
        val tubeRadius = (outerRadius - innerRadius) / 2.0
        val sweepRadius = midRadius * sin(openingAngle)
        val rimHeight = midRadius * cos(openingAngle)
        val torus = Instance(PartTorus(sweepRadius, tubeRadius))
        torus.translate(Vector3D(0.0, rimHeight, 0.0))
        return torus
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
        this.equals<FishBowl>(other) { a, b ->
            a.glassAir == b.glassAir &&
                a.waterAir == b.waterAir &&
                a.waterGlass == b.waterGlass &&
                a.innerRadius == b.innerRadius &&
                a.outerRadius == b.outerRadius &&
                a.waterY == b.waterY &&
                a.openingAngle == b.openingAngle
        }

    override fun hashCode(): Int =
        Objects.hash(
            glassAir,
            waterAir,
            waterGlass,
            innerRadius,
            outerRadius,
            waterY,
            openingAngle,
        )

    override fun toString(): String =
        "FishBowl(innerRadius=$innerRadius, outerRadius=$outerRadius, waterY=$waterY, " +
            "openingAngle=$openingAngle)"

    companion object {
        /** Half-angle of the top opening, in degrees (theta from +y); the book's bowl has a wide neck. */
        private const val OPENING_DEGREES = 50.0

        /** Default opening angle in radians, shared with the `fishBowl` DSL adder so the two stay in sync. */
        val DEFAULT_OPENING_ANGLE: Double = MathUtils.PI / 180.0 * OPENING_DEGREES
    }
}
