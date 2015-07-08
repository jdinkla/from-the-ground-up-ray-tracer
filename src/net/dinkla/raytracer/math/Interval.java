package net.dinkla.raytracer.math;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 16.06.2010
 * Time: 20:10:05
 * To change this template use File | Settings | File Templates.
 */
public class Interval<C extends Comparable> {

    public final C p;
    public final C q;

    public Interval(C p, C q) {
        assert null != p || null != q;
        assert null == p || null == q || p.compareTo(q) <= 0;
        this.p = p;
        this.q = q;
    }

    public boolean contains(C c) {
        int i = p.compareTo(c);
        int j = c.compareTo(q);
        return i <= 0 && j <= 0;
    }

    public boolean isDisjointTo(Interval<C> i) {
        return q.compareTo(i.p) < 0 || i.q.compareTo(p) < 0; 
    }

    public boolean partialOverlaps(Interval<C> i) {
        return !isDisjointTo(i);
    }

    public boolean fullyOverlaps(Interval<C> i) {
        return p.compareTo(i.p) <= 0 && q.compareTo(i.q) >= 0;
    }

}
