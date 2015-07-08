package net.dinkla.raytracer.colors;

import net.dinkla.raytracer.hits.Shade;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 10.04.2010
 * Time: 15:24:46
 * To change this template use File | Settings | File Templates.
 * TODO: Kann man Color besser kapseln? 
 */
abstract public class Color {

    static public Color black;
    static public Color white;
    static public Color error;

    abstract public Color getColor(Shade sr);

    abstract public Color plus(Color v);

    abstract public Color mult(Color v);

    abstract public Color mult(float s);

    abstract public Color pow(float s);

    abstract public int asInt();

    abstract public RGBBytes asBytes();

    abstract public Color clampToColor();

    abstract public Color maxToOne();

    static public Color getBlack() {
        return black;
    }

    static public Color getWhite() {
        return white;
    }

    static public Color getErrorColor() {
        return error;
    }

    abstract public Color createFromInt(int rgb);

}
