package net.dinkla.raytracer.objects.utilities;

import net.dinkla.raytracer.math.Axis;
import net.dinkla.raytracer.math.BBox;
import net.dinkla.raytracer.math.Point3DF;
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
    
    static public void splitByAxis(List<GeometricObject> objects, Float split, Axis axis, List<GeometricObject> objectsL, List<GeometricObject> objectsR) {
        objectsL.clear();
        objectsR.clear();
        for (GeometricObject object : objects) {
            BBox bbox = object.getBoundingBox();
            if (bbox.p.ith(axis) <= split) {
                objectsL.add(object);
            }
            if (bbox.q.ith(axis) >= split) {
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
        Collections.sort(objects, new Comparator() {
            public int compare(Object o1, Object o2) {
                final GeometricObject oP = (GeometricObject) o1;
                final GeometricObject oQ = (GeometricObject) o2;
                final BBox bboxP = oP.getBoundingBox();
                final BBox bboxQ = oQ.getBoundingBox();
                final Point3DF p = bboxP.q;

                final float pP = bboxP.p.ith(axis);
                final float widthP = bboxP.q.ith(axis) - pP;
                final float medP = pP + 0.5f * widthP;

                final float pQ = bboxQ.p.ith(axis);
                final float widthQ = bboxQ.q.ith(axis) - pQ;
                final float medQ = pQ + 0.5f * widthQ;

                final Point3DF q = bboxQ.q;
                return Float.compare(medP, medQ);
            }
        });
    }

    static public int size(List<GeometricObject> objects) {
        int size = 0;
        for (GeometricObject object : objects) {
            
        }
        return size;
    }
}
