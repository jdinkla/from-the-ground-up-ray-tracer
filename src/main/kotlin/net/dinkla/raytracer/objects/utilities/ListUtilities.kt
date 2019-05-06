package net.dinkla.raytracer.objects.utilities

import net.dinkla.raytracer.math.Axis
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.objects.GeometricObject

import java.util.Collections
import java.util.Comparator

object ListUtilities {

    fun splitByAxis(objects: List<GeometricObject>, split: Double, axis: Axis,
                    objectsL: MutableList<GeometricObject>,
                    objectsR: MutableList<GeometricObject>) {
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

    fun compare(oP: GeometricObject, oQ: GeometricObject, axis: Axis): Int{
        val bboxP = oP.boundingBox
        val bboxQ = oQ.boundingBox

        val pP = bboxP.p.ith(axis)
        val widthP = bboxP.q.ith(axis) - pP
        val medP = pP + 0.5 * widthP

        val pQ = bboxQ.p.ith(axis)
        val widthQ = bboxQ.q.ith(axis) - pQ
        val medQ = pQ + 0.5 * widthQ

        return java.lang.Double.compare(medP, medQ)
    }

    fun sortByAxis(objects: List<GeometricObject>, axis: Axis) =
            Collections.sort(objects, { p, q -> compare(p, q, axis) })
}
