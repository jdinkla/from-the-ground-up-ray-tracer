package net.dinkla.raytracer.objects.utilities;

import net.dinkla.raytracer.math.Axis;
import net.dinkla.raytracer.math.BBox;
import net.dinkla.raytracer.math.Point3D;
import net.dinkla.raytracer.objects.GeometricObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 16.06.2010
 * Time: 20:35:27
 * To change this template use File | Settings | File Templates.
 */
public class ListUtilities {
    
    static public void splitByAxis(List<GeometricObject> objects, Double split, Axis axis, List<GeometricObject> objectsL, List<GeometricObject> objectsR) {
        objectsL.clear();
        objectsR.clear();
        for (GeometricObject object : objects) {
            BBox bbox = object.getBoundingBox();
            if (bbox.getP().ith(axis) <= split) {
                objectsL.add(object);
            }
            if (bbox.getQ().ith(axis) >= split) {
                objectsR.add(object);
            }
        }
    }

    /**
     * Sorts the objects by the axis.
     * @param objects
     * @param axis
     */
    static public void sortByAxis(List<GeometricObject> objects, final Axis axis) {
        objects.sort((o1, o2) -> {
            final GeometricObject oP = (GeometricObject) o1;
            final GeometricObject oQ = (GeometricObject) o2;
            final BBox bboxP = oP.getBoundingBox();
            final BBox bboxQ = oQ.getBoundingBox();
            final Point3D p = bboxP.getQ();

            final double pP = bboxP.getP().ith(axis);
            final double widthP = bboxP.getQ().ith(axis) - pP;
            final double medP = pP + 0.5 * widthP;

            final double pQ = bboxQ.getP().ith(axis);
            final double widthQ = bboxQ.getQ().ith(axis) - pQ;
            final double medQ = pQ + 0.5 * widthQ;

            final Point3D q = bboxQ.getQ();
            return Double.compare(medP, medQ);
        });
    }

    static public int size(List<GeometricObject> objects) {
        int size = 0;
        for (GeometricObject object : objects) {
            
        }
        return size;
    }
}
