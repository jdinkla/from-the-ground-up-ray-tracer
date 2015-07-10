package net.dinkla.raytracer.objects.acceleration.kdtree;

import net.dinkla.raytracer.math.Axis;
import net.dinkla.raytracer.math.BBox;
import net.dinkla.raytracer.math.Point3DF;
import net.dinkla.raytracer.objects.GeometricObject;
import net.dinkla.raytracer.objects.utilities.ListUtilities;
import net.dinkla.raytracer.utilities.Counter;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 15.06.2010
 * Time: 20:40:44
 * To change this template use File | Settings | File Templates.
 */
public class ObjectMedian2Builder implements IKDTreeBuilder {

    static final Logger LOGGER = Logger.getLogger(ObjectMedian2Builder.class);

    public int maxDepth = 15;
    public int minChildren = 4;

    public AbstractNode build(KDTree tree, BBox voxel) {
        return build(tree.getObjects(), tree.getBoundingBox(), 0);
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    static public class Partitioner {

        List<GeometricObject> objects;

        List<GeometricObject> objectsL;
        List<GeometricObject> objectsR;
        
        List<GeometricObject> objectsLx;
        List<GeometricObject> objectsRx;

        List<GeometricObject> objectsLy;
        List<GeometricObject> objectsRy;

        List<GeometricObject> objectsLz;
        List<GeometricObject> objectsRz;

        Axis axis;
        int size;

        Float split = null;
        
        Float splitX;
        Float splitY;
        Float splitZ;

        BBox voxelL = null;
        BBox voxelR = null;

        public Partitioner(List<GeometricObject> objects) {
            this.objects = objects;
            size = objects.size();

            objectsL = new ArrayList<GeometricObject>();
            objectsR = new ArrayList<GeometricObject>();

            objectsLx = new ArrayList<GeometricObject>();
            objectsRx = new ArrayList<GeometricObject>();

            objectsLy = new ArrayList<GeometricObject>();
            objectsRy = new ArrayList<GeometricObject>();

            objectsLz = new ArrayList<GeometricObject>();
            objectsRz = new ArrayList<GeometricObject>();

        }

        // TODO die sortierten merken
        public void split(int medianIndex) {
            // --------------- X ---------------
            axis = Axis.X;
            ListUtilities.sortByAxis(objects, axis);
            GeometricObject median = objects.get(medianIndex);
            splitX = (float)median.getBoundingBox().q.ith(axis);
            ListUtilities.splitByAxis(objects, splitX, axis, objectsLx, objectsRx);
            int weightX = weight(objectsLx.size(), objectsRx.size(), size);

            // --------------- Y ---------------
            axis = Axis.Y;
            ListUtilities.sortByAxis(objects, axis);
            median = objects.get(medianIndex);
            splitY = (float)median.getBoundingBox().q.ith(axis);
            ListUtilities.splitByAxis(objects, splitY, axis, objectsLy, objectsRy);
            int weightY = weight(objectsLy.size(), objectsRy.size(), size);

            // --------------- Z ---------------
            axis = Axis.Z;
            ListUtilities.sortByAxis(objects, axis);
            median = objects.get(medianIndex);
            splitZ = (float)median.getBoundingBox().q.ith(axis);
            ListUtilities.splitByAxis(objects, splitZ, axis, objectsLz, objectsRz);
            int weightZ = weight(objectsLz.size(), objectsRz.size(), size);

            LOGGER.info("weightX=" + weightX + " (" + objectsLx.size() + ", " + objectsRx.size()
                   + "), weightY=" + weightY + " (" + objectsLy.size() + ", " + objectsRy.size()
                   + "), weightZ=" + weightZ + " (" + objectsLz.size() + ", " + objectsRz.size() + ")"
                    );

            if (weightX < weightY) {
                if (weightX < weightZ) {
                    axis = Axis.X;
                } else {
                    axis = Axis.Z;
                }
            } else {
                if (weightY < weightZ) {
                    axis = Axis.Y;
                } else {
                    axis = Axis.Z;
                }
            }
        }

        public void select() {
            if (axis == Axis.X) {
                // x
                BBox bL = BBox.create(objectsLx);
                BBox bR = BBox.create(objectsRx);

                Point3DF q1x = new Point3DF(splitX, bL.q.getY(), bL.q.getZ());
                Point3DF p2x = new Point3DF(splitX, bR.p.getY(), bR.p.getZ());

                voxelL = new BBox(bL.p, q1x);
                voxelR = new BBox(p2x, bR.q);

                objectsL = objectsLx;
                objectsR = objectsRx;

                split = splitX;
            } else if (axis == Axis.Y) {
                // y
                BBox bL = BBox.create(objectsLy);
                BBox bR = BBox.create(objectsRy);

                Point3DF q1 = new Point3DF(bL.q.getX(), splitY, bL.q.getZ());
                Point3DF p2 = new Point3DF(bR.p.getX(), splitY, bR.p.getZ());

                voxelL = new BBox(bL.p, q1);
                voxelR = new BBox(p2, bR.q);

                objectsL = objectsLy;
                objectsR = objectsRy;

                split = splitY;
            } else if (axis == Axis.Z) {
                // z
                BBox bL = BBox.create(objectsLz);
                BBox bR = BBox.create(objectsRz);

                Point3DF q1 = new Point3DF(bL.q.getX(), bL.q.getY(), splitZ);
                Point3DF p2 = new Point3DF(bR.p.getX(), bR.p.getY(), splitZ);

                voxelL = new BBox(bL.p, q1);
                voxelR = new BBox(p2, bR.q);

                objectsL = objectsLz;
                objectsR = objectsRz;

                split = splitZ;
            }
        }

        public boolean isFound() {
            boolean b1 = objects.size() == objectsL.size() || objects.size() == objectsR.size();
            return !b1;
        }        
    }

    public AbstractNode build(List<GeometricObject> objects, BBox voxel, int depth) {

        Counter.count("KDtree.build");

        AbstractNode node = null;

        if (objects.size() < minChildren || depth >= maxDepth) {
            Counter.count("KDtree.build.leaf");
            node = new Leaf(objects);
            return node;
        }

        Counter.count("KDtree.build.node");

        int size = objects.size();

        Partitioner par = new Partitioner(objects);
        par.split(size / 2);
        par.select();
        int p = 10;
        if (!par.isFound()) {
            for (int i=0; i<p-1 && !par.isFound(); i++) {
                if (i!=p/2) {
                    par.split((int) (size * (i*1.0f/p)));
                    par.select();
                }
            }
            if (!par.isFound()) {
                LOGGER.info("Not splitting " + objects.size() + " objects into " + par.objectsL.size() + " and " + par.objectsR.size() + " objects at " + par.split + " with depth " + depth);
                node = new Leaf(objects);
            }
        }
        if (null == node) {
            LOGGER.info("Splitting " + par.axis + " " + objects.size() + " objects into " + par.objectsL.size() + " and " + par.objectsR.size() + " objects at " + par.split + " with depth " + depth);
            AbstractNode left = build(par.objectsL, par.voxelL, depth + 1);
            AbstractNode right = build(par.objectsR, par.voxelR, depth + 1);
            node = new InnerNode(left, right, voxel, par.split, Axis.fromInt(depth % 3));
        }

        return node;
    }

    static private int weight(int a, int b, int c) {
        return Math.abs(a-c/2) + Math.abs(b-c/2);
    }

    public int getMaxDepth() {
        return maxDepth;
    }
    
}
