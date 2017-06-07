package net.dinkla.raytracer.math;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 10.04.2010
 * Time: 15:17:43
 * To change this template use File | Settings | File Templates.
 */
public class Point2D extends Element2D {

    public Point2D(float x, float y) {
        super(x, y);
    }

    public Point2D plus(final Vector2D v) {
        return new Point2D(x + v.x, y + v.y);
    }

    public Point2D minus(final Vector2D v) {
        return new Point2D(x - v.x, y - v.y);
    }

}