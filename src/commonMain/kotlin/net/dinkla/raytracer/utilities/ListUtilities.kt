package net.dinkla.raytracer.utilities

import net.dinkla.raytracer.math.Axis
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.objects.IGeometricObject

object ListUtilities {
    fun splitByAxis(
        objects: List<IGeometricObject>,
        split: Double,
        axis: Axis,
        objectsL: MutableList<IGeometricObject>,
        objectsR: MutableList<IGeometricObject>,
    ) {
        objectsL.clear()
        objectsR.clear()
        for (obj in objects) {
            val bBox = obj.boundingBox
            if (bBox.p.ith(axis) <= split) {
                objectsL.add(obj)
            }
            if (bBox.q.ith(axis) >= split) {
                objectsR.add(obj)
            }
        }
    }

    private fun compare(
        p: IGeometricObject,
        q: IGeometricObject,
        axis: Axis,
    ): Int {
        val medianOfP = p.boundingBox.median(axis)
        val medianOfQ = q.boundingBox.median(axis)
        return medianOfP.compareTo(medianOfQ)
    }

    private fun BBox.median(axis: Axis): Double {
        val p = p.ith(axis)
        val width = q.ith(axis) - p
        return p + 0.5 * width
    }

    fun sortByAxis(
        objects: List<IGeometricObject>,
        axis: Axis,
    ) = objects.sortedWith { p, q -> compare(p, q, axis) }
}
