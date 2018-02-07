package net.dinkla.raytracer.colors;

import net.dinkla.raytracer.hits.Shade;

import static java.lang.Math.max;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 10.04.2010
 * Time: 15:24:46
 * To change this template use File | Settings | File Templates.
 * TODO: Kann man Color besser kapseln? 
 */
public class Color {

    static public final Color BLACK = new Color(0, 0, 0);
    static public final Color WHITE = new Color(1, 1, 1);
    static public final Color RED = new Color(1, 0, 0);
    static public final Color GREEN = new Color(0, 1, 0);
    static public final Color BLUE = new Color(0, 0, 1);
    static public final Color CLAMP_COLOR = new Color(1, 0, 0);

    static public Color black;
    static public Color white;
    static public Color error;

    static public Color getBlack() {
        return Color.BLACK;
    }

    static public Color getWhite() {
        return Color.WHITE;
    }

    static public Color getErrorColor() {
        return Color.RED;
    }

    public final double red;
    public final double green;
    public final double blue;

    public Color(double red, double green, double blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public Color(double v) {
        this.red = v;
        this.green = v;
        this.blue = v;
    }

    public Color plus(Color v) {
        return new Color(red + v.red, green + v.green, blue + v.blue);
    }

    public Color mult(Color v) {
        return new Color(red * v.red, green * v.green, blue * v.blue);
    }

    public Color mult(double s) {
        return new Color(s * red, s * green, s * blue);
    }

    public Color pow(double s) {
        return new Color((double) Math.pow(red, s), (double) Math.pow(green, s), (double) Math.pow(blue, s));
    }

    public Color getColor(Shade sr) {
        return this;
    }

    /**
     *
     * @return
     */
    public int asInt() {
        final int r = (int) (red * 255);
        final int g = (int) (green * 255);
        final int b = (int) (blue * 255);
        return r << 16 | g << 8 | b;
    }

    public Color createFromInt(int rgb) {
        int rx = (rgb & 0x00ff0000);
        int gx = (rgb & 0x0000ff00);
        int bx = (rgb & 0x000000ff);

        final double r = ((rgb & 0x00ff0000) >> 16) / 255.0f;
        final double g = ((rgb & 0x0000ff00) >> 8) / 255.0f;
        final double b = (rgb & 0x000000ff) / 255.0f;
        return new Color(r, g, b);
    }

    public Color clampToColor() {
        if (red > 1 || green > 1 || blue > 1) {
            return CLAMP_COLOR;
        } else {
            return this;
        }
    }

    public Color maxToOne() {
        double maxValue = max(red, max(green, blue));
        if (maxValue > 1) {
            return this.mult(1/maxValue);
        } else {
            return this;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Color)) {
            return false;
        } else {
            Color e = (Color) obj;
            return (red == e.red && green == e.green && blue == e.blue);
        }
    }

    @Override
    public String toString() {
        return "(" + red + "," + green + "," + blue + ")";
    }
}
