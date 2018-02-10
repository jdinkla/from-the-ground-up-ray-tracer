package net.dinkla.raytracer.objects.acceleration.kdtree;

import net.dinkla.raytracer.math.Axis;
import net.dinkla.raytracer.math.BBox;
import net.dinkla.raytracer.math.Point3D;
import net.dinkla.raytracer.math.Vector3D;
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

        Counter.Companion.count("KDtree.build");

        AbstractNode node = null; //new Leaf(objects);
        BBox voxelL = null;
        BBox voxelR = null;

        if (objects.size() < minChildren || depth >= maxDepth) {
            Counter.Companion.count("KDtree.build.leaf");
            node = new Leaf(objects);
            return node;
        }

        Counter.Companion.count("KDtree.build.node");

        Double split = null;
        Vector3D width = voxel.getQ().minus(voxel.getP());

        // Find the axis width the largest difference
        Axis axis = null;
        if (width.getX() > width.getY()) {
            if (width.getX() > width.getZ()) {
                axis = Axis.X;
            } else {
                axis = Axis.Z;
            }
        } else {
            if (width.getY() > width.getZ()) {
                axis = Axis.Y;
            } else {
                axis = Axis.Z;
            }
        }

        final Axis axis2 = axis;
        // Sort the objects by the current axis
        // final Axis axis = Axis.fromInt(depth % 3);
        objects.sort((o1, o2) -> {
            final GeometricObject oP = (GeometricObject) o1;
            final GeometricObject oQ = (GeometricObject) o2;
            final BBox bboxP = oP.getBoundingBox();
            final BBox bboxQ = oQ.getBoundingBox();
            final Point3D p = bboxP.getQ();
            final Point3D q = bboxQ.getQ();
            return Double.compare(p.ith(axis2), q.ith(axis2));
        });

        int size = objects.size();
        double minAxis = objects.get(0).getBoundingBox().getP().ith(axis);
        double maxAxis = objects.get(objects.size() - 1).getBoundingBox().getP().ith(axis);
        double fwidth = maxAxis - minAxis;

        GeometricObject med = objects.get(size / 2);
        split = med.getBoundingBox().getP().ith(axis);

        List<GeometricObject> objectsL = new ArrayList<GeometricObject>();
        List<GeometricObject> objectsR = new ArrayList<GeometricObject>();

        if (axis2 == Axis.X) {
            // x
            for (GeometricObject object : objects) {
                BBox bbox = object.getBoundingBox();
                if (bbox.getP().getX() <= split) {
                    objectsL.add(object);
                }
                if (bbox.getQ().getX() >= split) {
                    objectsR.add(object);
                }
            }

            BBox bL = BBox.Companion.create(objectsL);
            BBox bR = BBox.Companion.create(objectsR);

            Point3D q1 = new Point3D(split, bL.getQ().getY(), bL.getQ().getZ());
            Point3D p2 = new Point3D(split, bR.getP().getY(), bR.getP().getZ());

            voxelL = new BBox(bL.getP(), q1);
            voxelR = new BBox(p2, bR.getQ());
        } else if (axis2 == Axis.Y) {
            // y
            for (GeometricObject object : objects) {
                BBox bbox = object.getBoundingBox();
                if (bbox.getP().getY() <= split) {
                    objectsL.add(object);
                }
                if (bbox.getQ().getY() >= split) {
                    objectsR.add(object);
                }
            }
            BBox bL = BBox.Companion.create(objectsL);
            BBox bR = BBox.Companion.create(objectsR);

            Point3D q1 = new Point3D(bL.getQ().getX(), split, bL.getQ().getZ());
            Point3D p2 = new Point3D(bR.getP().getX(), split, bR.getP().getZ());

            voxelL = new BBox(bL.getP(), q1);
            voxelR = new BBox(p2, bR.getQ());
        } else if (axis2 == Axis.Z) {
            // z
            for (GeometricObject object : objects) {
                BBox bbox = object.getBoundingBox();
                if (bbox.getP().getZ() <= split) {
                    objectsL.add(object);
                }
                if (bbox.getQ().getZ() >= split) {
                    objectsR.add(object);
                }
            }

            BBox bL = BBox.Companion.create(objectsL);
            BBox bR = BBox.Companion.create(objectsR);

            Point3D q1 = new Point3D(bL.getQ().getX(), bL.getQ().getY(), split);
            Point3D p2 = new Point3D(bR.getP().getX(), bR.getP().getY(), split);

            voxelL = new BBox(bL.getP(), q1);
            voxelR = new BBox(p2, bR.getQ());
        }

        if (objects.size() == objectsL.size() || objects.size() == objectsR.size()) {
            LOGGER.info("Not splitting " + objects.size() + " objects into " + objectsL.size() + " and " + objectsR.size() + " objects at " + split + " with depth " + depth);
            node = new Leaf(objects);
        } else {
            LOGGER.info("Splitting " + axis2 + " " + objects.size() + " objects into " + objectsL.size() + " and " + objectsR.size() + " objects at " + split + " with depth " + depth + " and width " + width);
            AbstractNode left = build(objectsL, voxelL, depth + 1);
            AbstractNode right = build(objectsR, voxelR, depth + 1);

            node = new InnerNode(left, right, voxel, split, Axis.Companion.fromInt(depth % 3));
        }

        return node;
    }

    public int getMaxDepth() {
        return maxDepth;
    }
}
