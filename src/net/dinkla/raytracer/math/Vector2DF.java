package net.dinkla.raytracer.math;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 10.04.2010
 * Time: 15:17:35
 * To change this template use File | Settings | File Templates.
 */
public class Vector2DF extends Element2D {

    public Vector2DF(final float x, final float y) {
        super(x, y);
    }

    public Vector2DF plus(final Vector2DF v) {
        return new Vector2DF(x + v.x, y + v.y);
    }

    public Vector2DF minus(final Vector2DF v) {
        return new Vector2DF(x - v.x, y - v.y);
    }

    public Vector2DF mult(final float s) {
        return new Vector2DF(s * x, s * y);
    }

    public float dot(final Vector2DF v)  {
        return x * v.x + y * v.y;
    }

    public float dot(final Normal v)  {
        return x * v.x + y * v.y;
    }

    public Vector2DF normalize() {
        final float l = length();
        return new Vector2DF(x / l, y / l);
    }

}