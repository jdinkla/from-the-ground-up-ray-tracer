package net.dinkla.raytracer.utilities

import net.dinkla.raytracer.math.Axis
import net.dinkla.raytracer.objects.IGeometricObject

object ListUtilities {

    fun splitByAxis(
        objects: List<IGeometricObject>, split: Double, axis: Axis,
        objectsL: MutableList<IGeometricObject>,
        objectsR: MutableList<IGeometricObject>
    ) {
        objectsL.clear()
        objectsR.clear()
        for (`object` in objects) {
            val bbox = `object`.boundingBox
            if (bbox.p.ith(axis) <= split) {
                objectsL.add(`object`)
            }
            if (bbox.q.ith(axis) >= split) {
                objectsR.add(`object`)
            }
        }
    }

    private fun compare(oP: IGeometricObject, oQ: IGeometricObject, axis: Axis): Int {
        val bboxP = oP.boundingBox
        val bboxQ = oQ.boundingBox

        val pP = bboxP.p.ith(axis)
        val widthP = bboxP.q.ith(axis) - pP
        val medP = pP + 0.5 * widthP

        val pQ = bboxQ.p.ith(axis)
        val widthQ = bboxQ.q.ith(axis) - pQ
        val medQ = pQ + 0.5 * widthQ

        return medP.compareTo(medQ)
    }

    fun sortByAxis(objects: List<IGeometricObject>, axis: Axis) =
        objects.sortedWith { p, q -> compare(p, q, axis)}

}
