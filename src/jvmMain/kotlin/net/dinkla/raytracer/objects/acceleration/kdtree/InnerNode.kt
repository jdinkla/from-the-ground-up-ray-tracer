package net.dinkla.raytracer.objects.acceleration.kdtree

import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.math.Axis
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.utilities.Histogram

class InnerNode(
    val left: Node,
    val right: Node,
    override val boundingBox: BBox,
    val split: Double,
    private val axis: Axis
) : Node {

    enum class Side {
        Left, Right
    }

    override fun hit(ray: Ray, sr: Hit): Boolean = hitRec(ray, sr)

    private fun hitRec(ray: Ray, sr: Hit): Boolean {
        val hit = boundingBox.hitX(ray)
        if (!hit.isHit) {
            return false
        }

        // On which side does the ray start?
        if (ray.origin.ith(axis) < split) {
            val srL = Hit(sr)
            srL.t = hit.t1
            val isHitL = left.hit(ray, srL)
            if (isHitL) {
                sr.set(srL)
                return true
            }
            val srR = Hit(sr)
            srR.t = hit.t1
            val isHitR = right.hit(ray, srR)
            if (isHitR) {
                sr.set(srR)
                return true
            }
        } else {
            val srR = Hit(sr)
            srR.t = hit.t1
            val isHitR = right.hit(ray, srR)
            if (isHitR) {
                sr.set(srR)
                return true
            }
            val srL = Hit(sr)
            srL.t = hit.t1
            val isHitL = left.hit(ray, srL)
            if (isHitL) {
                sr.set(srL)
                return true
            }
        }
        return false
    }

//    fun hitXX(ray: Ray, sr: Hit): Boolean {
//        if (!boundingBox.hit(ray)) {
//            //            Counter.count("KDTree.InnerNode.hit.bbox");
//            return false
//        }
//        //        Counter.count("KDTree.InnerNode.hit");
//
//        // Side
//        var side: Side? = null
//        if (ray.origin.ith(axis) < split) {
//            side = Side.Left
//            //            Counter.count("KDTree.InnerNode.Side.L");
//        } else {
//            side = Side.Right
//            //            Counter.count("KDTree.InnerNode.Side.R");
//        }
//
//        // Does ray hit split plane?
//        //        Plane p = null;
//        //        switch (axis) {
//        //            case X:
//        //                p = new Plane(new Point3D(split, 0, 0), Normal.RIGHT);
//        //                break;
//        //            case Y:
//        //                p = new Plane(new Point3D(0, split, 0), Normal.UP);
//        //                break;
//        //            case Z:
//        //                p = new Plane(new Point3D(0, 0, split), Normal.FORWARD);
//        //                break;
//        //        }
//        //
//        //        Hit srPlane = new Hit();
//        //        boolean hits = p.hit(ray, srPlane);
//
//        //        if (!hits) {
//        //            return false;
//        //            Counter.count("KDTree.InnerNode.Plane.0");
//        //            if (side == Side.Left) {
//        //                Hit srL = new Hit(sr);
//        //                boolean isHitL = left.hit(ray, srL);
//        //                if (isHitL && srL.getT() < sr.getT()) {
//        //                    Counter.count("KDTree.InnerNode.hit.L");
//        //                    sr.set(srL);
//        //                    return true;
//        //                }
//        //            } else {
//        //                Hit srR = new Hit(sr);
//        //                boolean isHitR = left.hit(ray, srR);
//        //                if (isHitR && srR.getT() < sr.getT()) {
//        //                    Counter.count("KDTree.InnerNode.hit.R");
//        //                    sr.set(srR);
//        //                    return true;
//        //                }
//        //            }
//        //        } else {
//        //            Counter.count("KDTree.InnerNode.Plane.Hits");
//        if (side == Side.Left) {
//            //                Counter.count("KDTree.InnerNode.hit.LR.L");
//            val srL = Hit(sr)
//            //                if (srPlane.getT() < sr.getT()) {
//            //                    srL.setT(srPlane.getT());
//            //                }
//            val isHitL = left.hit(ray, srL)
//            if (isHitL != null && srL.t < sr.t) {
//                //                    Counter.count("KDTree.InnerNode.hit.H.L");
//                sr.set(srL)
//                return true
//            }
//            val srR = Hit(sr)
//            val isHitR = right.hit(ray, srR)
//            if (isHitR != null && srR.t < sr.t) {
//                //                    Counter.count("KDTree.InnerNode.hit.H.R");
//                sr.set(srR)
//                return true
//            }
//        } else {
//            //                Counter.count("KDTree.InnerNode.hit.LR.R");
//            val srR = Hit(sr)
//            val isHitR = right.hit(ray, srR)
//            if (isHitR != null && srR.t < sr.t) {
//                //                    Counter.count("KDTree.InnerNode.hit.H2.R");
//                sr.set(srR)
//                return true
//            }
//            val srL = Hit(sr)
//            val isHitL = left.hit(ray, srL)
//            if (isHitL != null && srL.t < sr.t) {
//                //                    Counter.count("KDTree.InnerNode.hit.H2.L");
//                sr.set(srL)
//                return true
//            }
//        }
//        //        }
//
//        return false
//
//        //        // prev
//        //        Hit srL = new Hit(sr);
//        //        boolean isHitL = left.hit(ray, srL);
//        //
//        ////        if (isHitL) {
//        ////            assert null != srL.getNormal();
//        ////        }
//        //
//        //        Hit srR = new Hit(sr);
//        //        boolean isHitR = right.hit(ray, srR);
//        //
//        ////        if (isHitR) {
//        ////            assert null != srR.getNormal();
//        ////        }
//        //
//        //        if (isHitL && isHitR) {
//        //            if (srL.getT() < srR.getT()) {
//        //                Counter.count("KDTree.InnerNode.hit.LR.L");
//        //                sr.set(srL);
//        //                return true;
//        //            } else {
//        //                Counter.count("KDTree.InnerNode.hit.LR.R");
//        //                sr.set(srR);
//        //                return true;
//        //            }
//        //        } else if (!isHitL && isHitR && srR.getT() < sr.getT()) {
//        //            Counter.count("KDTree.InnerNode.hit.R");
//        //            sr.set(srR);
//        //            return true;
//        //        } else if (isHitL && !isHitR && srL.getT() < sr.getT()) {
//        //            Counter.count("KDTree.InnerNode.hit.L");
//        //            sr.set(srL);
//        //            return true;
//        //        } else {
//        //            Counter.count("KDTree.InnerNode.hit.0");
//        //            return false;
//        //        }
//    }
//
//    fun hitX(ray: Ray, sr: Hit): Boolean {
//        if (!boundingBox.hit(ray)) {
//            Counter.count("KDTree.InnerNode.hit.bbox")
//            return false
//        }
//        Counter.count("KDTree.InnerNode.hit")
//        val hitL = left.boundingBox?.hitX(ray)
//        val hitR = right.boundingBox?.hitX(ray)
//        if (hitL != null && hitL.isHit && hitR != null && !hitR.isHit) {
//            Counter.count("KDTree.InnerNode.hit.L")
//            return left.hit(ray, sr) ?: false
//        } else if (hitL != null && !hitL.isHit && hitR != null && hitR.isHit) {
//            Counter.count("KDTree.InnerNode.hit.R")
//            return right.hit(ray, sr) ?: false
//        } else if (hitL != null && hitL.isHit && hitR != null && hitR.isHit) {
//            Counter.count("KDTree.InnerNode.hit.LR")
//            if (hitL.t0 < hitR.t0) {
//                var sr2 = Hit()
//                sr2.t = sr.t
//                val isHitL = left.hit(ray, sr2)
//                if (isHitL != null && null != sr2.normal) {
//                    sr.set(sr2)
//                    return true
//                } else {
//                    sr2 = Hit()
//                    sr2.t = sr.t
//                    val isHitR = right.hit(ray, sr2)
//                    if (isHitR != null && null != sr2.normal) {
//                        sr.set(sr2)
//                        return true
//                    } else {
//                        return false
//                    }
//                }
//            } else {
//                var sr2 = Hit()
//                sr2.t = sr.t
//                val isHitR = right.hit(ray, sr2)
//                if (isHitR != null && null != sr2.normal) {
//                    sr.set(sr2)
//                    return true
//                } else {
//                    sr2 = Hit()
//                    sr2.t = sr.t
//                    val isHitL = left.hit(ray, sr2)
//                    if (isHitL != null && null != sr2.normal) {
//                        sr.set(sr2)
//                        return true
//                    } else {
//                        return false
//                    }
//                }
//            }
//        } else {
//            Counter.count("KDTree.InnerNode.hit.0")
//            return false
//        }
//    }

    override fun size(): Int = left.size() + right.size()

    override fun toString(): String =
            ("Node ${size()} [ ${left.size()}, ${right.size()}] $boundingBox $split\n($left)\n($right)")

    override fun printBBoxes(incr: Int): String = buildString {
        for (i in 0 until incr) {
            append(" ")
        }
        append(size())
        append(" ")
        append(axis)
        append(" ")
        append(split)
        append(" ")
        append(boundingBox.p)
        append(" ")
        append(boundingBox.q)
        append("\n")
        append(left.printBBoxes(incr + 2))
        append(right.printBBoxes(incr + 2))
    }

    companion object {
        var hits = Histogram()
        var fails = Histogram()
    }
}
