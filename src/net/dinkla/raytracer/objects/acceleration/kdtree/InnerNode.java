package net.dinkla.raytracer.objects.acceleration.kdtree;

import net.dinkla.raytracer.hits.Hit;
import net.dinkla.raytracer.math.*;
import net.dinkla.raytracer.objects.Plane;
import net.dinkla.raytracer.utilities.Counter;

import java.util.Stack;

/**
* Created by IntelliJ IDEA.
* User: jorndinkla
* Date: 11.06.2010
* Time: 09:40:54
* To change this template use File | Settings | File Templates.
*/
public class InnerNode extends AbstractNode {

    static public Histogram hits = new Histogram();
    static public Histogram fails = new Histogram();
    
    protected final BBox bbox;
    protected final AbstractNode left;
    protected final AbstractNode right;
    protected final float split;
    protected final Axis axis;

    public InnerNode(AbstractNode left, AbstractNode right, BBox bbox, float split, Axis axis) {
        assert null != left;
        assert null != right;
        this.left = left;
        this.right = right;
        this.bbox = bbox;
        this.split = split;
        this.axis = axis;
    }

    public static class Pair {
        AbstractNode node;
        Hit hit;
        public Pair(AbstractNode node, Hit hit) {
            this.node = node;
            this.hit = hit;
        }
    }

    //@Override
    public boolean hitNR(final Ray ray, Hit sr) {
        Stack<Pair> stack = new Stack<Pair>();
        stack.push(new Pair(this, sr));

        int count = 0;

        while (!stack.isEmpty()) {
            Pair pair = stack.pop();
            count++;
            if (pair.node instanceof Leaf) {
                Leaf leaf = (Leaf) pair.node;
                Hit sr2 = new Hit(pair.hit);
                boolean isHit = leaf.hit(ray, sr2);
//                if (isHit && sr2.getT() < sr.getT()) {
                if (isHit) {
                    sr.set(sr2);
//                    System.out.println("hits count=" + count);
                    hits.add(count);
                    return true;
                }
            } else {
                InnerNode node = (InnerNode) pair.node;
                BBox bbox = node.getBoundingBox();
                BBox.Hit hit = bbox.hitX(ray);
                if (hit.isHit) {
                    Hit srL = new Hit(pair.hit);
                    srL.setT(hit.t1);
                    Hit srR = new Hit(pair.hit);
                    if (ray.o.ith(axis) < node.getSplit()) {
                        stack.push(new Pair(node.left, srL));
                        stack.push(new Pair(node.right, srR));
                    } else {
                        stack.push(new Pair(node.right, srR));
                        stack.push(new Pair(node.left, srL));
                    }
                }
            }
        }
//        System.out.println("fails count=" + count);
        fails.add(count);
        return false;
    }

    public enum Side {
        Left, Right
    }
    
    public boolean hit(Ray ray, Hit sr) {
        return hitRec(ray, sr);
    }

    public boolean hitRec(Ray ray, Hit sr) {
        BBox.Hit hit = bbox.hitX(ray);
        if (!hit.isHit) {
//            Counter.count("KDTree.InnerNode.hit.bbox");
            return false;
        }
//        Counter.count("KDTree.InnerNode.hit");

        // On which side does the ray start? 
        if (ray.o.ith(axis) < split) {
//                Counter.count("KDTree.InnerNode.hit.LR.L");
            Hit srL = new Hit(sr);
            srL.setT(hit.t1);
            boolean isHitL = left.hit(ray, srL);
            if (isHitL) {
//                 Counter.count("KDTree.InnerNode.hit.H.L");
                sr.set(srL);
                return true;
            }
            Hit srR = new Hit(sr);
            srR.setT(hit.t1);
            boolean isHitR = right.hit(ray, srR);
            if (isHitR) {
//                    Counter.count("KDTree.InnerNode.hit.H.R");
                sr.set(srR);
                return true;
            }
        } else {
//                Counter.count("KDTree.InnerNode.hit.LR.R");
            Hit srR = new Hit(sr);
            srR.setT(hit.t1);
            boolean isHitR = right.hit(ray, srR);
            if (isHitR) {
//                    Counter.count("KDTree.InnerNode.hit.H2.R");
                sr.set(srR);
                return true;
            }
            Hit srL = new Hit(sr);
            srL.setT(hit.t1);
            boolean isHitL = left.hit(ray, srL);
            if (isHitL) {
//                    Counter.count("KDTree.InnerNode.hit.H2.L");
                sr.set(srL);
                return true;
            }
        }

        return false;
    }

    public boolean hitXX(Ray ray, Hit sr) {
        if (!bbox.hit(ray)) {
//            Counter.count("KDTree.InnerNode.hit.bbox");
            return false;
        }
//        Counter.count("KDTree.InnerNode.hit");

        // Side
        Side side = null;
        if (ray.o.ith(axis) < split) {
            side = Side.Left;
//            Counter.count("KDTree.InnerNode.Side.L");
        } else {
            side = Side.Right;
//            Counter.count("KDTree.InnerNode.Side.R");
        }

        // Does ray hit split plane?
//        Plane p = null;
//        switch (axis) {
//            case X:
//                p = new Plane(new Point3D(split, 0, 0), Normal.RIGHT);
//                break;
//            case Y:
//                p = new Plane(new Point3D(0, split, 0), Normal.UP);
//                break;
//            case Z:
//                p = new Plane(new Point3D(0, 0, split), Normal.FRONT);
//                break;
//        }
//
//        Hit srPlane = new Hit();
//        boolean hits = p.hit(ray, srPlane);

//        if (!hits) {
//            return false;
//            Counter.count("KDTree.InnerNode.Plane.0");
//            if (side == Side.Left) {
//                Hit srL = new Hit(sr);
//                boolean isHitL = left.hit(ray, srL);
//                if (isHitL && srL.getT() < sr.getT()) {
//                    Counter.count("KDTree.InnerNode.hit.L");
//                    sr.set(srL);
//                    return true;
//                }
//            } else {
//                Hit srR = new Hit(sr);
//                boolean isHitR = left.hit(ray, srR);
//                if (isHitR && srR.getT() < sr.getT()) {
//                    Counter.count("KDTree.InnerNode.hit.R");
//                    sr.set(srR);
//                    return true;
//                }
//            }
//        } else {
//            Counter.count("KDTree.InnerNode.Plane.Hits");
            if (side == Side.Left) {
//                Counter.count("KDTree.InnerNode.hit.LR.L");
                Hit srL = new Hit(sr);
//                if (srPlane.getT() < sr.getT()) {
//                    srL.setT(srPlane.getT());
//                }
                boolean isHitL = left.hit(ray, srL);
                if (isHitL && srL.getT() < sr.getT()) {
//                    Counter.count("KDTree.InnerNode.hit.H.L");
                    sr.set(srL);
                    return true;
                }
                Hit srR = new Hit(sr);
                boolean isHitR = right.hit(ray, srR);
                if (isHitR && srR.getT() < sr.getT()) {
//                    Counter.count("KDTree.InnerNode.hit.H.R");
                    sr.set(srR);
                    return true;
                }
            } else {
//                Counter.count("KDTree.InnerNode.hit.LR.R");
                Hit srR = new Hit(sr);
                boolean isHitR = right.hit(ray, srR);
                if (isHitR && srR.getT() < sr.getT()) {
//                    Counter.count("KDTree.InnerNode.hit.H2.R");
                    sr.set(srR);
                    return true;
                }
                Hit srL = new Hit(sr);
                boolean isHitL = left.hit(ray, srL);
                if (isHitL && srL.getT() < sr.getT()) {
//                    Counter.count("KDTree.InnerNode.hit.H2.L");
                    sr.set(srL);
                    return true;
                }
            }
//        }

        return false;

//        // prev
//        Hit srL = new Hit(sr);
//        boolean isHitL = left.hit(ray, srL);
//
////        if (isHitL) {
////            assert null != srL.getNormal();
////        }
//
//        Hit srR = new Hit(sr);
//        boolean isHitR = right.hit(ray, srR);
//
////        if (isHitR) {
////            assert null != srR.getNormal();
////        }
//
//        if (isHitL && isHitR) {
//            if (srL.getT() < srR.getT()) {
//                Counter.count("KDTree.InnerNode.hit.LR.L");
//                sr.set(srL);
//                return true;
//            } else {
//                Counter.count("KDTree.InnerNode.hit.LR.R");
//                sr.set(srR);
//                return true;
//            }
//        } else if (!isHitL && isHitR && srR.getT() < sr.getT()) {
//            Counter.count("KDTree.InnerNode.hit.R");
//            sr.set(srR);
//            return true;
//        } else if (isHitL && !isHitR && srL.getT() < sr.getT()) {
//            Counter.count("KDTree.InnerNode.hit.L");
//            sr.set(srL);
//            return true;
//        } else {
//            Counter.count("KDTree.InnerNode.hit.0");
//            return false;
//        }
    }

    public boolean hitX(Ray ray, Hit sr) {
        if (!bbox.hit(ray)) {
            Counter.count("KDTree.InnerNode.hit.bbox");
            return false;
        }
        Counter.count("KDTree.InnerNode.hit");
        BBox.Hit hitL = left.getBoundingBox().hitX(ray);
        BBox.Hit hitR = right.getBoundingBox().hitX(ray);
        if (hitL.isHit && (!hitR.isHit)) {
            Counter.count("KDTree.InnerNode.hit.L");
            return left.hit(ray, sr);
        } else if ((!hitL.isHit) && hitR.isHit) {
            Counter.count("KDTree.InnerNode.hit.R");
            return right.hit(ray, sr);
        } else if (hitL.isHit && hitR.isHit) {
            Counter.count("KDTree.InnerNode.hit.LR");
            if (hitL.t0 < hitR.t0) {
                Hit sr2 = new Hit();
                sr2.setT(sr.getT());
                boolean isHitL = left.hit(ray, sr2);
                if (isHitL && null != sr2.getNormal()) {
                    sr.set(sr2);
                    return true;
                } else {
                    sr2 = new Hit();
                    sr2.setT(sr.getT());
                    boolean isHitR = right.hit(ray, sr2);
                    if (isHitR && null != sr2.getNormal()) {
                        sr.set(sr2);
                        return true;
                    } else {
                        return false;
                    }
                }
            } else {
                Hit sr2 = new Hit();
                sr2.setT(sr.getT());
                boolean isHitR = right.hit(ray, sr2);
                if (isHitR && null != sr2.getNormal()) {
                    sr.set(sr2);
                    return true;
                } else {
                    sr2 = new Hit();
                    sr2.setT(sr.getT());
                    boolean isHitL = left.hit(ray, sr2);
                    if (isHitL && null != sr2.getNormal()) {
                        sr.set(sr2);
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        } else {
            Counter.count("KDTree.InnerNode.hit.0");
            return false;
        }
    }

    @Override
    public BBox getBoundingBox() {
        return bbox;
    }

    public float getSplit() {
        return split;
    }

    @Override
    public int size() {
        return left.size() + right.size();
    }

    @Override
    public String toString() {
        return "Node " + size() + " [" + left.size() + "," + right.size() + "] "
                + bbox.toString() + " " + split + "\n"
        + "(" + left.toString() + ")\n(" +  right.toString() + ")";
    }


    public String printBBoxes(int incr) {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<incr;i++) {
            sb.append(" ");
        }
        sb.append(size());
        sb.append(" ");
        sb.append(axis);
        sb.append(" ");
        sb.append(split);
        sb.append(" ");
        sb.append(bbox.p);
        sb.append(" ");
        sb.append(bbox.q);
        sb.append("\n");
        sb.append(left.printBBoxes(incr + 2));
        sb.append(right.printBBoxes(incr + 2));
        return sb.toString();
    }
}
