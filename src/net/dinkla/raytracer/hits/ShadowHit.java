package net.dinkla.raytracer.hits;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 15.05.2010
 * Time: 11:07:22
 * To change this template use File | Settings | File Templates.
 */
public class ShadowHit {

    /**
     * The distance. Initially set to <code>Float.MAX_VALUE</code>.
     */
    public double t;

    public static ShadowHit createMax() {
        ShadowHit f = new ShadowHit();
        f.setMaxT();
        return f;
    }

    /**
     * Constructor.
     */
    public ShadowHit() {
        this.t = Double.MAX_VALUE;
    }

    public ShadowHit(final double t) {
        this.t = t;
    }

    public double getT() {
        return t;
    }

    public void setT(final double t) {
        this.t = t;
    }

    public void setMaxT() {
        this.t = Double.MAX_VALUE;
    }
}
