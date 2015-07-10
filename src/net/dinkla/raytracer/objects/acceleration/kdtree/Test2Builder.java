package net.dinkla.raytracer.objects.acceleration.kdtree;

import net.dinkla.raytracer.math.Axis;
import net.dinkla.raytracer.math.BBox;
import net.dinkla.raytracer.objects.GeometricObject;
import net.dinkla.raytracer.objects.utilities.ListUtilities;
import net.dinkla.raytracer.utilities.Counter;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 16.06.2010
 * Time: 22:45:37
 * To change this template use File | Settings | File Templates.
 */
public class Test2Builder implements IKDTreeBuilder {

    static final Logger LOGGER = Logger.getLogger(TestBuilder.class);

    public int maxDepth = 15;
    public int minChildren = 4;

    public AbstractNode build(KDTree tree, BBox voxel) {
        return build(tree.getObjects(), tree.getBoundingBox(), 0);
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    static public class Partitioner {

        Triple root;
        
        public Set<Float> candidatesX;
        public Set<Float> candidatesY;
        public Set<Float> candidatesZ;

        public Partitioner(List<GeometricObject> objects, BBox voxel) {
            root = new Triple();
            root.objects = objects;
            root.bbox = voxel;
            root.update();

            candidatesX = new TreeSet<Float>();
            candidatesY = new TreeSet<Float>();
            candidatesZ = new TreeSet<Float>();

            for (GeometricObject object : objects) {
                BBox bbox = object.getBoundingBox();
                BBox clipped = bbox.clipTo(voxel);
                candidatesX.add(clipped.p.x());
                candidatesY.add(clipped.p.y());
                candidatesX.add(clipped.p.z());
                candidatesX.add(clipped.q.x());
                candidatesY.add(clipped.q.y());
                candidatesX.add(clipped.q.z());
            }
        }

        static public class Triple {

            public BBox bbox;
            List<GeometricObject> objects;
            float volume;

            public Triple() {
                objects = new ArrayList<GeometricObject>();
            }

            public void update() {
                //bbox = BBox.create(objects);
                volume = bbox.getVolume();
            }
        }

        static public class Split {
            static public final float constF = 0.333334f;

            public Axis axis;
            public float split;

            public Triple parent;
            public Triple left;
            public Triple right;

            public float sah;

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

            public float calcSah() {
                final float fL = left.volume / parent.volume;
                final float fR = right.volume / parent.volume;
                final float sL = left.objects.size();
                final float sR = right.objects.size(); 
//                return (constF + fL * sL + fR * sR);
                return (constF + fL * sL + fR * sR) * (5 * (sL+sR) / parent.objects.size());
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

        public static Split calcSplit(final Axis axis, final float split, final Triple parent) {
            Split s = new Split(parent);
            s.axis = axis;
            s.split = split;
            ListUtilities.splitByAxis(parent.objects, split, axis, s.left.objects, s.right.objects);

            s.left.bbox = parent.bbox.splitLeft(axis, split);
            s.right.bbox = parent.bbox.splitRight(axis, split);
            s.update();
            return s;
        }

        public Split x(final Axis axis, final Set<Float> cs) {
            Split min = null;
            for (Float split : cs) {
                if ((float)root.bbox.p.ith(axis) <= split && split <= (float) root.bbox.q.ith(axis)) {
                    Split s = calcSplit(axis, split, root);
                    if (s.isOk() && (null == min || s.sah < min.sah )) {
//                    LOGGER.info("Split: axis=" + axis + ", split=" + split + ", sah=" + s.sah + ", left=" + s.left.objects.size() + ", right=" + s.right.objects.size() + ", min=" + (null == min ? -1 : min.sah) );
                        min = s;
                    }
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
        } else if (x != null && y == null) {
            return true;
        } else {
            return false;
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

        Partitioner par = new Partitioner(objects, voxel);

        Partitioner.Split sX = par.x(Axis.X, par.candidatesX);
        Partitioner.Split sY = par.x(Axis.Y, par.candidatesY);
        Partitioner.Split sZ = par.x(Axis.Z, par.candidatesZ);

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

    public int getMaxDepth() {
        return maxDepth;
    }
}


