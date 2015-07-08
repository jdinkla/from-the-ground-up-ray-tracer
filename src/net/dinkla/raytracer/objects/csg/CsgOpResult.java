package net.dinkla.raytracer.objects.csg;

import net.dinkla.raytracer.math.Interval;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 28.06.2010
 * Time: 21:42:21
 * To change this template use File | Settings | File Templates.
 */
public class CsgOpResult<C extends Comparable> {

    final Interval<C> a;
    final Interval<C> b;

    public CsgOpResult() {
        a = null;
        b = null;
    }

    public CsgOpResult(final Interval<C> a) {
        this.a = a;
        b = null;
    }

    public CsgOpResult(final Interval<C> a, final Interval<C> b) {
        this.a = a;
        this.b = b;
    }

    public static CsgOpResult union(Interval p, Interval q) {
        if (p.isDisjointTo(q)) {
            return new CsgOpResult(p, q);
        } else {
            int i = p.p.compareTo(q.p);
            int j = p.q.compareTo(q.q);
            return new CsgOpResult(new Interval(i <= 0 ? p.p : q.p , j <= 0 ? q.q : p.q  ));
        }
    }

    public static CsgOpResult intersect(Interval p, Interval q) {
        if (p.isDisjointTo(q)) {
            return new CsgOpResult();
        } else {
            int i = p.p.compareTo(q.p);
            int j = p.q.compareTo(q.q);
            return new CsgOpResult(new Interval(i <= 0 ? q.p : p.p , j <= 0 ? p.q : q.q  ));
        }
    }

    public static CsgOpResult difference(Interval p, Interval q) {
        if (p.isDisjointTo(q)) {
            return new CsgOpResult(p);
        } else if (q.fullyOverlaps(p)) {
            return new CsgOpResult();
        } else if (p.fullyOverlaps(q)) {
            return new CsgOpResult(new Interval(p.p, q.p), new Interval(q.q, p.q));
        } else if (p.p.compareTo(q.p) <= 0) {
            return new CsgOpResult(new Interval(p.p, q.p));
        } else {
            return new CsgOpResult(new Interval(q.q, p.q));
        }
    }

}
