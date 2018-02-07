package net.dinkla.raytracer.colors;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 26.04.2010
 * Time: 09:30:33
 * To change this template use File | Settings | File Templates.
 */
public class ColorAccumulator<C extends Color> {

    protected Color aggregated;
    protected int count;

    public ColorAccumulator() {
        aggregated = C.BLACK;
        count = 0;
    }

    public void plus(Color color) {
        aggregated = aggregated.plus(color);
        count++;
    }

    public Color getAverage() {
        Color result;
        if (count > 0) {
            result = aggregated.mult(1.0 / count);
        } else {
            result = C.BLACK;
        }
        return result;
    }

}
