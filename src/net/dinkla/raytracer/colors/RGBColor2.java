package net.dinkla.raytracer.colors;

import net.dinkla.raytracer.hits.Shade;

import static java.lang.Math.max;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 10.04.2010
 * Time: 15:25:54
 * To change this template use File | Settings | File Templates.
 */
public class RGBColor2 extends Color {

    static public final RGBColor2 BLACK = new RGBColor2(0, 0, 0);
    static public final RGBColor2 WHITE = new RGBColor2(1, 1, 1);
    static public final RGBColor2 RED = new RGBColor2(1, 0, 0);
    static public final RGBColor2 GREEN = new RGBColor2(0, 1, 0);
    static public final RGBColor2 BLUE = new RGBColor2(0, 0, 1);
    static public final RGBColor2 CLAMP_COLOR = new RGBColor2(1, 0, 0);

    public final double red, green, blue;

    public RGBColor2(double red, double green, double blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public RGBColor2(double v) {
        this.red = v;
        this.green = v;
        this.blue = v;
    }

    public RGBColor2 plus(RGBColor2 v) {
        return new RGBColor2(red + v.red, green + v.green, blue + v.blue);
    }

    public RGBColor2 mult(RGBColor2 v) {
        return new RGBColor2(red * v.red, green * v.green, blue * v.blue);
    }

    public RGBColor2 mult(double s) {
        return new RGBColor2(s * red, s * green, s * blue);
    }

    public RGBColor2 pow(double s) {
        return new RGBColor2((double) Math.pow(red, s), (double) Math.pow(green, s), (double) Math.pow(blue, s));
    }

    @Override
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

    public RGBBytes asBytes() {
        final byte r = (byte) (red * 255);
        final byte g = (byte) (green * 255);
        final byte b = (byte) (blue * 255);
        return new RGBBytes(r, g, b);
    }

    @Override
    public Color createFromInt(int rgb) {
        int rx = (rgb & 0x00ff0000);
        int gx = (rgb & 0x0000ff00);
        int bx = (rgb & 0x000000ff);

        final double r = ((rgb & 0x00ff0000) >> 16) / 255.0f;
        final double g = ((rgb & 0x0000ff00) >> 8) / 255.0f;
        final double b = (rgb & 0x000000ff) / 255.0f;
        return new RGBColor2(r, g, b);
    }

    public RGBColor2 clampToColor() {
        if (red > 1 || green > 1 || blue > 1) {
            return CLAMP_COLOR;
        } else {
            return this;
        }
    }

    public RGBColor2 maxToOne() {
        double maxValue = max(red, max(green, blue));
        if (maxValue > 1) {
            return this.mult(1/maxValue);
        } else {
            return this;
        }
    }

    @Override
    public Color plus(Color v) {
        if (v instanceof RGBColor2) {
            RGBColor2 rgb = (RGBColor2) v;
            return new RGBColor2(red + rgb.red, green + rgb.green, blue + rgb.blue);
        } else {
            return null;
        }

    }

    @Override
    public Color mult(Color v) {
        if (v instanceof RGBColor2) {
            RGBColor2 rgb = (RGBColor2) v;
            return new RGBColor2(red * rgb.red, green * rgb.green, blue * rgb.blue);
        } else {
            return null;
        }
    }

    static public Color getBlack() {
        return RGBColor2.BLACK;
    }

    static public Color getWhite() {
        return RGBColor2.WHITE;
    }

    static public Color getErrorColor() {
        return RGBColor2.RED;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RGBColor2)) {
            return false;
        } else {
            RGBColor2 e = (RGBColor2) obj;
            return (red == e.red && green == e.green && blue == e.blue);
        }
    }

    @Override
    public String toString() {
        return "(" + red + "," + green + "," + blue + ")";
    }

}
