package net.dinkla.raytracer.objects

import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.hits.Shadow
import net.dinkla.raytracer.hits.ShadowHit
import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.objects.compound.Compound
import net.dinkla.raytracer.utilities.Logger

/**
 * The common contract for everything that can be placed in a scene and intersected by a ray —
 * primitives ([net.dinkla.raytracer.objects.Sphere], [net.dinkla.raytracer.objects.Plane], …),
 * compounds, instances and the acceleration structures that wrap them.
 *
 * Besides the core [hit]/[shadowHit] tests it carries the small set of polymorphic hooks the
 * acceleration structures rely on instead of `is Compound`/`is Grid` type checks — see
 * [getResultObject], [combineInCell], [objectCount], [promotableToSubgrid] and [childrenForRegrid].
 */
interface IGeometricObject {
    /** Whether this object casts shadows (i.e. participates in shadow-ray tests). */
    var isShadows: Boolean

    /** The axis-aligned bounding box used by the acceleration structures to place and cull this object. */
    var boundingBox: BBox

    /** The surface material used to shade a hit, or `null` until one is assigned. */
    var material: IMaterial?

    /**
     * Prepares the object for rendering after the scene is assembled — e.g. computing the bounding
     * box or building a nested acceleration structure. Called once before the first [hit].
     */
    fun initialize()

    /**
     * Intersects [ray] with this object. On the closest accepted hit it records the distance,
     * surface normal and struck object into [sr] and returns true; otherwise returns false and
     * leaves [sr] unchanged.
     */
    fun hit(
        ray: Ray,
        sr: IHit,
    ): Boolean

    /**
     * After a successful [hit] on this object recorded into [sr], the geometric object that should
     * be reported as the thing actually struck. A plain object reports itself; a Compound overrides
     * this to report the resolved inner leaf it already stored in [sr] during its own hit.
     *
     * This replaces the former `is Compound` branching at the grid/compound traversal call sites:
     * each type now decides its own result object polymorphically.
     */
    fun getResultObject(sr: IHit): IGeometricObject? = this

    /**
     * Combines this object — the current occupant of an acceleration-structure cell — with a
     * [newObject] that also lands in the same cell, returning the object that should occupy the cell
     * afterwards. A plain object wraps both into a fresh Compound; a Compound overrides this to
     * append [newObject] to itself and stay in place.
     *
     * This replaces the former `is Compound` branching in the grid cell-insertion paths.
     */
    fun combineInCell(newObject: IGeometricObject): IGeometricObject {
        val compound = Compound()
        compound.add(this)
        compound.add(newObject)
        return compound
    }

    /**
     * The number of leaf objects this object represents. A plain object counts as one; a Compound
     * overrides this to sum the counts of its children recursively.
     *
     * This replaces the former `is Compound` branch in `Compound.size()`.
     */
    fun objectCount(): Int = 1

    /**
     * Whether a crowded cell occupant of this type may be promoted into a nested grid by the dense
     * [acceleration.Grid]. A plain object cannot (there is nothing to re-distribute); a Compound can,
     * since it holds children to spread across a sub-grid. A nested [acceleration.Grid] is itself a
     * Compound but overrides this back to `false` — it absorbs further objects directly rather than
     * being re-promoted. This replaces the dense grid's `is Grid` / `is Compound` insertion checks.
     */
    fun promotableToSubgrid(): Boolean = false

    /**
     * The child objects to re-distribute when this occupant is promoted into a nested grid. A plain
     * object yields itself; a Compound yields its children so they spread individually across the new
     * sub-grid (matching the original `grid.add(compound.objects)` behaviour).
     */
    fun childrenForRegrid(): List<IGeometricObject> = listOf(this)

    /**
     * Shadow-ray test that writes the hit distance into [tmin] on success. The default delegates to
     * the [Shadow]-returning [shadowHit] overload; concrete objects typically override this one with
     * the cheaper "is anything in the way?" intersection. Returns true if the ray hits this object.
     */
    fun shadowHit(
        ray: Ray,
        tmin: ShadowHit,
    ): Boolean =
        when (val t = shadowHit(ray)) {
            is Shadow.Hit -> {
                tmin.t = t.t
                true
            }
            Shadow.None -> {
                false
            }
        }

    /**
     * Shadow-ray test returning a [Shadow] result ([Shadow.Hit] with the distance, or [Shadow.None]).
     * The default bridges to the [ShadowHit]-based overload; the warning flags the unexpected fallback
     * path, since objects are expected to implement that overload directly.
     */
    fun shadowHit(ray: Ray): Shadow {
        Logger.warn("Who is calling me?")
        val t = ShadowHit()
        return if (shadowHit(ray, t)) {
            Shadow.Hit(t.t)
        } else {
            Shadow.None
        }
    }
}
