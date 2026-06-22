package net.dinkla.raytracer.objects

import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.hits.Shadow
import net.dinkla.raytracer.hits.ShadowHit
import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.objects.compound.Compound
import net.dinkla.raytracer.utilities.Logger

interface IGeometricObject {
    var isShadows: Boolean
    var boundingBox: BBox
    var material: IMaterial?

    fun initialize()

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
