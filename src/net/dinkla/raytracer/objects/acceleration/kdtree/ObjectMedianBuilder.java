package net.dinkla.raytracer.objects.acceleration.kdtree;

import net.dinkla.raytracer.math.Axis;
import net.dinkla.raytracer.math.BBox;
import net.dinkla.raytracer.math.Point3DF;
import net.dinkla.raytracer.math.Vector3DF;
import net.dinkla.raytracer.objects.GeometricObject;
import net.dinkla.raytracer.utilities.Counter;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 15.06.2010
 * Time: 19:34:23
 * To change this template use File | Settings | File Templates.
 */
public class ObjectMedianBuilder implements IKDTreeBuilder {

    static final Logger LOGGER = Logger.getLogger(ObjectMedianBuilder.class);

    public int maxDepth = 15;
    public int minChildren = 4;

    public AbstractNode build(KDTree tree, BBox voxel) {
        return build(tree.getObjects(), tree.getBoundingBox(), 0);
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public AbstractNode build(List<GeometricObject> objects, BBox voxel, int depth) {

        Counter.count("KDtree.build");

        AbstractNode node = null; //new Leaf(objects);
        BBox voxelL = null;
        BBox voxelR = null;

        if (objects.size() < minChildren || depth >= maxDepth) {
            Counter.count("KDtree.build.leaf");
            node = new Leaf(objects);
            return node;
        }

        Counter.count("KDtree.build.node");

        Float split = null;
        Vector3DF width = voxel.q.minus(voxel.p);

        // Find the axis width the largest difference
        Axis axis = null;
        if (width.x() > width.y()) {
            if (width.x() > width.z()) {
                axis = Axis.X;
            } else {
                axis = Axis.Z;
            }
        } else {
            if (width.y() > width.z()) {
                axis = axis.Y;
            } else {
                axis = axis.Z;
            }
        }

        final Axis axis2 = axis;
        // Sort the objects by the current axis
        // final Axis axis = Axis.fromInt(depth % 3);
        Collections.sort(objects, new Comparator() {
            public int compare(Object o1, Object o2) {
                final GeometricObject oP = (GeometricObject) o1;
                final GeometricObject oQ = (GeometricObject) o2;
                final BBox bboxP = oP.getBoundingBox();
                final BBox bboxQ = oQ.getBoundingBox();
                final Point3DF p = bboxP.q;
                final Point3DF q = bboxQ.q;
                return Float.compare((float)p.ith(axis2), (float)q.ith(axis2));
            }
        });

        int size = objects.size();
        float minAxis = (float)objects.get(0).getBoundingBox().p.ith(axis);
        float maxAxis = (float)objects.get(objects.size()-1).getBoundingBox().p.ith(axis);
        float fwidth = maxAxis - minAxis;

        GeometricObject med = objects.get(size / 2);
        split = (float)med.getBoundingBox().p.ith(axis);

        List<GeometricObject> objectsL = new ArrayList<GeometricObject>();
        List<GeometricObject> objectsR = new ArrayList<GeometricObject>();

        if (axis2 == Axis.X) {
            // x
            for (GeometricObject object : objects) {
                BBox bbox = object.getBoundingBox();
                if (bbox.p.x() <= split) {
                    objectsL.add(object);
                }
                if (bbox.q.x() >= split) {
                    objectsR.add(object);
                }
            }

            BBox bL = BBox.create(objectsL);
            BBox bR = BBox.create(objectsR);

            Point3DF q1 = new Point3DF(split, bL.q.y(), bL.q.z());
            Point3DF p2 = new Point3DF(split, bR.p.y(), bR.p.z());

            voxelL = new BBox(bL.p, q1);
            voxelR = new BBox(p2, bR.q);
        } else if (axis2 == Axis.Y) {
            // y
            for (GeometricObject object : objects) {
                BBox bbox = object.getBoundingBox();
                if (bbox.p.y() <= split) {
                    objectsL.add(object);
                }
                if (bbox.q.y() >= split) {
                    objectsR.add(object);
                }
            }
            BBox bL = BBox.create(objectsL);
            BBox bR = BBox.create(objectsR);

            Point3DF q1 = new Point3DF(bL.q.x(), split, bL.q.z());
            Point3DF p2 = new Point3DF(bR.p.x(), split, bR.p.z());

            voxelL = new BBox(bL.p, q1);
            voxelR = new BBox(p2, bR.q);
        } else if (axis2 == Axis.Z) {
            // z
            for (GeometricObject object : objects) {
                BBox bbox = object.getBoundingBox();
                if (bbox.p.z() <= split) {
                    objectsL.add(object);
                }
                if (bbox.q.z() >= split) {
                    objectsR.add(object);
                }
            }

            BBox bL = BBox.create(objectsL);
            BBox bR = BBox.create(objectsR);

            Point3DF q1 = new Point3DF(bL.q.x(), bL.q.y(), split);
            Point3DF p2 = new Point3DF(bR.p.x(), bR.p.y(), split);

            voxelL = new BBox(bL.p, q1);
            voxelR = new BBox(p2, bR.q);
        }

        if (objects.size() == objectsL.size() || objects.size() == objectsR.size()) {
            LOGGER.info("Not splitting " + objects.size() + " objects into " + objectsL.size() + " and " + objectsR.size() + " objects at " + split + " with depth " + depth);
            node = new Leaf(objects);
        } else {
            LOGGER.info("Splitting " + axis2 + " " + objects.size() + " objects into " + objectsL.size() + " and " + objectsR.size() + " objects at " + split + " with depth " + depth + " and width " + width);
            AbstractNode left = build(objectsL, voxelL, depth + 1);
            AbstractNode right = build(objectsR, voxelR, depth + 1);

            node = new InnerNode(left, right, voxel, split, Axis.fromInt(depth % 3));
        }

        return node;
    }

    public int getMaxDepth() {
        return maxDepth;
    }
}
