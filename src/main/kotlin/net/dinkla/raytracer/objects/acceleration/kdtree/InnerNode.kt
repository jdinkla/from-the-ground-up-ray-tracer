package net.dinkla.raytracer.objects.acceleration.kdtree

import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.math.*
import net.dinkla.raytracer.utilities.Counter

import java.util.Stack

class InnerNode(val left: AbstractNode?, val right: AbstractNode?, override val boundingBox: BBox, val split: Double, protected val axis: Axis) : AbstractNode() {

    init {
        assert(null != left)
        assert(null != right)
    }

    open class Pair(var node: AbstractNode?, var hit: Hit)

    //@Override
    fun hitNR(ray: Ray, sr: Hit): Boolean {
        val stack = Stack<Pair>()
        stack.push(Pair(this, sr))

        var count = 0

        while (!stack.isEmpty()) {
            val pair = stack.pop()
            count++
            if (pair.node is Leaf) {
                val leaf = pair.node as Leaf
                val sr2 = Hit(pair.hit)
                val isHit = leaf.hit(ray, sr2)
                //                if (isHit && sr2.getT() < sr.getT()) {
                if (isHit) {
                    sr.set(sr2)
                    //                    System.out.println("hits count=" + count);
                    hits.add(count)
                    return true
                }
            } else {
                val node = pair.node as InnerNode
                val bbox = node.boundingBox
                val hit = bbox.hitX(ray)
                if (hit.isHit) {
                    val srL = Hit(pair.hit)
                    srL.t = hit.t1
                    val srR = Hit(pair.hit)
                    if (ray.origin.ith(axis) < node.split) {
                        stack.push(Pair(node.left, srL))
                        stack.push(Pair(node.right, srR))
                    } else {
                        stack.push(Pair(node.right, srR))
                        stack.push(Pair(node.left, srL))
                    }
                }
            }
        }
        //        System.out.println("fails count=" + count);
        fails.add(count)
        return false
    }

    enum class Side {
        Left, Right
    }

    override fun hit(ray: Ray, sr: Hit): Boolean {
        return hitRec(ray, sr)
    }

    fun hitRec(ray: Ray, sr: Hit): Boolean {
        val hit = boundingBox.hitX(ray)
        if (!hit.isHit) {
            //            Counter.count("KDTree.InnerNode.hit.bbox");
            return false
        }
        //        Counter.count("KDTree.InnerNode.hit");

        // On which side does the ray start?
        if (ray.origin.ith(axis) < split) {
            //                Counter.count("KDTree.InnerNode.hit.LR.L");
            val srL = Hit(sr)
            srL.t = hit.t1
            val isHitL = left?.hit(ray, srL)
            if (isHitL != null) {
                //                 Counter.count("KDTree.InnerNode.hit.H.L");
                sr.set(srL)
                return true
            }
            val srR = Hit(sr)
            srR.t = hit.t1
            val isHitR = right?.hit(ray, srR)
            if (isHitR != null) {
                //                    Counter.count("KDTree.InnerNode.hit.H.R");
                sr.set(srR)
                return true
            }
        } else {
            //                Counter.count("KDTree.InnerNode.hit.LR.R");
            val srR = Hit(sr)
            srR.t = hit.t1
            val isHitR = right?.hit(ray, srR)
            if (isHitR != null) {
                //                    Counter.count("KDTree.InnerNode.hit.H2.R");
                sr.set(srR)
                return true
            }
            val srL = Hit(sr)
            srL.t = hit.t1
            val isHitL = left?.hit(ray, srL)
            if (isHitL != null) {
                //                    Counter.count("KDTree.InnerNode.hit.H2.L");
                sr.set(srL)
                return true
            }
        }

        return false
    }

    fun hitXX(ray: Ray, sr: Hit): Boolean {
        if (!boundingBox.hit(ray)) {
            //            Counter.count("KDTree.InnerNode.hit.bbox");
            return false
        }
        //        Counter.count("KDTree.InnerNode.hit");

        // Side
        var side: Side? = null
        if (ray.origin.ith(axis) < split) {
            side = Side.Left
            //            Counter.count("KDTree.InnerNode.Side.L");
        } else {
            side = Side.Right
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
        //                p = new Plane(new Point3D(0, 0, split), Normal.FORWARD);
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
            val srL = Hit(sr)
            //                if (srPlane.getT() < sr.getT()) {
            //                    srL.setT(srPlane.getT());
            //                }
            val isHitL = left?.hit(ray, srL)
            if (isHitL != null && srL.t < sr.t) {
                //                    Counter.count("KDTree.InnerNode.hit.H.L");
                sr.set(srL)
                return true
            }
            val srR = Hit(sr)
            val isHitR = right?.hit(ray, srR)
            if (isHitR != null && srR.t < sr.t) {
                //                    Counter.count("KDTree.InnerNode.hit.H.R");
                sr.set(srR)
                return true
            }
        } else {
            //                Counter.count("KDTree.InnerNode.hit.LR.R");
            val srR = Hit(sr)
            val isHitR = right?.hit(ray, srR)
            if (isHitR != null && srR.t < sr.t) {
                //                    Counter.count("KDTree.InnerNode.hit.H2.R");
                sr.set(srR)
                return true
            }
            val srL = Hit(sr)
            val isHitL = left?.hit(ray, srL)
            if (isHitL != null && srL.t < sr.t) {
                //                    Counter.count("KDTree.InnerNode.hit.H2.L");
                sr.set(srL)
                return true
            }
        }
        //        }

        return false

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

    fun hitX(ray: Ray, sr: Hit): Boolean {
        if (!boundingBox.hit(ray)) {
            Counter.count("KDTree.InnerNode.hit.bbox")
            return false
        }
        Counter.count("KDTree.InnerNode.hit")
        val hitL = left?.boundingBox?.hitX(ray)
        val hitR = right?.boundingBox?.hitX(ray)
        if (hitL != null && hitL.isHit && hitR != null && !hitR.isHit) {
            Counter.count("KDTree.InnerNode.hit.L")
            return left?.hit(ray, sr) ?: false
        } else if (hitL != null && !hitL.isHit && hitR != null && hitR.isHit) {
            Counter.count("KDTree.InnerNode.hit.R")
            return right?.hit(ray, sr) ?: false
        } else if (hitL != null && hitL.isHit && hitR != null && hitR.isHit) {
            Counter.count("KDTree.InnerNode.hit.LR")
            if (hitL.t0 < hitR.t0) {
                var sr2 = Hit()
                sr2.t = sr.t
                val isHitL = left?.hit(ray, sr2)
                if (isHitL != null && null != sr2.normal) {
                    sr.set(sr2)
                    return true
                } else {
                    sr2 = Hit()
                    sr2.t = sr.t
                    val isHitR = right?.hit(ray, sr2)
                    if (isHitR != null && null != sr2.normal) {
                        sr.set(sr2)
                        return true
                    } else {
                        return false
                    }
                }
            } else {
                var sr2 = Hit()
                sr2.t = sr.t
                val isHitR = right?.hit(ray, sr2)
                if (isHitR != null && null != sr2.normal) {
                    sr.set(sr2)
                    return true
                } else {
                    sr2 = Hit()
                    sr2.t = sr.t
                    val isHitL = left?.hit(ray, sr2)
                    if (isHitL != null && null != sr2.normal) {
                        sr.set(sr2)
                        return true
                    } else {
                        return false
                    }
                }
            }
        } else {
            Counter.count("KDTree.InnerNode.hit.0")
            return false
        }
    }

    override fun size(): Int {
        return (left?.size() ?: 0) + (right?.size() ?: 0)
    }

    override fun toString(): String {
        return ("Node " + size() + " [" + left?.size() + "," + right?.size() + "] "
                + boundingBox.toString() + " " + split + "\n"
                + "(" + left?.toString() + ")\n(" + right?.toString() + ")")
    }


    override fun printBBoxes(incr: Int): String {
        val sb = StringBuilder()
        for (i in 0 until incr) {
            sb.append(" ")
        }
        sb.append(size())
        sb.append(" ")
        sb.append(axis)
        sb.append(" ")
        sb.append(split)
        sb.append(" ")
        sb.append(boundingBox.p)
        sb.append(" ")
        sb.append(boundingBox.q)
        sb.append("\n")
        sb.append(left?.printBBoxes(incr + 2))
        sb.append(right?.printBBoxes(incr + 2))
        return sb.toString()
    }

    companion object {

        var hits = Histogram()
        var fails = Histogram()
    }
}
