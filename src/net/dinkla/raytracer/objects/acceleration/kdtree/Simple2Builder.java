package net.dinkla.raytracer.objects.acceleration.kdtree;

import net.dinkla.raytracer.math.Axis;
import net.dinkla.raytracer.math.BBox;
import net.dinkla.raytracer.math.Point3D;
import net.dinkla.raytracer.math.Vector3D;
import net.dinkla.raytracer.objects.GeometricObject;
import net.dinkla.raytracer.utilities.Counter;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 11.06.2010
 * Time: 11:47:01
 * To change this template use File | Settings | File Templates.
 */
public class Simple2Builder implements IKDTreeBuilder {

    static final Logger LOGGER = Logger.getLogger(SpatialMedianBuilder.class);

    public int maxDepth = 10;

    public int minChildren = 4;
    
    public AbstractNode build(KDTree tree, BBox voxel) {
        return build(tree.getObjects(), tree.getBoundingBox(), 0);
    }

    /**
     * 
     * @param objects
     * @param voxel
     * @param depth
     * @return
     */
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

        Vector3D half = voxel.getQ().minus(voxel.getP()).mult(0.5);
        Point3D mid = voxel.getP().plus(half);

        Double split = null;

        List<GeometricObject> objectsL = new ArrayList<GeometricObject>();
        List<GeometricObject> objectsR = new ArrayList<GeometricObject>();

        BBox voxelLx = null;
        BBox voxelRx = null;

        BBox voxelLy = null;
        BBox voxelRy = null;

        BBox voxelLz = null;
        BBox voxelRz = null;

        List<GeometricObject> objectsLx = new ArrayList<GeometricObject>();
        List<GeometricObject> objectsRx = new ArrayList<GeometricObject>();
        List<GeometricObject> objectsLy = new ArrayList<GeometricObject>();
        List<GeometricObject> objectsRy = new ArrayList<GeometricObject>();
        List<GeometricObject> objectsLz = new ArrayList<GeometricObject>();
        List<GeometricObject> objectsRz = new ArrayList<GeometricObject>();

        split = mid.getX();

        Point3D q1 = new Point3D(mid.getX(), voxel.getQ().getY(), voxel.getQ().getZ());
        voxelLx = new BBox(voxel.getP(), q1);

        Point3D p2 = new Point3D(mid.getX(), voxel.getP().getY(), voxel.getP().getZ());
        voxelRx = new BBox(p2, voxel.getQ());

        int bothX = 0;
        int bothY = 0;
        int bothZ = 0;

        for (GeometricObject object : objects) {
            BBox bbox = object.getBoundingBox();
            boolean isBoth = false;
            if (bbox.getP().getX() <= split) {
                objectsLx.add(object);
                isBoth = true;
            }
            if (bbox.getQ().getX() >= split) {
                objectsRx.add(object);
                if (isBoth) {
                    bothX++;
                }
            }
        }

        split = mid.getY();

        Point3D q1y = new Point3D(voxel.getQ().getX(), mid.getY(), voxel.getQ().getZ());
        voxelLy = new BBox(voxel.getP(), q1y);

        Point3D p2y = new Point3D(voxel.getP().getX(), mid.getY(), voxel.getP().getZ());
        voxelRy = new BBox(p2y, voxel.getQ());

        for (GeometricObject object : objects) {
            BBox bbox = object.getBoundingBox();
            boolean isBoth = false;
            if (bbox.getP().getY() <= split) {
                objectsLy.add(object);
                isBoth = true;
            }
            if (bbox.getQ().getY() >= split) {
                objectsRy.add(object);
                if (isBoth) {
                    bothY++;
                }
            }
        }

        split = mid.getZ();

        Point3D q1z = new Point3D(voxel.getQ().getX(), voxel.getQ().getY(), mid.getZ());
        voxelLz = new BBox(voxel.getP(), q1z);

        Point3D p2z = new Point3D(voxel.getP().getX(), voxel.getP().getY(), mid.getZ());
        voxelRz = new BBox(p2z, voxel.getQ());

        for (GeometricObject object : objects) {
            BBox bbox = object.getBoundingBox();
            boolean isBoth = false;
            if (bbox.getP().getZ() <= split) {
                objectsLz.add(object);
                isBoth = true;
            }
            if (bbox.getQ().getZ() >= split) {
                objectsRz.add(object);
                if (isBoth) {
                    bothZ++;
                }
            }
        }

        int n = objects.size();
        
        int diffX = Math.abs(objectsLx.size() - objectsRx.size()) + bothX * 3 + (objectsLx.size() + objectsRx.size() - n) * 5;
        int diffY = Math.abs(objectsLy.size() - objectsRy.size()) + bothY * 3 + (objectsLy.size() + objectsRy.size() - n) * 5;
        int diffZ = Math.abs(objectsLz.size() - objectsRz.size()) + bothZ * 3 + (objectsLz.size() + objectsRz.size() - n) * 5;

        if (diffX < diffY) {
            if (diffX < diffZ) {
                objectsL = objectsLx;
                objectsR = objectsRx;
                voxelL = voxelLx;
                voxelR = voxelRx;
            } else {
                objectsL = objectsLz;
                objectsR = objectsRz;
                voxelL = voxelLz;
                voxelR = voxelRz;
            }
        } else {
            if (diffY < diffZ) {
                objectsL = objectsLy;
                objectsR = objectsRy;
                voxelL = voxelLy;
                voxelR = voxelRy;
            } else {
                objectsL = objectsLz;
                objectsR = objectsRz;
                voxelL = voxelLz;
                voxelR = voxelRz;
            }
        }

        if (objectsL.size() + objectsR.size() > n*1.5) {
            node = new Leaf(objects);
        } else {
            LOGGER.info("Splitting " + objects.size() + " objects into " + objectsL.size() + " and " + objectsR.size() + " objects at " + split + " with depth " + depth);
            AbstractNode left = build(objectsL, voxelL, depth + 1);
            AbstractNode right = build(objectsR, voxelR, depth + 1);

            node = new InnerNode(left, right, voxel, split, Axis.fromInt(depth % 3));
        }

        return node;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }
    
}
