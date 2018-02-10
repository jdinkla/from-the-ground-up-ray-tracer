package net.dinkla.raytracer.objects.acceleration.kdtree;

import net.dinkla.raytracer.math.Axis;
import net.dinkla.raytracer.math.BBox;
import net.dinkla.raytracer.math.Point3D;
import net.dinkla.raytracer.objects.GeometricObject;
import net.dinkla.raytracer.objects.utilities.ListUtilities;
import net.dinkla.raytracer.utilities.Counter;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 16.06.2010
 * Time: 20:36:46
 * To change this template use File | Settings | File Templates.
 */
public class TestBuilder implements IKDTreeBuilder {

    static final Logger LOGGER = Logger.getLogger(TestBuilder.class);

    public int maxDepth = 30;
    public int minChildren = 4;

    public AbstractNode build(KDTree tree, BBox voxel) {
        return build(tree.getObjects(), tree.getBoundingBox(), 0);
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    static public class Partitioner {

        Triple root;
        
        public Partitioner(List<GeometricObject> objects, BBox voxel) {
            root = new Triple();
            root.objects = objects;
            root.bbox = voxel;
            root.update();            
        }

        static public class Triple {

            public BBox bbox;
            List<GeometricObject> objects;
            double volume;

            public Triple() {
                objects = new ArrayList<GeometricObject>(); 
            }

            public void update() {
                bbox = BBox.Companion.create(objects);
                volume = bbox.getVolume();
            }
        }

        static public class Split {
            static public final float constF = 0.333334f;

            public Axis axis;
            public double split;
            
            public Triple parent;
            public Triple left;
            public Triple right;

            public double sah;

            public Split(final Triple parent) {
                this.parent = parent;
                left = new Triple();
                right = new Triple();
            }

            public void update() {
                left.update();
                right.update();
                sah = calcSah();
            }

            public double calcSah() {
                return constF
                        + (left.volume / parent.volume) * left.objects.size()
                        + (right.volume / parent.volume) * right.objects.size();
            }

            public boolean isOk() {
                boolean b1 = parent.objects.size() <= left.objects.size();
                boolean b2 = parent.objects.size() <= right.objects.size();
                return !(b1 || b2);
            }

            static public Split max() {
                Split s = new Split(null);
                s.sah = Float.POSITIVE_INFINITY;
                return s;
            }
        }

        public static Split calcSplit(final Axis axis, final double split, final Triple parent) {
            Split s = new Split(parent);
            s.axis = axis;
            s.split = split;
            ListUtilities.splitByAxis(parent.objects, split, axis, s.left.objects, s.right.objects);
            s.update();
            return s;
        }

        public Split x(final Axis axis, final int num) {
            Split min = null;
            double width = root.bbox.getQ().ith(axis) - root.bbox.getP().ith(axis);
            // divide interval in num parts
            final double step = width / (num + 1);
            for (int i=1; i<num;i++) {
                double split = root.bbox.getP().ith(axis) + i * step;
                Split s = calcSplit(axis, split, root);
                if (s.isOk() && (null == min || s.sah < min.sah )) {
//                    LOGGER.info("Split: axis=" + axis + ", split=" + split + ", sah=" + s.sah + ", left=" + s.left.objects.size() + ", right=" + s.right.objects.size() + ", min=" + (null == min ? -1 : min.sah) );
                    min = s;
                }
            }
            return min;
        }

        public boolean isFound() {
            return true;
        }
    }

    static public boolean isLess(Partitioner.Split x, Partitioner.Split y) {
        if (x != null && y != null) {
            return x.sah < y.sah;
        } else {
            return false;
        }
    }

    public AbstractNode build(List<GeometricObject> objects, BBox voxel, int depth) {

        Counter.Companion.count("KDtree.build");

        AbstractNode node = null;

        if (objects.size() < minChildren || depth >= maxDepth) {
            Counter.Companion.count("KDtree.build.leaf");
            node = new Leaf(objects);
            return node;
        }

        Counter.Companion.count("KDtree.build.node");

        Partitioner par = new Partitioner(objects, voxel);

        Partitioner.Split sX = par.x(Axis.X, 3);
        Partitioner.Split sY = par.x(Axis.Y, 3);
        Partitioner.Split sZ = par.x(Axis.Z, 3);

        Partitioner.Split split = null;

        if (isLess(sX, sY)) {
            if (isLess(sX, sZ)) {
                split = sX;
            } else {
                split = sZ;
            }
        } else {
            if (isLess(sY, sZ)) {
                split = sY;
            } else {
                split = sZ;
            }
        }
        if (null == split) {
            LOGGER.info("Not splitting " + objects.size() + " objects with depth " + depth);
            node = new Leaf(objects);
        } else {
            assert null != split;
            assert null != objects;
            assert null != split.left.objects;
            assert null != split.right.objects;

            LOGGER.info("Splitting " + split.axis + " " + objects.size() + " objects into " + split.left.objects.size() + " and " + split.right.objects.size() + " objects at " + split.split + " with depth " + depth);
            AbstractNode left = build(split.left.objects, split.left.bbox, depth + 1);
            AbstractNode right = build(split.right.objects, split.right.bbox, depth + 1);
            node = new InnerNode(left, right, voxel, split.split, split.axis);
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

