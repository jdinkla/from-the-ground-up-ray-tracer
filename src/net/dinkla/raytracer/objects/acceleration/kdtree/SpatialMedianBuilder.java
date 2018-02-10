package net.dinkla.raytracer.objects.acceleration.kdtree;

import net.dinkla.raytracer.math.BBox;
import net.dinkla.raytracer.math.Point3D;
import net.dinkla.raytracer.math.Vector3D;
import net.dinkla.raytracer.objects.GeometricObject;
import net.dinkla.raytracer.math.Axis;
import net.dinkla.raytracer.utilities.Counter;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 11.06.2010
 * Time: 11:46:03
 * To change this template use File | Settings | File Templates.
 */
public class SpatialMedianBuilder implements IKDTreeBuilder {

    static final Logger LOGGER = Logger.getLogger(SpatialMedianBuilder.class);

    public int maxDepth = 15;
    public int minChildren = 4;

    public AbstractNode build(KDTree tree, BBox voxel) {
        return build(tree.getObjects(), tree.getBoundingBox(), 0);
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

        Vector3D half = voxel.getQ().minus(voxel.getP()).mult(0.5);
        Point3D mid = voxel.getP().plus(half);

        Double split = null;

        List<GeometricObject> objectsL = new ArrayList<GeometricObject>();
        List<GeometricObject> objectsR = new ArrayList<GeometricObject>();

        if (depth % 3 == 0) {
            // x
            split = mid.getX();

            Point3D q1 = new Point3D(mid.getX(), voxel.getQ().getY(), voxel.getQ().getZ());
            voxelL = new BBox(voxel.getP(), q1);

            Point3D p2 = new Point3D(mid.getX(), voxel.getP().getY(), voxel.getP().getZ());
            voxelR = new BBox(p2, voxel.getQ());

            for (GeometricObject object : objects) {
                BBox bbox = object.getBoundingBox();
                if (bbox.getP().getX() <= split) {
                    objectsL.add(object);
                }
                if (bbox.getQ().getX() >= split) {
                    objectsR.add(object);
                }
            }

        } else if (depth % 3 == 1) {
            // y
            split = mid.getY();

            Point3D q1 = new Point3D(voxel.getQ().getX(), mid.getY(), voxel.getQ().getZ());
            voxelL = new BBox(voxel.getP(), q1);

            Point3D p2 = new Point3D(voxel.getP().getX(), mid.getY(), voxel.getP().getZ());
            voxelR = new BBox(p2, voxel.getQ());

            for (GeometricObject object : objects) {
                BBox bbox = object.getBoundingBox();
                if (bbox.getP().getY() <= split) {
                    objectsL.add(object);
                }
                if (bbox.getQ().getY() >= split) {
                    objectsR.add(object);
                }
            }
        } else if (depth % 3 == 2) {
            // z
            split = mid.getZ();

            Point3D q1 = new Point3D(voxel.getQ().getX(), voxel.getQ().getY(), mid.getZ());
            voxelL = new BBox(voxel.getP(), q1);

            Point3D p2 = new Point3D(voxel.getP().getX(), voxel.getP().getY(), mid.getZ());
            voxelR = new BBox(p2, voxel.getQ());

            for (GeometricObject object : objects) {
                BBox bbox = object.getBoundingBox();
                if (bbox.getP().getZ() <= split) {
                    objectsL.add(object);
                }
                if (bbox.getQ().getZ() >= split) {
                    objectsR.add(object);
                }
            }
        }

        LOGGER.info("Splitting " + objects.size() + " objects into " + objectsL.size() + " and " + objectsR.size() + " objects at " + split + " with depth " + depth);
        AbstractNode left = build(objectsL, voxelL, depth + 1);
        AbstractNode right = build(objectsR, voxelR, depth + 1);

        node = new InnerNode(left, right, voxel, split, Axis.Companion.fromInt(depth % 3));

        return node;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }
}
