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
    public float t;

    public static ShadowHit createMax() {
        ShadowHit f = new ShadowHit();
        f.setMaxT();
        return f;
    }

    /**
     * Constructor.
     */
    public ShadowHit() {
        this.t = Float.MAX_VALUE;
    }

    public ShadowHit(final float t) {
        this.t = t;
    }

    public float getT() {
        return t;
    }

    public void setT(final float t) {
        this.t = t;
    }

    public void setMaxT() {
        this.t = Float.MAX_VALUE;
    }
}
