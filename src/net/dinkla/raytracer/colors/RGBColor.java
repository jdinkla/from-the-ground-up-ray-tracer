package net.dinkla.raytracer.colors;

import net.dinkla.raytracer.hits.Shade;

import static java.lang.Math.max;

public class RGBColor extends Color {

    static public final RGBColor BLACK = new RGBColor(0, 0, 0);
    static public final RGBColor WHITE = new RGBColor(1, 1, 1);
    static public final RGBColor RED = new RGBColor(1, 0, 0);
    static public final RGBColor GREEN = new RGBColor(0, 1, 0);
    static public final RGBColor BLUE = new RGBColor(0, 0, 1);
    static public final RGBColor CLAMP_COLOR = new RGBColor(1, 0, 0);

    public final double red, green, blue;

    public RGBColor(double red, double green, double blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public RGBColor(double v) {
        this.red = v;
        this.green = v;
        this.blue = v;
    }

    public RGBColor plus(RGBColor v) {
        return new RGBColor(red + v.red, green + v.green, blue + v.blue);
    }

    public RGBColor mult(RGBColor v) {
        return new RGBColor(red * v.red, green * v.green, blue * v.blue);
    }

    public RGBColor mult(double s) {
        return new RGBColor(s * red, s * green, s * blue);
    }

    public RGBColor pow(double s) {
        return new RGBColor((double) Math.pow(red, s), (double) Math.pow(green, s), (double) Math.pow(blue, s));
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

    @Override
    public Color createFromInt(int rgb) {
        int rx = (rgb & 0x00ff0000);
        int gx = (rgb & 0x0000ff00);
        int bx = (rgb & 0x000000ff);

        final double r = ((rgb & 0x00ff0000) >> 16) / 255.0f;
        final double g = ((rgb & 0x0000ff00) >> 8) / 255.0f;
        final double b = (rgb & 0x000000ff) / 255.0f;
        return new RGBColor(r, g, b);
    }

    public RGBColor clampToColor() {
        if (red > 1 || green > 1 || blue > 1) {
            return CLAMP_COLOR;
        } else {
            return this;
        }
    }

    public RGBColor maxToOne() {
        double maxValue = max(red, max(green, blue));
        if (maxValue > 1) {
            return this.mult(1/maxValue);
        } else {
            return this;
        }
    }

    @Override
    public Color plus(Color v) {
        if (v instanceof RGBColor) {
            RGBColor rgb = (RGBColor) v;
            return new RGBColor(red + rgb.red, green + rgb.green, blue + rgb.blue);
        } else {
            return null;
        }

    }

    @Override
    public Color mult(Color v) {
        if (v instanceof RGBColor) {
            RGBColor rgb = (RGBColor) v;
            return new RGBColor(red * rgb.red, green * rgb.green, blue * rgb.blue);
        } else {
            return null;
        }
    }

    static public Color getBlack() {
        return RGBColor.BLACK;
    }

    static public Color getWhite() {
        return RGBColor.WHITE;
    }

    static public Color getErrorColor() {
        return RGBColor.RED;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RGBColor)) {
            return false;
        } else {
            RGBColor e = (RGBColor) obj;
            return (red == e.red && green == e.green && blue == e.blue);
        }
    }

    @Override
    public String toString() {
        return "(" + red + "," + green + "," + blue + ")";
    }

}
