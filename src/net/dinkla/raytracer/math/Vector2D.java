package net.dinkla.raytracer.math;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 10.04.2010
 * Time: 15:17:35
 */
public class Vector2D extends Element2D {

    public Vector2D(final float x, final float y) {
        super(x, y);
    }

    public Vector2D(final double x, final double y) {
        super(x, y);
    }

    public Vector2D plus(final Vector2D v) {
        return new Vector2D(x + v.x, y + v.y);
    }

    public Vector2D minus(final Vector2D v) {
        return new Vector2D(x - v.x, y - v.y);
    }

    public Vector2D mult(final float s) {
        return new Vector2D(s * x, s * y);
    }

    public float dot(final Vector2D v)  {
        return x * v.x + y * v.y;
    }

    public float dot(final Normal v)  {
        return x * v.x + y * v.y;
    }

    public Vector2D normalize() {
        final float l = length();
        return new Vector2D(x / l, y / l);
    }

}