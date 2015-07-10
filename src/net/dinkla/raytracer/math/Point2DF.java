package net.dinkla.raytracer.math;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 10.04.2010
 * Time: 15:17:43
 * To change this template use File | Settings | File Templates.
 */
public class Point2DF extends Element2D {

    public Point2DF(float x, float y) {
        super(x, y);
    }

    public Vector2DF plus(final Point2DF v) {
        return new Vector2DF(x + v.x, y + v.y);
    }

    public Point2DF plus(final Vector2DF v) {
        return new Point2DF(x + v.x, y + v.y);
    }

    public Vector2DF minus(final Point2DF v) {
        return new Vector2DF(x - v.x, y - v.y);
    }

    public Point2DF minus(final Vector2DF v) {
        return new Point2DF(x - v.x, y - v.y);
    }

}