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

        Vector3D half = voxel.q.minus(voxel.p).mult(0.5f);
        Point3D mid = voxel.p.plus(half);

        Float split = null;

        List<GeometricObject> objectsL = new ArrayList<GeometricObject>();
        List<GeometricObject> objectsR = new ArrayList<GeometricObject>();

        if (depth % 3 == 0) {
            // x
            split = mid.x;

            Point3D q1 = new Point3D(mid.x, voxel.q.y, voxel.q.z);
            voxelL = new BBox(voxel.p, q1);

            Point3D p2 = new Point3D(mid.x, voxel.p.y, voxel.p.z);
            voxelR = new BBox(p2, voxel.q);

            for (GeometricObject object : objects) {
                BBox bbox = object.getBoundingBox();
                if (bbox.p.x <= split) {
                    objectsL.add(object);
                }
                if (bbox.q.x >= split) {
                    objectsR.add(object);
                }
            }

        } else if (depth % 3 == 1) {
            // y
            split = mid.y;

            Point3D q1 = new Point3D(voxel.q.x, mid.y, voxel.q.z);
            voxelL = new BBox(voxel.p, q1);

            Point3D p2 = new Point3D(voxel.p.x, mid.y, voxel.p.z);
            voxelR = new BBox(p2, voxel.q);

            for (GeometricObject object : objects) {
                BBox bbox = object.getBoundingBox();
                if (bbox.p.y <= split) {
                    objectsL.add(object);
                }
                if (bbox.q.y >= split) {
                    objectsR.add(object);
                }
            }
        } else if (depth % 3 == 2) {
            // z
            split = mid.z;

            Point3D q1 = new Point3D(voxel.q.x, voxel.q.y, mid.z);
            voxelL = new BBox(voxel.p, q1);

            Point3D p2 = new Point3D(voxel.p.x, voxel.p.y, mid.z);
            voxelR = new BBox(p2, voxel.q);

            for (GeometricObject object : objects) {
                BBox bbox = object.getBoundingBox();
                if (bbox.p.z <= split) {
                    objectsL.add(object);
                }
                if (bbox.q.z >= split) {
                    objectsR.add(object);
                }
            }
        }

        LOGGER.info("Splitting " + objects.size() + " objects into " + objectsL.size() + " and " + objectsR.size() + " objects at " + split + " with depth " + depth);
        AbstractNode left = build(objectsL, voxelL, depth + 1);
        AbstractNode right = build(objectsR, voxelR, depth + 1);

        node = new InnerNode(left, right, voxel, split, Axis.fromInt(depth % 3));

        return node;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }
}
